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
package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.RemoteViews;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple Processor which gets detected TextBlocks and adds
 them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Button cnpj_button, coo_button, data_button, valor1_button;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, Button cnpj_btn, Button coo_btn, Button data_btn, Button valor1_btn) {
        mGraphicOverlay = ocrGraphicOverlay;
        cnpj_button = cnpj_btn;
        coo_button = coo_btn;
        data_button = data_btn;
        valor1_button = valor1_btn;
    }

    //grab the layout, then set the text of the Button called R.id.Counter:
    //Context context = new Context(Context);
    //private RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ocr_capture);
    //remoteViews.setTextViewText(R.id.valor1_button, "Ronaldo");

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar
     in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that
     have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        String cnpj, coo, data;
        Matcher matcher;
        Pattern pattern;

        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null) {
                if (item.getValue().replaceAll("\\s", "").matches(".*CNPJ:.*")){
                    cnpj = item.getValue().replaceAll("\\s",
                            "").replaceFirst(".*CNPJ:","");
                    cnpj = cnpj.replaceAll(",", ".");
                    cnpj = cnpj.substring(0, 18);
                    if (cnpj.matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}")) {
                        Log.d("OcrDetectorProcessor", "CNPJ " + cnpj);
                        //cnpj_button.setTextColor(Color.GREEN);
                        cnpj_button.setText(cnpj);
                    }
                }
                if (item.getValue().replaceAll("\\s", "").matches(".*\\d{2}/\\d{2}/\\d{4}.*")) {
                    data = item.getValue().replaceAll("\\s", "");

                    pattern = Pattern.compile(".*(\\d{2}/\\d{2}/\\d{4}).*");
                    matcher = pattern.matcher(data);

                    if (matcher.find()) {
                        Log.d("OcrDetectorProcessor", "Data " + matcher.group(1));
                        //data_button.setTextColor(Color.GREEN);
                        data_button.setText(matcher.group(1));
                    }
                }
                if (item.getValue().matches(".*C[OU0][OU0]:.*")) {
                    coo = item.getValue().replaceAll("\\s", "").replaceFirst(".*C[OU0][OU0]:", "");
                    if (coo.matches("\\d{6}")) {
                        Log.d("OcrDetectorProcessor", "COO " + coo);
                        //coo_button.setTextColor(Color.GREEN);
                        coo_button.setText(coo);
                    }
                }

                pattern = Pattern.compile("(\\d+[,\\.]\\d{2})");
                matcher = pattern.matcher(item.getValue().replaceAll("\\s", ""));

                while (matcher.find()) {
                    Log.d("OcrDetectorProcessor", "Possível valor " + matcher.group(1).replace(".",","));
                   // valor1_button.setTextColor(Color.GREEN);
                    valor1_button.setText(matcher.group(1).replace(".",","));
                }
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}