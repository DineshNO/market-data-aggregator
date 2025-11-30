package com.marketdata.domain.port.out;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;

import java.util.List;

/**
 * Repository for storing and querying raw bid/ask events.
 * No pre-aggregation or caching - events are stored as-is.
 */
public interface EventRepository {
    /**
     * Stores a raw bid/ask event.
     *
     * @param event The bid/ask event to store
     */
    void save(BidAskEvent event);
    
    /**
     * Queries events for a symbol within a time range.
     *
     * @param symbol Trading symbol
     * @param from Start time (inclusive) in seconds
     * @param to End time (inclusive) in seconds
     * @return List of events sorted by timestamp
     */
    List<BidAskEvent> query(String symbol, long from, long to);
    
    /**
     * Aggregates events into candles using SQL.
     * More efficient than querying events and computing in Java.
     *
     * @param symbol Trading symbol
     * @param timeframe Desired candle timeframe
     * @param from Start time (inclusive) in seconds
     * @param to End time (inclusive) in seconds
     * @return List of candles sorted by time
     */
    List<Candle> aggregateCandles(String symbol, Timeframe timeframe, long from, long to);
}
