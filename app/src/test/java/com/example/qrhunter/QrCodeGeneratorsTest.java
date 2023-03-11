package com.example.qrhunter;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.example.qrhunter.generators.QrCodeScoreGenerator;

public class QrCodeGeneratorsTest {
    /**
     * Tests the hex_to_bit function of name generator
     * Note that image generators is the same
     */
    @Test
    public void name_hex_to_bit_test () {
        QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
        assertEquals("11111111", nameGenerator.hex_to_bit("FF"));
        assertEquals("11111111", nameGenerator.hex_to_bit("FFFFFF"));
        assertEquals("00000000", nameGenerator.hex_to_bit("00"));
        assertEquals("10101011", nameGenerator.hex_to_bit("AB"));
        assertEquals("00010000", nameGenerator.hex_to_bit("10"));
    }
    /**
     * Tests the score algorithm on a few examples done by hand
     * and some edge cases.
     */
    @Test
    public void score_algorithm_test() {
        QrCodeScoreGenerator scoreGenerator = new QrCodeScoreGenerator();
        // Repeats: c:2, f:2, e:2, d:6, f:2, Sum repeat values by power-1: 12^1+15^1+14^1+13^5+15^1 = 371349
        assertEquals(371349, scoreGenerator.score_algorithm("25cce1b3f4ff0cee487dddddd029041014cf695b85daff02652fab7d308dc802"));
        // Repeats: d:2, a:2, 0:2, c:2, Sum repeats values by power-1: 13^1+10^1+20^1+12^1 = 55
        assertEquals(55, scoreGenerator.score_algorithm("cba1ddfaac2da49271dc83fca5d695802895231e903812f4600f95c0cc7b39d9"));
        // Repeats: a:2, c:3, 7:2, 9:2, c:2, b:2 Sum repeats values by power-1: 10^1+12^2+7^1+9^1+12^1+11^1 = 193
        assertEquals(193, scoreGenerator.score_algorithm("0385b45cb9bc7f862e2fd36aa27561ccc770995089d81814f6e7ccd98bbd3acf"));
        // Edge Case: all F's. Should max out at 15^5
        assertEquals(759375, scoreGenerator.score_algorithm("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        // Edge Case: No repeats.
        assertEquals(0, scoreGenerator.score_algorithm("010101010101010101010101010101010101010101010101010101010101010"));
        // Edge Case: Repeat only at beginning
        assertEquals(1, scoreGenerator.score_algorithm("110101010101010101010101010101010101010101010101010101010101010"));
        // Edge Case: Repeat only at end
        assertEquals(1, scoreGenerator.score_algorithm("1a0101010101010101010101010101010101010101010101010101010101011"));
        // Edge Case: Max value possibly does not overflow. With this, 68 scores of max value needed to overflow wallet total score
        assertEquals(32000400, scoreGenerator.score_algorithm("000001000001000001000001000001000001000001000001000001000001000"));


    }
}
