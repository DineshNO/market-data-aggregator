package com.candleservice.domain.port.out;

import com.candleservice.domain.model.BidAskEvent;

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
     * @param fromMillis Start time (inclusive) in milliseconds
     * @param toMillis End time (inclusive) in milliseconds
     * @return List of events sorted by timestamp
     */
    List<BidAskEvent> query(String symbol, long fromMillis, long toMillis);
}
