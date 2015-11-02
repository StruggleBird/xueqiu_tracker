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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import com.alibaba.fastjson.JSON;

/**
 * @author Ternence
 * @create 2015年11月1日
 */
public class Monitor {

    private static Map<String, Object> cache = new HashMap<String, Object>();
    private static Config config;
    private static Component comp = null;
    private static TrayIcon trayIcon;

    public static void main(String[] args) throws IllegalStateException, IOException, InterruptedException {

        initTray();
        config = loadConfig();

        for (int i = 0;; i++) {
            String url = config.getUrls()[i % config.getUrls().length];
            checkData(url);
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
            Image image = Toolkit.getDefaultToolkit().getImage(Monitor.class.getResource("/META-INF/icon-32.png"));

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
     * 
     * @return
     * @create 2015年11月1日
     */
    private static Config loadConfig() {
        File file = new File("config.json");
        String fileContent;
        try {
            fileContent = FileUtils.readFileToString(file);
            return JSON.parseObject(fileContent, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param url
     * @create 2015年11月1日
     */
    private static void checkData(String url) {
        if (!config.getTestMode() && !isOpen()) {
            System.out.println("休市中....");
            return;
        };
        System.out.println("check url:" + url + " -" + new Date());
        String content = getContent(url);
        Map<String, Object> data = getData(content);
        if (data != null) {
            for (String key : data.keySet()) {
                System.out.println(key + ":" + data.get(key));
            }
            System.out.println("--------------------------------------------------------------------------");

            // 如果已经有缓存，对比和缓存中的是否一致，不一致发出提醒

            if (cache.containsKey(url)) {
                Map<String, Object> oriData = (Map<String, Object>) cache.get(url);
                if (!oriData.equals(data)) {
                    notify(url);
                }
            }

            cache.put(url, data);
        }
    }

    /**
     * @param url
     * @create 2015年11月1日
     */
    private static void notify(String url) {
        System.out.println("发现组合有变化,触发提醒功能！");
        try {
            trayIcon.displayMessage("组合变更提醒", url + "的组合有变动", MessageType.INFO);
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
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
        Pattern treePattern = Pattern.compile("SNB.cubeTreeData = (.+?);");
        Matcher matcher = treePattern.matcher(content);
        if (matcher.find()) {
            String stockTree = matcher.group(1);
            Map<String, Object> map = JSON.parseObject(stockTree, Map.class);
            return map;
        }

        return null;
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
            headers[0] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
            headers[1] = new BasicHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36 LBBROWSER");

            headers[2] = new BasicHeader("Cookie", config.getCookie());
            httpgets.setHeaders(headers);
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instreams = entity.getContent();
                String content = convertStreamToString(instreams);
                httpgets.abort();
                // setCookieStore(response);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return null;
    }

    public static void setCookieStore(HttpResponse httpResponse) {
        System.out.println("----setCookieStore");
        BasicCookieStore cookieStore = new BasicCookieStore();
        // JSESSIONID
        String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
        String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
        System.out.println("JSESSIONID:" + JSESSIONID);
        // 新建一个Cookie
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
        cookie.setVersion(0);
        cookie.setDomain("127.0.0.1");
        cookie.setPath("/CwlProClient");
        // cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
        // cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
        // cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
        // cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
        cookieStore.addCookie(cookie);
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    private static class ExitItemActionListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            System.exit(0);
        }
    }

    private static class TrayIconMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON1) {
                comp.setVisible(true);
            }
        }
    }
}
