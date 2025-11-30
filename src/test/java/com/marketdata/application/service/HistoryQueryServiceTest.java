package com.marketdata.application.service;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import com.marketdata.infrastructure.repository.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryQueryServiceTest {

    private TestEventRepository eventRepository;
    private HistoryQueryService service;

    @BeforeEach
    void setUp() {
        eventRepository = new TestEventRepository();
        service = new HistoryQueryService(eventRepository, new com.marketdata.domain.service.CandleAggregator());
    }

    @Test
    void testGetHistoryReturnsEmptyForNoData() {
        List<Candle> candles = service.getHistory("BTC-USD", Timeframe.M1, 1620000000, 1620000600);
        assertTrue(candles.isEmpty());
    }

    @Test
    void testGetHistoryReturnsSortedCandles() {
        // Store events in different 1-minute buckets (unsorted)
        eventRepository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000120L));
        eventRepository.save(new BidAskEvent("BTC-USD", 49000.0, 49001.0, 1620000060L));
        eventRepository.save(new BidAskEvent("BTC-USD", 51000.0, 51001.0, 1620000180L));

        List<Candle> candles = service.getHistory("BTC-USD", Timeframe.M1, 1620000000, 1620000200);

        assertEquals(3, candles.size());
        assertEquals(1620000060, candles.get(0).getTime());
        assertEquals(1620000120, candles.get(1).getTime());
        assertEquals(1620000180, candles.get(2).getTime());
    }

    @Test
    void testGetHistoryFiltersTimeRange() {
        eventRepository.save(new BidAskEvent("BTC-USD", 49000.0, 49001.0, 1620000000L));
        eventRepository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000060L));
        eventRepository.save(new BidAskEvent("BTC-USD", 51000.0, 51001.0, 1620000120L));
        eventRepository.save(new BidAskEvent("BTC-USD", 52000.0, 52001.0, 1620000180L));

        List<Candle> candles = service.getHistory("BTC-USD", Timeframe.M1, 1620000060, 1620000120);

        assertEquals(2, candles.size());
        assertEquals(1620000060, candles.get(0).getTime());
        assertEquals(1620000120, candles.get(1).getTime());
    }

    @Test
    void testGetHistoryFiltersBySymbol() {
        eventRepository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000060L));
        eventRepository.save(new BidAskEvent("ETH-USD", 3000.0, 3001.0, 1620000060L));

        List<Candle> btcCandles = service.getHistory("BTC-USD", Timeframe.M1, 1620000000, 1620000120);
        List<Candle> ethCandles = service.getHistory("ETH-USD", Timeframe.M1, 1620000000, 1620000120);

        assertEquals(1, btcCandles.size());
        assertEquals(50000.5, btcCandles.get(0).getOpen(), 0.1);
        
        assertEquals(1, ethCandles.size());
        assertEquals(3000.5, ethCandles.get(0).getOpen(), 0.1);
    }
}
