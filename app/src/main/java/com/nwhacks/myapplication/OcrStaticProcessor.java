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

    private static Date getTransactionDate(SparseArray<TextBlock> blocks) {
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
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> lines = blocks.valueAt(bi).getComponents();
            for (int li=0; li < lines.size(); li++) {
                List<?extends Text> elements = lines.get(li).getComponents();
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
        }
        System.out.println("No date found on receipt, returning current date");
        return new Date();
    }

    private static Double getTotalCost(SparseArray<TextBlock> blocks) {
        String costTemplate = "^\\$?(\\d+\\.\\d{2})$";
        double largestCost = 0.0;
        Pattern costPattern = Pattern.compile(costTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> lines = blocks.valueAt(bi).getComponents();
            for (int li=0; li < lines.size(); li++) {
                List<?extends Text> elements = lines.get(li).getComponents();
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

        }
        if (largestCost == 0.0) {
            System.out.println("Total cost not found on receipt");
        }
        return largestCost;
    }

    private static JSONArray getPurchasedItems(SparseArray<TextBlock> blocks) {
        JSONArray purchasedItems = new JSONArray();
        String subTotalTemplate = "(?i)sub\\s*total";
        Pattern subTotalPattern = Pattern.compile(subTotalTemplate);
        String pItemTemplate = "(.*)\\s+\\$?(\\d+\\.\\d{2})";
        Pattern pItemPattern = Pattern.compile(pItemTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            String strLine = blocks.get(blocks.keyAt(bi)).getValue());
            Matcher matcherSubTotal = subTotalPattern.matcher(strLine);
            System.out.println("Line li " + li);
            System.out.println("strLine: " + strLine);
            if (matcherSubTotal.matches() && purchasedItems.length() > 0) {
                System.out.println("Hit sub total line, stopped looking for purchasedItems");
                return purchasedItems;
            }
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
