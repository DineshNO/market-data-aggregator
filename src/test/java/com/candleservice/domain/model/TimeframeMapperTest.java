package com.candleservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeframeMapperTest {

    @Test
    void testParseTradingFormat() {
        // Second format
        assertEquals(Timeframe.S1, TimeframeMapper.parse("1s"));
        
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
}
