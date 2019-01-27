/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nwhacks.myapplication;

import android.util.SparseArray;

import com.nwhacks.myapplication.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Element;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        try {
            JSONObject receipt = parseDetectedItems(items);
        } catch (Exception e) {
            return;
        }
    }
    public JSONObject parseDetectedItems(SparseArray<TextBlock> blocks) throws JSONException, ParseException {
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

        receipt.put("companyName", companyName);
        receipt.put("transactionDate", strTransactionDate);
        receipt.put("totalCost", totalCost);
        receipt.put("purchasedItems", purchasedItems);
        return receipt;
    }

    public Date getTransactionDate(SparseArray<TextBlock> blocks) throws ParseException {
        String dateTemplate = "\\d{2}\\/\\d{2}\\/\\d{2,4}";
        Pattern datePattern = Pattern.compile(dateTemplate);
        Matcher matcher;
        for (int bi=0; bi < blocks.size(); bi++) {
            List<?extends Text> block = blocks.valueAt(bi).getComponents();
            for (int li=0; li < block.size(); li++) {
                List<?extends Text> line = block.get(li).getComponents();
                for (int ei=0; ei < line.size(); ei++) {
                    Text element = line.get(ei);
                    String word = element.getValue();
                    if (Pattern.matches(word, dateTemplate)) {
                        {
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyyy");
                            return format.parse(word);
                        }
                    }
                }
            }
        }
        return new Date();
    }

    public Double getTotalCost(SparseArray<TextBlock> blocks) throws ParseException {
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
        return 0.0;
    }

    public JSONArray getPurchasedItems(SparseArray<TextBlock> blocks) throws ParseException, JSONException {
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
                    purchasedPair.put("productName", purchasedItem);
                    purchasedPair.put("productCost", purchasedValue);
                    purchasedItems.put(purchasedPair);
                }
            }
        }
        return purchasedItems;
    }


    public getTotalCost()

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
