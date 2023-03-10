package com.example.qrhunter.generators;

public abstract class QrCodeRepresentative {
    protected final int maxBits = 8;
    public String hex_to_bit(String hex_string) {
        String bit_string = "";
        int i = 0;
        int maxBits = 6;
        while(i<(maxBits-maxBits %4)/4+1) {
            char fixedChar = hex_string.charAt(i);
            String byteString = (Integer.toBinaryString(Character.getNumericValue(fixedChar)));
            while (byteString.length() < 4) {
                byteString = "0".concat(byteString);
            }
            bit_string = bit_string.concat(byteString);
            i ++;
        }
        return bit_string;
    }
}