package com.nwhacks.myapplication;

import android.util.SparseArray;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrStaticProcessor {

    public static JSONObject parseDetectedItems(SparseArray<TextBlock> blocks) {
        int nTextBlocks = blocks.size();
        if (nTextBlocks < 1) {
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String companyName = blocks.valueAt(0).getValue();

        Date transactionDate = getTransactionDate(blocks);

        String strTransactionDate = dateFormat.format(transactionDate);
        Double totalCost = getTotalCost(blocks);
        JSONArray purchasedItems = getPurchasedItems(blocks);

        JSONObject receipt = new JSONObject();

        try {
            receipt.put("companyName", companyName);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        try {
            receipt.put("transactionDate", strTransactionDate);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            receipt.put("totalCost", totalCost);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        try {
            receipt.put("purchasedItems", purchasedItems);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return receipt;
    }

    public static Date getTransactionDate(SparseArray<TextBlock> blocks) {
        String dateTemplate = "\\d{2}\\/\\d{2}\\/\\d{2,4}";
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            for (int li=0; li < block.size(); li++) {
                List<?extends Text> line = block.get(li).getComponents();
                for (int ei=0; ei < line.size(); ei++) {
                    Text element = line.get(ei);
                    String word = element.getValue();
                    if (Pattern.matches(word, dateTemplate)) {
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyyy");
                        Date parsedDate;
                        try {
                            parsedDate = format.parse(word);
                        } catch (ParseException e) {
                            parsedDate = new Date();
                        }
                        if (parsedDate != null) {
                            return parsedDate;
                        }
                    }
                }
            }
        }
        System.out.println("Date not found on receipt");
        return new Date();
    }

    public static Double getTotalCost(SparseArray<TextBlock> blocks) {
        String costTemplate = "\\$\\d+\\.\\d{2}";
        Pattern costPattern = Pattern.compile(costTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            for (int li=0; li < block.size(); li++) {
                List<?extends Text> line = block.get(li).getComponents();
                for (int ei=0; ei < line.size(); ei++) {
                    Text element = line.get(ei);
                    String word = element.getValue();
                    if (Pattern.matches(word, costTemplate)) {
                        Matcher matcher = costPattern.matcher(word);
                        return Double.parseDouble(matcher.group(0));
                    }
                }
            }
        }
        System.out.println("Total cost not found on receipt");
        return 0.0;
    }

    public static JSONArray getPurchasedItems(SparseArray<TextBlock> blocks) {
        JSONArray purchasedItems = new JSONArray();
        String pItemTemplate = "(.+)\\s+(\\d+\\.\\d{2})";
        Pattern pItemPattern = Pattern.compile(pItemTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            for (int li=0; li < block.size(); li++) {
                String strLine = block.get(li).getValue();
                if (Pattern.matches(strLine, pItemTemplate)) {
                    Matcher matcher = pItemPattern.matcher(strLine);
                    String purchasedItem = matcher.group(0);
                    Double purchasedValue = Double.parseDouble(matcher.group(1));
                    JSONObject purchasedPair = new JSONObject();
                    try {
                        purchasedPair.put("productName", purchasedItem);
                        purchasedPair.put("productCost", purchasedValue);
                        purchasedItems.put(purchasedPair);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (purchasedItems.length() < 1) {
            System.out.println("No purchased items found on receipt");
        }
        return purchasedItems;
    }
}
