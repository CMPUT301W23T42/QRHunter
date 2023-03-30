package com.example.qrhunter.generators;

import com.example.qrhunter.generators.QrCodeRepresentative;

/***
 * This class handles QR code name generation
 */
public class QrCodeNameGenerator implements QrCodeRepresentative{

    protected final String[] zeroString = {"Absolutely ", "Massive ", "Extremely Expensive ", "Turbo", "Multi-Dimensional ", "Poly-Dodecahedron"};
    protected final String[] oneString = {"Fiery ", "Solid ", "Ready To Go ", "Giga", "Draconic ", "Tesseract"};
    protected int maxBytes = 2;
    /**
     * This generates a UNIQUE (up to the number of bits) QRCode Name up to @maxBits
     * Draws strings from zeroString and oneString
     * @param hex_string: This is the QRCode hash
     * @return name: This is the finished QRCode name
     */
    public String createQRName(String hex_string) {
        String name = "";
        String bit_string = hex_to_bit(hex_string);
        if (isPure(bit_string)) {
            name = name.concat("Pure ");
        }
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

    /***
     * Converts a hexadecimal string into a bitstring of @maxBits
     * @param hex_string
     * @return bit_string
     */
    public String hex_to_bit(String hex_string) {
        String bit_string = "";
        int i = 0;
        while(i<maxBytes) {
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

    public boolean isPure(String bit_string) {
        int first_val = bit_string.charAt(0) + 2*bit_string.charAt(1);
        if (bit_string.charAt(2) + 2*bit_string.charAt(3) == first_val && bit_string.charAt(4) + 2*bit_string.charAt(5) == first_val) {
            return true;
        }
        return false;
    }
}
