package com.example.qrhunter.generators;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

import com.example.qrhunter.generators.QrCodeRepresentative;

import java.util.HashMap;
import java.util.Map;

public class QrCodeImageGenerator extends QrCodeRepresentative {
    String[] frames = {};
    String[] rests = {};
    String[] squares = {};
    protected final int imageNumber = 3;
    public void generateQRCodeImage(String hex_string) {
        Map<String, String>  drawables = new HashMap<String, String>();
        String bit_string = super.hex_to_bit(hex_string);
        for (int i = 0; i < imageNumber; i++) {
            char firstChar = bit_string.charAt(i);
            char secondChar = bit_string.charAt(i+1);
            if (firstChar == '0' && secondChar == '0') {

            } else if (firstChar == '0' && secondChar == '1') {

            } else if (firstChar == '1' && secondChar == '0') {

            } else if (firstChar == '1' && secondChar == '1') {

            }
        }

    }

}
