package com.candleservice.integration;

import com.candleservice.application.service.HistoryQueryService;
import com.candleservice.domain.model.BidAskEvent;
import com.candleservice.domain.model.Candle;
import com.candleservice.domain.model.Timeframe;
import com.candleservice.domain.port.in.QueryHistoryUseCase;
import com.candleservice.infrastructure.repository.InMemoryEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test covering the full flow: event storage -> on-demand candle computation -> query
 */
class CandleServiceIntegrationTest {

    private InMemoryEventRepository eventRepository;
    private HistoryQueryService historyQueryService;

    @BeforeEach
    void setUp() {
        eventRepository = new InMemoryEventRepository();
        historyQueryService = new HistoryQueryService(eventRepository);
    }

    @Test
    void testFullCandleLifecycle() {
        String symbol = "BTC-USD";
        long baseTime = 1620000000000L; // Base timestamp in milliseconds
        
        // Store multiple events in the same 1-second bucket
        BidAskEvent e1 = new BidAskEvent(symbol, 50000.0, 50001.0, baseTime);
        BidAskEvent e2 = new BidAskEvent(symbol, 50100.0, 50101.0, baseTime + 200);
        BidAskEvent e3 = new BidAskEvent(symbol, 49900.0, 49901.0, baseTime + 500);
        
        eventRepository.save(e1);
        eventRepository.save(e2);
        eventRepository.save(e3);
        
        // Query and compute candles on-demand
        long bucketStart = Timeframe.S1.bucketStartEpochSeconds(baseTime);
        List<Candle> candles = historyQueryService.getHistory(symbol, Timeframe.S1, bucketStart, bucketStart);
        
        // Verify
        assertEquals(1, candles.size());
        Candle candle = candles.get(0);
        assertEquals(bucketStart, candle.getTime());
        assertEquals(50000.5, candle.getOpen(), 0.1); // (50000 + 50001) / 2
        assertEquals(50100.5, candle.getHigh(), 0.1); // (50100 + 50101) / 2
        assertEquals(49900.5, candle.getLow(), 0.1);  // (49900 + 49901) / 2
        assertEquals(3, candle.getVolume());
    }

    @Test
    void testMultipleSymbolsAndTimeframes() {
        long baseTime = 1620000000000L;
        
        eventRepository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, baseTime));
        eventRepository.save(new BidAskEvent("ETH-USD", 3000.0, 3001.0, baseTime));
        
        long bucket = Timeframe.M1.bucketStartEpochSeconds(baseTime);
        List<Candle> btcCandles = historyQueryService.getHistory("BTC-USD", Timeframe.M1, bucket, bucket);
        List<Candle> ethCandles = historyQueryService.getHistory("ETH-USD", Timeframe.M1, bucket, bucket);
        
        assertEquals(1, btcCandles.size());
        assertEquals(1, ethCandles.size());
        assertEquals(50000.5, btcCandles.get(0).getOpen(), 0.1);
        assertEquals(3000.5, ethCandles.get(0).getOpen(), 0.1);
    }

    @Test
    void testMultipleBucketsInSameTimeframe() {
        String symbol = "BTC-USD";
        
        // Events in different 1-second buckets
        long time1 = 1620000000000L;
        long time2 = 1620000001000L; // 1 second later
        long time3 = 1620000002000L; // 2 seconds later
        
        eventRepository.save(new BidAskEvent(symbol, 50000.0, 50001.0, time1));
        eventRepository.save(new BidAskEvent(symbol, 51000.0, 51001.0, time2));
        eventRepository.save(new BidAskEvent(symbol, 52000.0, 52001.0, time3));
        
        // Query range
        long startBucket = Timeframe.S1.bucketStartEpochSeconds(time1);
        long endBucket = Timeframe.S1.bucketStartEpochSeconds(time3);
        List<Candle> candles = historyQueryService.getHistory(symbol, Timeframe.S1, startBucket, endBucket);
        
        assertEquals(3, candles.size());
        assertEquals(50000.5, candles.get(0).getOpen(), 0.1);
        assertEquals(51000.5, candles.get(1).getOpen(), 0.1);
        assertEquals(52000.5, candles.get(2).getOpen(), 0.1);
    }
}
