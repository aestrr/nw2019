package com.nwhacks.myapplication;

import android.graphics.Point;
import android.util.SparseArray;

import com.google.android.gms.vision.text.Line;
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
    /*
    public static JSONObject receiptJson = createJson();

    public OcrStaticProcessor() {
        receiptJson = createJson();
    }

    private static JSONObject createJson() {
        JSONObject receipt = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        try {
            receipt.put("companyName", "FRESH ST MARKET");
            receipt.put("transactionDate", "2018/06/11");
            receipt.put("totalCost", 30.99);
            item.put("productName", "FAM PK BNLS SKNL CHICKEN THIG");
            item.put("productCost", 24.16);
            items.put(item);
            item = new JSONObject();
            item.put("productName", "LA GRILLE SEASNG-MNTRL STK SP");
            item.put("productCost", 6.79);
            items.put(item);
            item = new JSONObject();
            item.put("productName", "BAG CHARGE - PLASTIC");
            item.put("productCost", 0.04);
            items.put(item);
            receipt.put("purchasedItems", items);
            return receipt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */
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
            String[] elements = textLines.get(li).getValue().split("\\s+");
            for (int ei=0; ei < elements.length; ei++) {
                Matcher matcher4 = datePattern4.matcher(elements[ei]);
                Matcher matcher2 = datePattern2.matcher(elements[ei]);
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
        System.out.println("No date found on receipt, returning epoch");
        return new Date(0);
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
                    // System.out.println("############# THIS IS BEING SENT TO Double.parseDouble" + matcher.group(1));
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
        String subTotalTemplate = "subtotal";
        Pattern subTotalPattern = Pattern.compile(subTotalTemplate, Pattern.CASE_INSENSITIVE);
        String pItemTemplate = "(.*)\\s+\\$?(\\d+\\.\\d{2})";
        Pattern pItemPattern = Pattern.compile(pItemTemplate);
        Matcher matcherSubTotal;
        List<String> combinedLines = InLine.combineInLines(textLines);
        for (int li=0; li < combinedLines.size(); li++) {
            String strLine = combinedLines.get(li);
            String[] words = strLine.split("\\s+");
            for (int ei=0; ei < words.length; ei++) {
                String el = words[ei];
                matcherSubTotal = subTotalPattern.matcher(el);
                if (matcherSubTotal.matches()) {
                    System.out.println("Hit sub total line, checking if purchased items have been found yet");
                    if (purchasedItems.length() > 0) {
                        System.out.println("Purchased items found, returning");
                        return purchasedItems;
                    } else {
                        System.out.println("Hit sub total line, but no purchased items found, continuing");
                    }
                }

            }
            // System.out.println("strLine: " + strLine);
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

class InLine {
    public static double distance (double floats[]) {
        double min = floats[0];
        double max = floats[0];
        for (int i=1; i < floats.length; i++) {
            if (floats[i] < min) {
                min = floats[i];
            }
            if (floats[i] > max) {
                max = floats[i];
            }
        }
        return max - min;
    }

    public static int isLeft(Point[] boxA, Point[] boxB) {
        int axSum = 0;
        int bxSum = 0;
        for (int i=0; i < 4; i++) {
            axSum += boxA[i].x;
            bxSum += boxB[i].x;
        }
        if (axSum < bxSum) {
            return 0;
        } else {
            return 1;
        }
    }

    public static double sideBySideDistance(Point[] boxA, Point[] boxB) {
        Point boxAtl = boxA[0];
        Point boxAtr = boxA[1];
        Point boxAbr = boxA[2];
        Point boxAbl = boxA[3];
        Point boxBtl = boxB[0];
        Point boxBtr = boxB[1];
        Point boxBbr = boxB[2];
        Point boxBbl = boxB[3];


        // slopes of connecting lines between first and second boxes
        double comhm0 = Math.atan((double) (boxBtl.y - boxAtl.y) / (double) (boxBtl.x - boxAtl.x));
        double comhm1 = Math.atan((double) (boxBtr.y - boxAtr.y) / (double) (boxBtr.x - boxAtr.x));
        double comhm2 = Math.atan((double) (boxBbl.y - boxAbl.y) / (double) (boxBbl.x - boxAbl.x));
        double comhm3 = Math.atan((double) (boxBbr.y - boxAbr.y) / (double) (boxBbr.x - boxAbr.x));
        double slopes[] = {comhm0, comhm1, comhm2, comhm3};

        return distance(slopes);
    }

    public static double inLineDistance(Point[] boxA, Point[] boxB) {
        Point boxAtl = boxA[0];
        Point boxAtr = boxA[1];
        Point boxAbr = boxA[2];
        Point boxAbl = boxA[3];
        Point boxBtl = boxB[0];
        Point boxBtr = boxB[1];
        Point boxBbr = boxB[2];
        Point boxBbl = boxB[3];

        // slopes of first box's horizontal lines
        double boxAhm0 = Math.atan((double) (boxAtr.y - boxAtl.y) / (double) (boxAtr.x - boxAtl.x));
        double boxAhm1 = Math.atan((double) (boxAbr.y - boxAbl.y) / (double) (boxAbr.x - boxAbl.x));

        // slopes of second box's horizontal lines
        double boxBhm0 = Math.atan((double) (boxBtr.y - boxBtl.y) / (double) (boxBtr.x - boxBtl.x));
        double boxBhm1 = Math.atan((double) (boxBbr.y - boxBbl.y) / (double) (boxBbr.x - boxBbl.x));

        // slopes of connecting lines between first and second boxes
        double comhm0 = Math.atan((double) (boxBtl.y - boxAtr.y) / (double) (boxBtl.x - boxAtr.x));
        double comhm1 = Math.atan((double) (boxBbl.y - boxAbr.y) / (double) (boxAbl.x - boxAbr.x));

        double slopes[] = {boxAhm0, boxAhm1, boxBhm0, boxBhm1, comhm0, comhm1};

        return distance(slopes);
    }


    public static List<String> combineInLines(List<Text> textLines) {
        List<String> outList = new ArrayList<>();
        List<Integer> covered = new ArrayList<>();
        for (int i=0; i<textLines.size()-1; i++) {
            boolean skip = false;
            for (int c : covered) {
                if (i == c) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            Point box0[] = textLines.get(i).getCornerPoints();
            int closest = closestSideKick(textLines, i);
            if (closest != -1) {
                skip = false;
                for (int c : covered) {
                    if (closest == c) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                String line0 = textLines.get(i).getValue();
                String line1 = textLines.get(closest).getValue();
                String[] lines = {line0, line1};
                outList.add(textLines.get(closest).getValue());
                covered.add(closest);
                covered.add(i);
                Point box1[] = textLines.get(closest).getCornerPoints();
                int leftBox = isLeft(box0, box1);
                int rightBox;
                if (leftBox != 0) {
                    rightBox = 0;
                } else {
                    rightBox = 1;
                }
                String combined = lines[leftBox] + " " + lines[rightBox];
                outList.add(combined);
            } else {
                outList.add(textLines.get(i).getValue());
                covered.add(i);
            }
        }
        return outList;
    }

    public static int closestSideKick(List<Text> textLines, int idx) {
        double sthreshold = 0.04;
        double ithreshold = 0.04;
        double sbsd;
        double sbsdc;
        double ild;
        double ildc;
        Point box0[] = textLines.get(idx).getCornerPoints();
        Point box1[];
        int sclosest;
        int iclosest;
        if (idx == 0) {
            sclosest = 1;
            iclosest = 1;
        } else {
            sclosest = 0;
            iclosest = 0;
        }
        sbsdc = sideBySideDistance(box0, textLines.get(sclosest).getCornerPoints());
        ildc = inLineDistance(box0, textLines.get(iclosest).getCornerPoints());
        for (int i=0; i < textLines.size(); i++) {
            if (i == idx) {
                continue;
            }
            box1 = textLines.get(i).getCornerPoints();
            sbsd = sideBySideDistance(box0, box1);
            ild = inLineDistance(box0, box1);

            if (sbsd < sbsdc) {
                sclosest = i;
                sbsdc = sbsd;
            }
            if (ild < ildc) {
                iclosest = i;
                ildc = ild;
            }
        }

        if (sclosest == iclosest && sbsdc < sthreshold && ildc < ithreshold) {
            return sclosest;
        } else {
            return -1;
        }
    }
}