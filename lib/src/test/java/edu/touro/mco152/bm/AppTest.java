package edu.touro.mco152.bm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for methods in App class
 */
public class AppTest {

    @BeforeEach
    void setup() {
        // Reset App fields before each test
        App.blockSizeKb = 512;
        App.numOfBlocks = 32;
        App.numOfMarks = 10;
    }

    /**
     * Right-BICEP (Right): Ensure targetMarkSizeKb() computes correctly.
     */
    @Test
    @DisplayName("targetMarkSizeKb computes correctly")
    void testTargetMarkSizeKbRight() {
        long expected = (long) App.blockSizeKb * App.numOfBlocks;
        assertEquals(expected, App.targetMarkSizeKb(), "targetMarkSizeKb() should correctly multiply block size and number of blocks");
    }

    /**
     * Right-BICEP (Cross-Check): Compare targetMarkSizeKb to manual multiplication.
     */
    @Test
    @DisplayName("Cross-check targetMarkSizeKb calculation manually")
    void crossCheckTargetMarkSizeKb() {
        long manual = 512L * 32L;
        assertEquals(manual, App.targetMarkSizeKb(), "Cross-check with manual calculation for targetMarkSizeKb()");
    }

    /**
     * Right-BICEP (Boundary): Test targetMarkSizeKb() at boundary values.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 32, 0",             // blockSize 0
            "512, 0, 0",            // numBlocks 0
            "1, 1, 1",              // smallest non-zero
            "1024, 100000, 102400000" // very large number
    })
    @DisplayName("Boundary tests for targetMarkSizeKb")
    void boundaryTestTargetMarkSizeKb(int blockSizeKb, int numBlocks, long expected) {
        App.blockSizeKb = blockSizeKb;
        App.numOfBlocks = numBlocks;
        assertEquals(expected, App.targetMarkSizeKb(), "Boundary condition test for targetMarkSizeKb()");
    }

    /**
     * Right-BICEP (Right): Ensure targetTxSizeKb() computes correctly.
     */
    @Test
    @DisplayName("targetTxSizeKb computes correctly")
    void testTargetTxSizeKbRight() {
        long expected = (long) App.blockSizeKb * App.numOfBlocks * App.numOfMarks;
        assertEquals(expected, App.targetTxSizeKb(), "targetTxSizeKb() should correctly multiply block size, number of blocks, and number of marks");
    }

    /**
     * Right-BICEP (Boundary): Test targetTxSizeKb() at boundary values.
     */
    @ParameterizedTest
    @CsvSource({
            "512, 0, 10, 0",           // numBlocks 0
            "0, 32, 10, 0",            // blockSize 0
            "512, 32, 0, 0",           // numMarks 0
            "1, 1, 1, 1",              // smallest non-zero
            "1024, 1000, 1000, 1024000000" // very large numbers
    })
    @DisplayName("Boundary tests for targetTxSizeKb")
    void boundaryTestTargetTxSizeKb(int blockSizeKb, int numBlocks, int numMarks, long expected) {
        App.blockSizeKb = blockSizeKb;
        App.numOfBlocks = numBlocks;
        App.numOfMarks = numMarks;
        assertEquals(expected, App.targetTxSizeKb(), "Boundary condition test for targetTxSizeKb()");
    }
}
