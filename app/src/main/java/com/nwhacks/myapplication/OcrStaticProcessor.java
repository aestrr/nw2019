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
        String dateTemplate = "\\d{2}\\/\\d{2}\\/\\d{2,4}";
        Pattern datePattern = Pattern.compile(dateTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            Matcher matcher = datePattern.matcher(blocks.valueAt(bi).getValue());
            for (int m = 0; m < matcher.groupCount(); m++) {
                String word = matcher.group(m);
                SimpleDateFormat format0 = new SimpleDateFormat("MM/dd/yyyyy");
                SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yy");
                SimpleDateFormat formats[] = {format0, format1, format2, format3};
                Date parsedDate;
                for (SimpleDateFormat f : formats) {
                    try {
                        parsedDate = format0.parse(word);
                        if (parsedDate != null) {
                            return parsedDate;
                        }
                    } catch (ParseException e) {
                        System.out.println(String.format("Can't parse date %s", word));
                    }
                }

            }
        }
        System.out.println("No date found on receipt, returning current date");
        return new Date();
    }

    private static Double getTotalCost(SparseArray<TextBlock> blocks) {
        String costTemplate = "\\$(\\d+\\.\\d{2})";
        double largestCost = 0.0;
        Pattern costPattern = Pattern.compile(costTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            Matcher matcher = costPattern.matcher(blocks.valueAt(bi).getValue());
            for (int m = 0; m < matcher.groupCount(); m++) {
                Double match = Double.parseDouble(matcher.group(m));
                if (match > largestCost) {
                    largestCost = match;
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
        String subTotalTemplate = "?(S|s)ub.+?(t|T)otal";
        String pItemTemplate = "(.+)\\s+(\\d+\\.\\d{2})";
        Pattern pItemPattern = Pattern.compile(pItemTemplate);
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            for (int li=0; li < block.size(); li++) {
                String strLine = block.get(li).getValue();
                if (Pattern.matches(strLine, subTotalTemplate) && purchasedItems.length() > 0) {
                    System.out.println("Hit sub total line, stopped looking for purchasedItems");
                    return purchasedItems;
                }
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
