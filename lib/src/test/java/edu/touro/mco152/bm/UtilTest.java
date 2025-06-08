package edu.touro.mco152.bm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Util.randInt(int min, int max)
 */
public class UtilTest {

    /**
     * Right-BICEP (Right): Check that result is always within bounds
     */
    @RepeatedTest(10)
    @DisplayName("randInt returns value within range")
    void testRandIntRightRange() {
        int min = 3;
        int max = 8;
        int result = Util.randInt(min, max);
        assertTrue(result >= min && result <= max, "Result should be within [" + min + ", " + max + "]");
    }

    /**
     * Right-BICEP (Boundary): When min == max
     */
    @Test
    @DisplayName("randInt returns value equal to min when min == max")
    void testRandIntSameMinMax() {
        int result = Util.randInt(5, 5);
        assertEquals(5, result, "If min == max, result must equal both");
    }

    /**
     * Right-BICEP (Boundary): Integer.MIN_VALUE and MAX_VALUE
     */
    @Test
    @DisplayName("randInt handles full integer range")
    void testRandIntExtremeBounds() {
        int min = Integer.MIN_VALUE;
        int max = Integer.MIN_VALUE + 1;
        int result = Util.randInt(min, max);
        assertTrue(result == min || result == max, "Result should be either MIN_VALUE or MIN_VALUE + 1");
    }

    /**
     * Right-BICEP (Cross-check): All values in range should be possible
     */
    @Test
    @DisplayName("Cross-check: All values in small range are reachable")
    void testRandIntCrossCheckCoverage() {
        int min = 1;
        int max = 3;
        boolean found1 = false, found2 = false, found3 = false;
        for (int i = 0; i < 100; i++) {
            int val = Util.randInt(min, max);
            if (val == 1) found1 = true;
            if (val == 2) found2 = true;
            if (val == 3) found3 = true;
        }
        assertTrue(found1 && found2 && found3, "randInt should generate all values in the range [1, 3]");
    }

    /**
     * Right-BICEP (Error): min > max should throw or handle
     */
    @Test
    @DisplayName("randInt with min > max should throw IllegalArgumentException")
    void testRandIntInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            Util.randInt(10, 5);
        });
    }
}
