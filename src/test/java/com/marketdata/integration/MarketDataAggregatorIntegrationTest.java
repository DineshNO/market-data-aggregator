package com.marketdata.integration;

import com.marketdata.application.service.HistoryQueryService;
import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import com.marketdata.infrastructure.repository.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test covering the full flow: event storage -> on-demand candle computation -> query
 */
class MarketDataAggregatorIntegrationTest {

    private TestEventRepository eventRepository;
    private HistoryQueryService historyQueryService;

    @BeforeEach
    void setUp() {
        eventRepository = new TestEventRepository();
        historyQueryService = new HistoryQueryService(eventRepository, new com.marketdata.domain.service.CandleAggregator());
    }

    @Test
    void testFullCandleLifecycle() {
        String symbol = "BTC-USD";
        long baseTime = 1620000000L; // Base timestamp in seconds
        
        // Store multiple events in the same 1-minute bucket
        BidAskEvent e1 = new BidAskEvent(symbol, 50000.0, 50001.0, baseTime);
        BidAskEvent e2 = new BidAskEvent(symbol, 50100.0, 50101.0, baseTime);
        BidAskEvent e3 = new BidAskEvent(symbol, 49900.0, 49901.0, baseTime);
        
        eventRepository.save(e1);
        eventRepository.save(e2);
        eventRepository.save(e3);
        
        // Query and compute candles on-demand
        long bucketStart = Timeframe.M1.bucketStart(baseTime);
        List<Candle> candles = historyQueryService.getHistory(symbol, Timeframe.M1, bucketStart, bucketStart);
        
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
        long baseTime = 1620000000L;
        
        eventRepository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, baseTime));
        eventRepository.save(new BidAskEvent("ETH-USD", 3000.0, 3001.0, baseTime));
        
        long bucket = Timeframe.M1.bucketStart(baseTime);
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
        
        // Events in different 1-minute buckets
        long time1 = 1620000000L;
        long time2 = 1620000060L; // 1 minute later
        long time3 = 1620000120L; // 2 minutes later
        
        eventRepository.save(new BidAskEvent(symbol, 50000.0, 50001.0, time1));
        eventRepository.save(new BidAskEvent(symbol, 51000.0, 51001.0, time2));
        eventRepository.save(new BidAskEvent(symbol, 52000.0, 52001.0, time3));
        
        // Query range
        long startBucket = Timeframe.M1.bucketStart(time1);
        long endBucket = Timeframe.M1.bucketStart(time3);
        List<Candle> candles = historyQueryService.getHistory(symbol, Timeframe.M1, startBucket, endBucket);
        
        assertEquals(3, candles.size());
        assertEquals(50000.5, candles.get(0).getOpen(), 0.1);
        assertEquals(51000.5, candles.get(1).getOpen(), 0.1);
        assertEquals(52000.5, candles.get(2).getOpen(), 0.1);
    }
}
