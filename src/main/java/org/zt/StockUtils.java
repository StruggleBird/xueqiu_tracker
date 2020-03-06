package org.zt;

import java.util.LinkedHashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class StockUtils {

  public static Map<String, Float> extractStockInfo(Map<String, Object> data) {
    Map<String, Float> result = new LinkedHashMap<String, Float>();
    for (Object obj : data.values()) {
      JSONObject jsonObject = (JSONObject) obj;
      JSONArray stocks = jsonObject.getJSONArray("stocks");
      for (int i = 0; i < stocks.size(); i++) {
        JSONObject stock = (JSONObject) stocks.get(i);
        String stockName = stock.get("stock_name") + "";
        result.put(stockName, Float.parseFloat(stock.get("weight") + ""));
      }

    }
    return result;
  }
  
}
