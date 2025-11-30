package com.marketdata.domain.service;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Domain service for aggregating bid/ask events into OHLCV candles.
 * Contains pure business logic with no infrastructure dependencies.
 */
public class CandleAggregator {
    
    /**
     * Aggregates a list of bid/ask events into candles for the specified timeframe.
     * 
     * @param events List of bid/ask events (should be sorted by timestamp)
     * @param timeframe Desired candle timeframe
     * @return List of candles, one per time bucket
     */
    public List<Candle> aggregate(List<BidAskEvent> events, Timeframe timeframe) {
        if (events.isEmpty()) {
            return List.of();
        }
        
        var buckets = bucketEvents(events, timeframe);
        return buildCandles(buckets);
    }
    
    /**
     * Groups events into time buckets based on the timeframe.
     */
    private TreeMap<Long, List<BidAskEvent>> bucketEvents(List<BidAskEvent> events, Timeframe timeframe) {
        var buckets = new TreeMap<Long, List<BidAskEvent>>();
        
        for (var event : events) {
            var bucketTime = timeframe.bucketStart(event.timestamp());
            buckets.computeIfAbsent(bucketTime, k -> new ArrayList<>()).add(event);
        }
        
        return buckets;
    }
    
    /**
     * Builds candles from bucketed events.
     */
    private List<Candle> buildCandles(TreeMap<Long, List<BidAskEvent>> buckets) {
        var result = new ArrayList<Candle>();
        
        for (var entry : buckets.entrySet()) {
            var bucketTime = entry.getKey();
            var eventsInBucket = entry.getValue();
            var candle = buildCandle(bucketTime, eventsInBucket);
            result.add(candle);
        }
        
        return result;
    }
    
    /**
     * Builds a single candle from events in a time bucket.
     * Computes OHLCV (Open, High, Low, Close, Volume) from bid/ask events.
     */
    private Candle buildCandle(long bucketTime, List<BidAskEvent> events) {
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Cannot build candle from empty events");
        }
        
        var firstEvent = events.getFirst();
        var lastEvent = events.getLast();
        
        double open = midPrice(firstEvent);
        double close = midPrice(lastEvent);
        double high = Double.NEGATIVE_INFINITY;
        double low = Double.POSITIVE_INFINITY;
        
        for (var event : events) {
            double mid = midPrice(event);
            if (mid > high) high = mid;
            if (mid < low) low = mid;
        }
        
        return new Candle(bucketTime, open, high, low, close, events.size());
    }
    
    /**
     * Calculates mid-price from bid and ask.
     */
    private double midPrice(BidAskEvent event) {
        return (event.bid() + event.ask()) / 2.0;
    }
}
