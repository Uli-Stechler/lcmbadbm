package edu.touro.mco152.bm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiskMark.getBwMbSecAsString()
 */
public class DiskMarkTest {

    /**
     * Right-BICEP (Right): getBwMbSecAsString returns expected string format
     */
    @Test
    @DisplayName("getBwMbSecAsString returns correct format")
    void testBwMbSecAsStringRight() {
        DiskMark mark = new DiskMark(DiskMark.MarkType.WRITE);
        mark.setBwMbSec(123.456789);
        String expected = String.format("%.3f", 123.456789);
        assertEquals(expected, mark.getBwMbSecAsString(), "Should return string with 3 decimal places");
    }

    /**
     * Right-BICEP (Boundary): Test bwMbSec = 0
     */
    @Test
    @DisplayName("getBwMbSecAsString handles 0.0")
    void testBwMbSecAsStringZero() {
        DiskMark mark = new DiskMark(DiskMark.MarkType.READ);
        mark.setBwMbSec(0.0);
        assertEquals("0", mark.getBwMbSecAsString(), "Zero value should format as '0'");
    }

    /**
     * Right-BICEP (Boundary): Test bwMbSec = Double.MAX_VALUE
     */
    @Test
    @DisplayName("getBwMbSecAsString handles Double.MAX_VALUE")
    void testBwMbSecAsStringMaxValue() {
        DiskMark mark = new DiskMark(DiskMark.MarkType.READ);
        mark.setBwMbSec(Double.MAX_VALUE);

        String result = mark.getBwMbSecAsString();

        // Check that it starts with the correct digits
        assertTrue(result.startsWith("17976931348623157"),
                "Output should start with correct digits of Double.MAX_VALUE");
    }


    /**
     * Right-BICEP (Cross-check): Use alternate String.format
     */
    @Test
    @DisplayName("Cross-check string output with manual format")
    void testBwMbSecAsStringCrossCheck() {
        double val = 99.98765;
        DiskMark mark = new DiskMark(DiskMark.MarkType.WRITE);
        mark.setBwMbSec(val);

        String expected = String.format("%.3f", val);
        String actual = mark.getBwMbSecAsString();

        assertEquals(expected, actual, "Should match formatted string from manual logic");
    }
}
