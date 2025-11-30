package com.marketdata.api.controller;

import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HistoryControllerTest {

    private HistoryController controller;
    private QueryHistoryUseCase queryHistoryUseCase;

    @BeforeEach
    void setUp() {
        queryHistoryUseCase = mock(QueryHistoryUseCase.class);
        controller = new HistoryController(queryHistoryUseCase);
    }

    @Test
    void testHistory_ReturnsCandles() {
        // Given
        var candles = List.of(
            new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100),
            new Candle(1620000060L, 50500.0, 51500.0, 50000.0, 51000.0, 95)
        );
        
        when(queryHistoryUseCase.getHistory(anyString(), any(Timeframe.class), anyLong(), anyLong()))
            .thenReturn(candles);

        // When
        var response = controller.history("BTC-USD", "1m", 1620000000L, 1620000600L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getStatus());
        assertEquals(2, response.getBody().getTimestamps().size());
        assertEquals(1620000000L, response.getBody().getTimestamps().get(0));
        assertEquals(50000.0, response.getBody().getOpen().get(0));
        
        verify(queryHistoryUseCase).getHistory("BTC-USD", Timeframe.M1, 1620000000L, 1620000600L);
    }

    @Test
    void testHistory_EmptyResult() {
        // Given
        when(queryHistoryUseCase.getHistory(anyString(), any(Timeframe.class), anyLong(), anyLong()))
            .thenReturn(List.of());

        // When
        var response = controller.history("BTC-USD", "1m", 1620000000L, 1620000600L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getStatus());
        assertTrue(response.getBody().getTimestamps().isEmpty());
    }

    @Test
    void testHistoryWithSql_ReturnsCandles() {
        // Given
        var candles = List.of(
            new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100),
            new Candle(1620000060L, 50500.0, 51500.0, 50000.0, 51000.0, 95)
        );
        
        when(queryHistoryUseCase.getHistoryWithSql(anyString(), any(Timeframe.class), anyLong(), anyLong()))
            .thenReturn(candles);

        // When
        var response = controller.historyWithSql("BTC-USD", "1m", 1620000000L, 1620000600L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getStatus());
        assertEquals(2, response.getBody().getTimestamps().size());
        assertEquals(1620000000L, response.getBody().getTimestamps().get(0));
        assertEquals(50000.0, response.getBody().getOpen().get(0));
        
        verify(queryHistoryUseCase).getHistoryWithSql("BTC-USD", Timeframe.M1, 1620000000L, 1620000600L);
    }

    @Test
    void testHistoryWithSql_EmptyResult() {
        // Given
        when(queryHistoryUseCase.getHistoryWithSql(anyString(), any(Timeframe.class), anyLong(), anyLong()))
            .thenReturn(List.of());

        // When
        var response = controller.historyWithSql("BTC-USD", "1m", 1620000000L, 1620000600L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getStatus());
        assertTrue(response.getBody().getTimestamps().isEmpty());
    }

    @Test
    void testHistory_DifferentTimeframes() {
        // Given
        var candles = List.of(new Candle(1620000000L, 50000.0, 51000.0, 49000.0, 50500.0, 100));
        when(queryHistoryUseCase.getHistory(anyString(), any(Timeframe.class), anyLong(), anyLong()))
            .thenReturn(candles);

        // When - Test different intervals
        controller.history("BTC-USD", "5m", 1620000000L, 1620000600L);
        verify(queryHistoryUseCase).getHistory("BTC-USD", Timeframe.M5, 1620000000L, 1620000600L);

        controller.history("BTC-USD", "1h", 1620000000L, 1620000600L);
        verify(queryHistoryUseCase).getHistory("BTC-USD", Timeframe.H1, 1620000000L, 1620000600L);
    }
}
