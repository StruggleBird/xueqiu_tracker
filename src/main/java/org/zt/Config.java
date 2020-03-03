/**
 * 
 */
package org.zt;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.io.FileUtils;
import com.alibaba.fastjson.JSON;

/**
 * @author Ternence
 * @create 2015年11月1日
 */
public class Config implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer interval;

    private Boolean preview;

    private Float floatVal;

    private String[] urls;
    
    private Boolean testMode;

    private String cookie;
    
    private String mailToken;
    
    private String mailAddr;
    
    private static Config instance;
    
    
    static {
      instance = loadConfig();
    }
    
    public  static Config instance() {
      return instance;
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
     * @return the interval
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    /**
     * @return the preview
     */
    public Boolean getPreview() {
        return preview;
    }

    /**
     * @param preview the preview to set
     */
    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    /**
     * @return the floatVal
     */
    public Float getFloatVal() {
        return floatVal;
    }

    /**
     * @param floatVal the floatVal to set
     */
    public void setFloatVal(Float floatVal) {
        this.floatVal = floatVal;
    }

    /**
     * @return the urls
     */
    public String[] getUrls() {
        return urls;
    }

    /**
     * @param urls the urls to set
     */
    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    /**
     * @return the testMode
     */
    public Boolean getTestMode() {
        return testMode;
    }

    /**
     * @param testMode the testMode to set
     */
    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getMailToken() {
      return mailToken;
    }

    public void setMailToken(String mailToken) {
      this.mailToken = mailToken;
    }

    public String getMailAddr() {
      return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
      this.mailAddr = mailAddr;
    }
    
    
}
