package com.candleservice.application.service;

import com.candleservice.domain.model.BidAskEvent;
import com.candleservice.domain.model.Candle;
import com.candleservice.domain.model.Timeframe;
import com.candleservice.domain.port.in.QueryHistoryUseCase;
import com.candleservice.domain.port.out.EventRepository;

import java.util.*;

/**
 * Computes candles on-demand from raw bid/ask events.
 * No caching or pre-aggregation - pure computation at query time.
 */
public class HistoryQueryService implements QueryHistoryUseCase {
    private final EventRepository eventRepository;

    public HistoryQueryService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves historical candle data for a symbol within a time range.
     * Candles are computed on-demand from raw bid/ask events.
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    public List<Candle> getHistory(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec) {
        long fromMillis = fromEpochSec * 1000;
        long toMillis = toEpochSec * 1000 + 999;
        
        var events = eventRepository.query(symbol, fromMillis, toMillis);
        
        return computeCandles(events, timeframe);
    }

    private List<Candle> computeCandles(List<BidAskEvent> events, Timeframe timeframe) {
        if (events.isEmpty()) {
            return List.of();
        }
        
        var buckets = new TreeMap<Long, List<BidAskEvent>>();
        
        for (var event : events) {
            var bucketTime = timeframe.bucketStartEpochSeconds(event.timestampMillis());
            buckets.computeIfAbsent(bucketTime, k -> new ArrayList<>()).add(event);
        }
        
        var result = new ArrayList<Candle>();
        for (var entry : buckets.entrySet()) {
            var bucketTime = entry.getKey();
            var eventsInBucket = entry.getValue();
            var candle = buildCandle(bucketTime, eventsInBucket);
            result.add(candle);
        }
        
        return result;
    }

    private Candle buildCandle(long bucketTime, List<BidAskEvent> events) {
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Cannot build candle from empty events");
        }
        
        var firstEvent = events.getFirst();
        var lastEvent = events.getLast();
        
        double open = (firstEvent.bid() + firstEvent.ask()) / 2.0;
        double close = (lastEvent.bid() + lastEvent.ask()) / 2.0;
        double high = Double.NEGATIVE_INFINITY;
        double low = Double.POSITIVE_INFINITY;
        
        for (var event : events) {
            double midPrice = (event.bid() + event.ask()) / 2.0;
            if (midPrice > high) high = midPrice;
            if (midPrice < low) low = midPrice;
        }
        
        return new Candle(bucketTime, open, high, low, close, events.size());
    }
}
