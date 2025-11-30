package com.marketdata.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeframeMapperTest {

    @Test
    void testParseTradingFormat() {
        // Minute format
        assertEquals(Timeframe.M1, TimeframeMapper.parse("1m"));
        assertEquals(Timeframe.M5, TimeframeMapper.parse("5m"));
        assertEquals(Timeframe.M15, TimeframeMapper.parse("15m"));
        assertEquals(Timeframe.M30, TimeframeMapper.parse("30m"));
        
        // Hour format
        assertEquals(Timeframe.H1, TimeframeMapper.parse("1h"));
        assertEquals(Timeframe.H5, TimeframeMapper.parse("5h"));
        
        // Day/week/month format
        assertEquals(Timeframe.D1, TimeframeMapper.parse("1d"));
        assertEquals(Timeframe.W1, TimeframeMapper.parse("1w"));
        assertEquals(Timeframe.MN1, TimeframeMapper.parse("1M"));
    }

    @Test
    void testParseInvalidInterval() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TimeframeMapper.parse("INVALID")
        );
        assertTrue(ex.getMessage().contains("Invalid interval"));
    }

    @Test
    void testParseEnumFormatNotSupported() {
        // Enum format not supported - only trading format
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TimeframeMapper.parse("M1")
        );
        assertTrue(ex.getMessage().contains("Invalid interval"));
    }

    @Test
    void testParseNullInterval() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TimeframeMapper.parse(null)
        );
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void testParseEmptyInterval() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TimeframeMapper.parse("")
        );
        assertTrue(ex.getMessage().contains("cannot be null or empty"));
    }

    @Test
    void testParseNumericInterval() {
        // Test numeric values like "15" which are invalid
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TimeframeMapper.parse("15")
        );
        assertTrue(ex.getMessage().contains("Invalid interval: 15"));
        assertTrue(ex.getMessage().contains("Supported formats"));
    }

    @Test
    void testParseInvalidFormats() {
        // Test various invalid formats
        String[] invalidIntervals = {"2m", "10m", "3h", "2d", "15", "1min", "5minutes"};
        
        for (String interval : invalidIntervals) {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> TimeframeMapper.parse(interval),
                "Expected exception for interval: " + interval
            );
            assertTrue(ex.getMessage().contains("Invalid interval"));
            assertTrue(ex.getMessage().contains("Supported formats"));
        }
    }
}
