package com.candleservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CandleTest {

    @Test
    void testCandleConstructor() {
        long time = 1620000000L;
        double price = 50000.0;
        
        Candle candle = new Candle(time, price, price, price, price, 1);
        
        assertEquals(time, candle.getTime());
        assertEquals(price, candle.getOpen());
        assertEquals(price, candle.getHigh());
        assertEquals(price, candle.getLow());
        assertEquals(price, candle.getClose());
        assertEquals(1, candle.getVolume());
    }

    @Test
    void testCandleAllArgsConstructor() {
        long time = 1620000000L;
        double open = 50000.0;
        double high = 51000.0;
        double low = 49000.0;
        double close = 50500.0;
        long volume = 100;
        
        Candle candle = new Candle(time, open, high, low, close, volume);
        
        assertEquals(time, candle.getTime());
        assertEquals(open, candle.getOpen());
        assertEquals(high, candle.getHigh());
        assertEquals(low, candle.getLow());
        assertEquals(close, candle.getClose());
        assertEquals(volume, candle.getVolume());
    }

    @Test
    void testCandleImmutability() {
        Candle candle = new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100);
        
        assertEquals(50000.0, candle.getOpen());
        assertEquals(51000.0, candle.getHigh());
        assertEquals(49000.0, candle.getLow());
        assertEquals(50500.0, candle.getClose());
        assertEquals(100, candle.getVolume());
    }

    @Test
    void testCandleEquality() {
        Candle candle1 = new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100);
        Candle candle2 = new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100);
        
        assertEquals(candle1.getTime(), candle2.getTime());
        assertEquals(candle1.getOpen(), candle2.getOpen());
        assertEquals(candle1.getHigh(), candle2.getHigh());
        assertEquals(candle1.getLow(), candle2.getLow());
        assertEquals(candle1.getClose(), candle2.getClose());
        assertEquals(candle1.getVolume(), candle2.getVolume());
    }
}
