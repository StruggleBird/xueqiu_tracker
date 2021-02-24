package org.zt;

import java.util.Map;
import org.junit.Assert;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

class MonitorTest {

  @Test
 public void dataEquals() {
    String json =
        "{\"电子\":{\"color\":\"#53b3e3\",\"name\":\"电子\",\"stocks\":[{\"proactive\":false,\"segment_color\":\"#53b3e3\",\"segment_id\":9899604,\"segment_name\":\"电子\",\"stock_id\":1001301,\"stock_name\":\"风华高科\",\"stock_symbol\":\"SZ000636\",\"textname\":\"undefined(SZ000636)\",\"url\":\"/S/SZ000636\",\"volume\":0.07830918,\"weight\":100}],\"weight\":100}}";
    Map<String, Object> data = JSON.parseObject(json, Map.class);
    
    String json2 =
        "{\"电子\":{\"color\":\"#53b3e3\",\"name\":\"电子\",\"stocks\":[{\"proactive\":false,\"segment_color\":\"#53b3e3\",\"segment_id\":9899604,\"segment_name\":\"电子\",\"stock_id\":1001301,\"stock_name\":\"风华高科\",\"stock_symbol\":\"SZ000636\",\"textname\":\"undefined(SZ000636)\",\"url\":\"/S/SZ000636\",\"volume\":0.07830918,\"weight\":100}],\"weight\":100}}";
    Map<String, Object> data2 = JSON.parseObject(json2, Map.class);
    
    boolean dataEquals = Monitor.dataEquals(data, data2);
    Assert.assertTrue(dataEquals);
    
    
    
    //浮动测试--------------------
    String json3 =
        "{\"电子\":{\"color\":\"#53b3e3\",\"name\":\"电子\",\"stocks\":[{\"proactive\":false,\"segment_color\":\"#53b3e3\",\"segment_id\":9899604,\"segment_name\":\"电子\",\"stock_id\":1001301,\"stock_name\":\"风华高科\",\"stock_symbol\":\"SZ000636\",\"textname\":\"undefined(SZ000636)\",\"url\":\"/S/SZ000636\",\"volume\":0.07830918,\"weight\":90}],\"weight\":90}}";
    Map<String, Object> data3 = JSON.parseObject(json3, Map.class);
    Assert.assertFalse(Monitor.dataEquals(data, data3));
    
    
    String json31 =
        "{\"电子\":{\"color\":\"#53b3e3\",\"name\":\"电子\",\"stocks\":[{\"proactive\":false,\"segment_color\":\"#53b3e3\",\"segment_id\":9899604,\"segment_name\":\"电子\",\"stock_id\":1001301,\"stock_name\":\"风华高科\",\"stock_symbol\":\"SZ000636\",\"textname\":\"undefined(SZ000636)\",\"url\":\"/S/SZ000636\",\"volume\":0.07830918,\"weight\":98}],\"weight\":98}}";
    Map<String, Object> data31 = JSON.parseObject(json31, Map.class);
    Assert.assertTrue(Monitor.dataEquals(data, data31));
    //---------------------------
    
    
    String json4 =
        "{电子:{\"color\":\"#53b3e3\",\"name\":\"电子\",\"weight\":37.52,\"stocks\":[{\"volume\":0.03887876,\"textname\":\"undefined(SZ300014)\",\"stock_name\":\"亿纬锂能\",\"stock_symbol\":\"SZ300014\",\"segment_color\":\"#53b3e3\",\"weight\":37.52,\"proactive\":false,\"segment_name\":\"电子\",\"segment_id\":9414379,\"stock_id\":1003146,\"url\":\"/S/SZ300014\"}]}}";
    Map<String, Object> data4 = JSON.parseObject(json4, Map.class);
    Assert.assertFalse(Monitor.dataEquals(data, data4));
    
    
    String json5 =
        "{电子:{\"color\":\"#53b3e3\",\"name\":\"电子\",\"weight\":37.52,\"stocks\":[{\"volume\":0.03887876,\"textname\":\"undefined(SZ300014)\",\"stock_name\":\"亿纬锂能\",\"stock_symbol\":\"SZ300014\",\"segment_color\":\"#53b3e3\",\"weight\":37.52,\"proactive\":false,\"segment_name\":\"电子\",\"segment_id\":9414379,\"stock_id\":1003146,\"url\":\"/S/SZ300014\"}]},医药生物:{\"color\":\"#2bb98f\",\"name\":\"医药生物\",\"weight\":62.13,\"stocks\":[{\"volume\":0.04207355,\"textname\":\"undefined(SZ300573)\",\"stock_name\":\"兴齐眼药\",\"stock_symbol\":\"SZ300573\",\"segment_color\":\"#2bb98f\",\"weight\":62.13,\"proactive\":false,\"segment_name\":\"医药生物\",\"segment_id\":9237186,\"stock_id\":1027750,\"url\":\"/S/SZ300573\"}]}}";
    Map<String, Object> data5 = JSON.parseObject(json5, Map.class);
    Assert.assertFalse(Monitor.dataEquals(data, data5));
    Assert.assertFalse(Monitor.dataEquals(data5, data));
    Assert.assertFalse(Monitor.dataEquals(data5, data4));
    Assert.assertTrue(Monitor.dataEquals(data5, data5));
    
  }

}
