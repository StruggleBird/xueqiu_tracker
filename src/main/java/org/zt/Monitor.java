/**
 * 
 */
package org.zt;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Ternence
 * @create 2015年11月1日
 */
public class Monitor {

  private static Map<String, Object> cache = new HashMap<String, Object>();
  private static Config config = Config.instance();
  private static Component comp = null;
  private static TrayIcon trayIcon;

  public static void main(String[] args)
      throws IllegalStateException, IOException, InterruptedException {

    initTray();

    while (true) {
      for (int i = 0; i < config.getUrls().length; i++) {
        String url = config.getUrls()[i];
        checkData(url);
      }
      Thread.sleep(TimeUnit.SECONDS.toMillis(config.getInterval()));
    }

  }

  /**
   * 
   * @create 2015年11月1日
   */
  private static void initTray() {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      Image image =
          Toolkit.getDefaultToolkit().getImage(Monitor.class.getResource("/META-INF/icon-32.png"));

      PopupMenu popupMenu = new PopupMenu();
      MenuItem exitItem = new MenuItem("Exit");

      exitItem.addActionListener(new ExitItemActionListener());

      popupMenu.add(exitItem);
      trayIcon = new TrayIcon(image, "雪球", popupMenu);
      trayIcon.addMouseListener(new TrayIconMouseListener());
      try {
        tray.add(trayIcon);
      } catch (AWTException e) {
        System.err.println(e);
      }
    }
  }



  /**
   * @param url
   * @create 2015年11月1日
   */
  private static void checkData(String url) {
    if (!config.getTestMode() && !isOpen()) {
      Logger.log("休市中....");
      return;
    } ;
    Logger.log("check url:" + url);
    String content = getContent(url);
    Logger.debug("响应是否有内容："+(content!=null&& content.length()!=0));
    Map<String, Object> data = getData(content);
    if (data != null) {
      Map<String, Object> dataInfoMap = (Map<String, Object>) data.get("data");
      Logger.log("组合信息：" + StockUtils.extractStockInfo(dataInfoMap));
      Logger.log("--------------------------------------------------------------------------");

      // 如果已经有缓存，对比和缓存中的是否一致，不一致发出提醒

      if (cache.containsKey(url)) {
        Map<String, Object> oriData = (Map<String, Object>) cache.get(url);
        if (!dataEquals(oriData, dataInfoMap)) {
          notify(oriData,data, url);
        }
      }

      cache.put(url, dataInfoMap);
    }
  }

  public static boolean dataEquals(Map<String, Object> oriData, Map<String, Object> data) {
    if (oriData.size() != data.size()) {
      return false;
    }
    
    Map<String, Float> oriMap = StockUtils.extractStockInfo(oriData);
    Map<String, Float> newMap = StockUtils.extractStockInfo(data);
    
    for (String key : oriMap.keySet()) {
      float weight = oriMap.get(key);
      if (!newMap.containsKey(key)) {
        return false;
      }

      Float weightNew = newMap.get(key);
      if (Math.abs(weight - weightNew) > config.getFloatVal()) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param data
 * @param data 
   * @param url
   * @create 2015年11月1日
   */
  private static void notify(Map<String, Object> oriData, Map<String, Object> data, String url) {
    Logger.log("发现组合有变化,触发提醒功能，最新组合信息：" + JSON.toJSONString(data));
    try {
      Mailer.send(oriData,data, url);
      trayIcon.displayMessage("组合变更提醒", url + "的组合有变动", MessageType.INFO);
      Desktop.getDesktop().browse(new URI(url));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  /**
   * 检查是否已开市
   * 
   * @create 2015年11月1日
   */
  private static boolean isOpen() {
    Date now = new Date();
    if (now.getHours() < 9 || now.getHours() >= 15) {
      return false;
    }

    return true;
  }

  /**
   * @param content
   * @return
   * @create 2015年11月1日
   */
  private static Map<String, Object> getData(String content) {
    if (content == null) {
      return null;
    }
    
    Map<String, Object> map = new HashMap<>();
    
    
    //查找组合内容
    Pattern treePattern = Pattern.compile("SNB.cubeTreeData = (.+?);");
    Matcher matcher = treePattern.matcher(content);
    if (matcher.find()) {
      String stockTree = matcher.group(1);
      // Logger.log(stockTree);
      Map<String, Object> stockData = JSON.parseObject(stockTree, Map.class);
      map.put("data", stockData);
    }else {
    	return null;
    }
    
    //查找组合标题
    treePattern = Pattern.compile("SNB.cubeInfo = (.+?);");
    matcher = treePattern.matcher(content);
    if (matcher.find()) {
      String stockInfo = matcher.group(1);
      // Logger.log(stockTree);
      JSONObject jsonObject = JSON.parseObject(stockInfo);
      map.put("name", jsonObject.getString("name"));
    }

    return map;
  }

  /**
   * @return
   * @throws IOException
   * @throws ClientProtocolException
   * @create 2015年11月1日
   */
  private static String getContent(String url) {
    // 创建HttpClient实例
    CloseableHttpClient httpclient = HttpClients.createDefault();


    try {
      // 创建Get方法实例
      HttpGet httpgets = new HttpGet(url);
      Header[] headers = new BasicHeader[3];
      headers[0] = new BasicHeader("Content-Type", "text/html; charset=utf-8");
      headers[1] = new BasicHeader("User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36 LBBROWSER");

      headers[2] = new BasicHeader("Cookie", config.getCookie());
      httpgets.setHeaders(headers);
      RequestConfig requestConfig =
          RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(3000).build();
      httpgets.setConfig(requestConfig);
      HttpResponse response = httpclient.execute(httpgets);
      HttpEntity entity = response.getEntity();
      Logger.debug("响应码："+response.getStatusLine().getStatusCode());
      if (entity != null) {
        InputStream instreams = entity.getContent();

        String content = IOUtils.toString(instreams, "UTF-8");
        httpgets.abort();
        // setCookieStore(response);
        return content;
      }
    } catch (Exception e) {
      Logger.log("请求异常：" + e.getMessage());
    } finally {
      try {
        httpclient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }



    return null;
  }


  private static class ExitItemActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      System.exit(0);
    }
  }

  private static class TrayIconMouseListener extends MouseAdapter {
    public void mousePressed(MouseEvent me) {
      if (me.getButton() == MouseEvent.BUTTON1) {
//        comp.setVisible(true);
      }
    }
  }
}
