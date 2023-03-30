package com.example.qrhunter;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.example.qrhunter.generators.QrCodeScoreGenerator;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class QrCodeGeneratorsTest {
    /**
     * Tests various aspects of the name generator
     * tests name generator on a few examples done by hand to ensure correctness and continued correctness
     * tests is pure and hex_to_bit
     */
    @Test
    public void name_test () {
        // 'Pure' is added as a prefix if a qrcode is the same color
        // protected final String[] zeroString = {"Absolutely ", "Massive ", "Extremely Expensive ", "Turbo", "Multi-Dimensional ", "Poly-Dodecahedron"};
        // protected final String[] oneString = {"Fiery ", "Solid ", "Ready To Go ", "Giga", "Draconic ", "Tesseract"};
        QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
        // bit(84) = 10000010. Expected "Fiery Massive Extremely Expensive TurboMulti-Dimensional Poly-Dodecahedron"
        assertEquals("Fiery Massive Extremely Expensive TurboMulti-Dimensional Poly-Dodecahedron", nameGenerator.createQRName("84e9ff9ffc70101f15673b99a79868fdb7c441f8b8f8569785aae1799e32ed56"));
        // bit(e9) = 11101001. Expected "Fiery Solid Ready To Go Extremely Expensive Draconic Poly-Dodecahedron"
        assertEquals("Fiery Solid Ready To Go Extremely Expensive Draconic Poly-Dodecahedron", nameGenerator.createQRName("e97017cdba5a0405df4309360f277b61e4133907476ba9a8cfaa879a63b7c5fb"));
        // bit(42) = 10000100. Expected "Fiery Massive Extremely Expensive TurboMulti-Dimensional Tesseract"
        assertEquals("Fiery Massive Extremely Expensive TurboMulti-Dimensional Tesseract", nameGenerator.createQRName("4215d86adb1f713c7aab29931a25979203103c26b94db67a273c6ea22ca7c76a"));
        // bit(aa) = 10101010. Expected "Pure Fiery Massive Ready To Go TurboDraconic Poly-Dodecahedron"
        assertEquals("Pure Fiery Massive Ready To Go TurboDraconic Poly-Dodecahedron", nameGenerator.createQRName("aaa82ef2acc62dec48ead94abcb878d1d91fd263ee65c7f0aabdad936a122c2b"));

        // isPure(String bit_string) is true if when in base 4 the first 3 digits are the same
        assertEquals(false, nameGenerator.isPure("10000000")); //2000
        assertEquals(false, nameGenerator.isPure("10100000")); //2200
        assertEquals(true, nameGenerator.isPure("11111111")); //3333
        assertEquals(true, nameGenerator.isPure("11111100")); //3330
        assertEquals(true, nameGenerator.isPure("00000000")); //0000
        assertEquals(true, nameGenerator.isPure("10101000")); //2220
        assertEquals(true, nameGenerator.isPure("01010111")); //1113
        assertEquals(false, nameGenerator.isPure("00100000")); //0200
        assertEquals(false, nameGenerator.isPure("11001100")); //3030


        assertEquals("11111111", nameGenerator.hex_to_bit("FF"));
        assertEquals("11111111", nameGenerator.hex_to_bit("FFFFFF"));
        assertEquals("00000000", nameGenerator.hex_to_bit("00"));
        assertEquals("10101011", nameGenerator.hex_to_bit("AB"));
        assertEquals("00010000", nameGenerator.hex_to_bit("10"));
        assertEquals("00010000", nameGenerator.hex_to_bit("10"));
    }

    /**
     * Tests the score algorithm on a few examples done by hand
     * and some edge cases.
     */

    @Test
    public void score_algorithm_test() {
        // Repeats: c:2, f:2, e:2, d:6, f:2, Sum repeat values by power-1: 12^1+15^1+14^1+13^5+15^1 = 371349
        assertEquals(371349, QrCodeScoreGenerator.score_algorithm("25cce1b3f4ff0cee487dddddd029041014cf695b85daff02652fab7d308dc802"));
        // Repeats: d:2, a:2, 0:2, c:2, Sum repeats values by power-1: 13^1+10^1+20^1+12^1 = 55
        assertEquals(55, QrCodeScoreGenerator.score_algorithm("cba1ddfaac2da49271dc83fca5d695802895231e903812f4600f95c0cc7b39d9"));
        // Repeats: a:2, c:3, 7:2, 9:2, c:2, b:2 Sum repeats values by power-1: 10^1+12^2+7^1+9^1+12^1+11^1 = 193
        assertEquals(193, QrCodeScoreGenerator.score_algorithm("0385b45cb9bc7f862e2fd36aa27561ccc770995089d81814f6e7ccd98bbd3acf"));
        // Edge Case: all F's. Should max out at 15^5
        assertEquals(759375, QrCodeScoreGenerator.score_algorithm("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        // Edge Case: No repeats.
        assertEquals(0, QrCodeScoreGenerator.score_algorithm("010101010101010101010101010101010101010101010101010101010101010"));
        // Edge Case: Repeat only at beginning
        assertEquals(1, QrCodeScoreGenerator.score_algorithm("110101010101010101010101010101010101010101010101010101010101010"));
        // Edge Case: Repeat only at end
        assertEquals(1, QrCodeScoreGenerator.score_algorithm("1a0101010101010101010101010101010101010101010101010101010101011"));
        // Edge Case: Max value possibly does not overflow. With this, 68 scores of max value needed to overflow 32 bit.
        assertEquals(32000400, QrCodeScoreGenerator.score_algorithm("000001000001000001000001000001000001000001000001000001000001000"));

    }
}
