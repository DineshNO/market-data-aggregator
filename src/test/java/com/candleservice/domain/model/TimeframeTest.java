package com.candleservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeframeTest {

    @Test
    void testS1BucketCalculation() {
        // 1620000000000 ms = 1620000000 seconds
        long epochMilli = 1620000123456L;
        long bucket = Timeframe.S1.bucketStartEpochSeconds(epochMilli);
        
        // Should round down to nearest second
        assertEquals(1620000123L, bucket);
    }

    @Test
    void testM1BucketCalculation() {
        // Test minute bucket alignment
        long epochMilli = 1620000123456L; // 1620000123 seconds
        long bucket = Timeframe.M1.bucketStartEpochSeconds(epochMilli);
        
        // Should round down to nearest minute (60 seconds)
        // 1620000123 / 60 = 27000002.05 -> 27000002 * 60 = 1620000120
        assertEquals(1620000120L, bucket);
    }

    @Test
    void testM5BucketCalculation() {
        long epochMilli = 1620000456789L;
        long bucket = Timeframe.M5.bucketStartEpochSeconds(epochMilli);
        
        // Should round down to nearest 5 minutes (300 seconds)
        // 1620000456 / 300 = 5400001.52 -> 5400001 * 300 = 1620000300
        assertEquals(1620000300L, bucket);
    }

    @Test
    void testH1BucketCalculation() {
        long epochMilli = 1620003723456L;
        long bucket = Timeframe.H1.bucketStartEpochSeconds(epochMilli);
        
        // Should round down to nearest hour (3600 seconds)
        // 1620003723 / 3600 = 450001.03 -> 450001 * 3600 = 1620003600
        assertEquals(1620003600L, bucket);
    }

    @Test
    void testTimeframeSeconds() {
        assertEquals(1, Timeframe.S1.getSeconds());
        assertEquals(60, Timeframe.M1.getSeconds());
        assertEquals(300, Timeframe.M5.getSeconds());
        assertEquals(900, Timeframe.M15.getSeconds());
        assertEquals(3600, Timeframe.H1.getSeconds());
    }
}
