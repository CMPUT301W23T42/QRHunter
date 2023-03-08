package com.example.qrhunter.generators;

import com.example.qrhunter.generators.QrCodeRepresentative;

public class QrCodeNameGenerator extends QrCodeRepresentative{

    protected final String[] zeroString = {"cool", "Fro", "Mo", "Mega", "Spectral", "Crab"};
    protected final String[] oneString = {"hot", "Glo", "Lo", "Ultra", "Sonic", "Shark"};
    public String createQRName(String hex_string) {
        String name = "";
        String bit_string = super.hex_to_bit(hex_string);
        int i = 0;
        while (i < zeroString.length && i < oneString.length) {
            if (bit_string.charAt(i) == '0') {
                name = name.concat(zeroString[i]);
            } else if (bit_string.charAt(i) == '1') {
                name = name.concat(oneString[i]);
            }
            i ++;
        }
        return name;
    }
}
