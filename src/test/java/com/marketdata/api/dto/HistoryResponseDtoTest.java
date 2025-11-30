package com.marketdata.api.dto;

import com.marketdata.domain.model.Candle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryResponseDtoTest {

    @Test
    void testFromCandlesCreatesCorrectStructure() {
        Candle c1 = new Candle(1620000000, 50000.0, 51000.0, 49000.0, 49000.0, 3);
        Candle c2 = new Candle(1620000060, 49500.0, 50500.0, 49500.0, 50500.0, 2);

        List<Candle> candles = Arrays.asList(c1, c2);
        HistoryResponseDto dto = HistoryResponseDto.from(candles);

        assertEquals("ok", dto.getStatus());
        assertEquals(2, dto.getTimestamps().size());
        assertEquals(2, dto.getOpen().size());
        assertEquals(2, dto.getHigh().size());
        assertEquals(2, dto.getLow().size());
        assertEquals(2, dto.getClose().size());
        assertEquals(2, dto.getVolume().size());

        // Verify first candle
        assertEquals(1620000000L, dto.getTimestamps().get(0));
        assertEquals(50000.0, dto.getOpen().get(0));
        assertEquals(51000.0, dto.getHigh().get(0));
        assertEquals(49000.0, dto.getLow().get(0));
        assertEquals(49000.0, dto.getClose().get(0));
        assertEquals(3L, dto.getVolume().get(0));

        // Verify second candle
        assertEquals(1620000060L, dto.getTimestamps().get(1));
        assertEquals(49500.0, dto.getOpen().get(1));
        assertEquals(50500.0, dto.getHigh().get(1));
        assertEquals(49500.0, dto.getLow().get(1));
        assertEquals(50500.0, dto.getClose().get(1));
        assertEquals(2L, dto.getVolume().get(1));
    }

    @Test
    void testFromEmptyListCreatesEmptyArrays() {
        HistoryResponseDto dto = HistoryResponseDto.from(List.of());

        assertEquals("ok", dto.getStatus());
        assertTrue(dto.getTimestamps().isEmpty());
        assertTrue(dto.getOpen().isEmpty());
        assertTrue(dto.getHigh().isEmpty());
        assertTrue(dto.getLow().isEmpty());
        assertTrue(dto.getClose().isEmpty());
        assertTrue(dto.getVolume().isEmpty());
    }

    @Test
    void testErrorResponse() {
        HistoryResponseDto dto = HistoryResponseDto.error("Test error");

        assertEquals("error", dto.getStatus());
        assertTrue(dto.getTimestamps().isEmpty());
    }
}
