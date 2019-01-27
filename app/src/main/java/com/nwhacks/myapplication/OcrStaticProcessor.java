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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrStaticProcessor {

    public static Comparator<Text> TextComparator
            = new Comparator<Text>() {
        @Override
        public int compare(Text t1, Text t2) {
            int diffOfTops = t1.getBoundingBox().top - t2.getBoundingBox().top;
            int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

            if (diffOfTops != 0) {
                return diffOfTops;
            }
            return diffOfLefts;
        }
    };

    public static JSONObject parseDetectedItems(SparseArray<TextBlock> blocks) {
        int nTextBlocks = blocks.size();
        if (nTextBlocks < 1) {
            return null;
        }


        List<Text> textLines = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            TextBlock textBlock = blocks.valueAt(i);

            List<? extends Text> textComponents = textBlock.getComponents();
            textLines.addAll(textComponents);
        }


        Collections.sort(textLines, TextComparator);


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String companyName = textLines.get(0).getValue();

        Date transactionDate = getTransactionDate(textLines);

        String strTransactionDate = dateFormat.format(transactionDate);
        Double totalCost = getTotalCost(textLines);
        JSONArray purchasedItems = getPurchasedItems(textLines);

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

    private static Date getTransactionDate(List<Text> textLines) {
        String dateTemplate4 = "^\\d{2}\\/\\d{2}\\/\\d{4}$";
        String dateTemplate2 = "^\\d{2}\\/\\d{2}\\/\\d{2}$";
        Pattern datePattern4 = Pattern.compile(dateTemplate4);
        Pattern datePattern2 = Pattern.compile(dateTemplate2);
        SimpleDateFormat format0 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat formats4[] = {format0, format2};
        SimpleDateFormat formats2[] = {format1, format3};
        for (int li=0; li < textLines.size(); li++) {
            List<?extends Text> elements = textLines.get(li).getComponents();
            for (int ei=0; ei < elements.size(); ei++) {
                Matcher matcher4 = datePattern4.matcher(elements.get(ei).getValue());
                Matcher matcher2 = datePattern2.matcher(elements.get(ei).getValue());
                if (matcher4.matches()) {
                    String word = matcher4.group(0);
                    Date parsedDate;
                    for (SimpleDateFormat f : formats4) {
                       try {
                           parsedDate = f.parse(word);
                           if (parsedDate != null) {
                                return parsedDate;
                           }
                       } catch (ParseException e) {
                           System.out.println(String.format("Can't parse date %s", word));
                        }
                    }
                }
                if (matcher2.matches()) {
                    String word = matcher2.group(0);
                    Date parsedDate;
                    for (SimpleDateFormat f : formats2) {
                        try {
                            parsedDate = f.parse(word);
                            if (parsedDate != null) {
                                return parsedDate;
                            }
                        } catch (ParseException e) {
                            System.out.println(String.format("Can't parse date %s", word));
                        }
                    }
                }
            }
        }
        System.out.println("No date found on receipt, returning current date");
        return new Date();
    }

    private static Double getTotalCost(List<Text> textLines) {
        String costTemplate = "^\\$?(\\d+\\.\\d{2})$";
        double largestCost = 0.0;
        Pattern costPattern = Pattern.compile(costTemplate);
        for (int li=0; li < textLines.size(); li++) {
            List<?extends Text> elements = textLines.get(li).getComponents();
            for (int ei=0; ei < elements.size(); ei++) {
                Matcher matcher = costPattern.matcher(elements.get(ei).getValue());
                if (matcher.matches()) {
                    System.out.println("############# THIS IS BEING SENT TO Double.parseDouble" + matcher.group(1));
                    Double match = Double.parseDouble(matcher.group(1));
                    if (match > largestCost) {
                        largestCost = match;
                    }
                }
            }
        }
        if (largestCost == 0.0) {
            System.out.println("Total cost not found on receipt");
        }
        return largestCost;
    }

    private static JSONArray getPurchasedItems(List<Text> textLines) {
        JSONArray purchasedItems = new JSONArray();
        String subTotalTemplate = ".+sub\\s*total.+";
        Pattern subTotalPattern = Pattern.compile(subTotalTemplate, Pattern.CASE_INSENSITIVE);
        String pItemTemplate = "(.*)\\s+\\$?(\\d+\\.\\d{2})";
        Pattern pItemPattern = Pattern.compile(pItemTemplate);
        Matcher matcherSubTotal;
        for (int li=0; li < textLines.size(); li++) {
            String strLine = textLines.get(li).getValue();
            List<?extends Text> elements = textLines.get(li).getComponents();
            for (int ei=0; ei < elements.size(); ei++) {
                String el = elements.get(ei).getValue();
                matcherSubTotal = subTotalPattern.matcher(el);
                if (matcherSubTotal.matches() && purchasedItems.length() > 0) {
                    System.out.println("Hit sub total line, stopped looking for purchasedItems");
                    return purchasedItems;
                }

            }
            System.out.println("strLine: " + strLine);
            Matcher matcherPurchasedItems = pItemPattern.matcher(strLine);
            if (matcherPurchasedItems.matches()) {
                String purchasedItem = matcherPurchasedItems.group(1);
                Double purchasedValue = Double.parseDouble(matcherPurchasedItems.group(2));
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
        if (purchasedItems.length() < 1) {
            System.out.println("No purchased items found on receipt");
        }
        return purchasedItems;
    }
}
