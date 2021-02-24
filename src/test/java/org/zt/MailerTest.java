package org.zt;

import java.util.Map;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

class MailerTest {

  @Test
  void testSend() {
    

    String json =
        "{\"电子\":{\"color\":\"#53b3e3\",\"name\":\"电子\",\"stocks\":[{\"proactive\":false,\"segment_color\":\"#53b3e3\",\"segment_id\":9899604,\"segment_name\":\"电子\",\"stock_id\":1001301,\"stock_name\":\"风华高科\",\"stock_symbol\":\"SZ000636\",\"textname\":\"undefined(SZ000636)\",\"url\":\"/S/SZ000636\",\"volume\":0.07830918,\"weight\":100}],\"weight\":100}}";
    Map<String, Object> data = JSON.parseObject(json, Map.class);
    Mailer.send(data,data, "https://xueqiu.com/P/ZH2099343");
    
  }
  
  
  @Test
  void testSend2() {

    String json =
        "{电子:{\"color\":\"#53b3e3\",\"name\":\"电子\",\"weight\":37.52,\"stocks\":[{\"volume\":0.03887876,\"textname\":\"undefined(SZ300014)\",\"stock_name\":\"亿纬锂能\",\"stock_symbol\":\"SZ300014\",\"segment_color\":\"#53b3e3\",\"weight\":37.52,\"proactive\":false,\"segment_name\":\"电子\",\"segment_id\":9414379,\"stock_id\":1003146,\"url\":\"/S/SZ300014\"}]},医药生物:{\"color\":\"#2bb98f\",\"name\":\"医药生物\",\"weight\":62.13,\"stocks\":[{\"volume\":0.04207355,\"textname\":\"undefined(SZ300573)\",\"stock_name\":\"兴齐眼药\",\"stock_symbol\":\"SZ300573\",\"segment_color\":\"#2bb98f\",\"weight\":62.13,\"proactive\":false,\"segment_name\":\"医药生物\",\"segment_id\":9237186,\"stock_id\":1027750,\"url\":\"/S/SZ300573\"}]}}";
    Map<String, Object> data = JSON.parseObject(json, Map.class);
    Mailer.send(data,data, "https://xueqiu.com/P/ZH2099343");
    
  }

}
