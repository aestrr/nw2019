package com.nwhacks.myapplication.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JSONToReceiptParser {

    public static Receipt parseJson(JSONObject jsonObject) throws JSONException {
       String companyName = jsonObject.getString("companyName");
       String transactionDateString = jsonObject.getString("transactionDate");
       SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
       Date transactionDate = new Date();
       try {
           transactionDate = format.parse(transactionDateString);
       }
       catch (ParseException e){
           e.printStackTrace();
        }
       double totalCost = jsonObject.getDouble("totalCost");
       JSONArray jsonArray = jsonObject.getJSONArray("purchasedItems");
       List<Product> items = new ArrayList<>();
       for (int i = 0; i < jsonArray.length(); i++) {
           JSONObject product = jsonArray.getJSONObject(i);
           String productName = product.getString("productName");
           double productCost = product.getDouble("productCost");
           items.add(new Product(productName, productCost));
       }

       return new Receipt(companyName, totalCost, items, transactionDate);

    }

}
