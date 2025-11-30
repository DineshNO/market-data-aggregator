package com.marketdata.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for bid/ask events.
 * Handles simple CRUD operations and queries.
 */
@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, Long> {
    
    /**
     * Find events by symbol and timestamp range.
     * Used for Java-based candle aggregation.
     *
     * @param symbol Trading symbol
     * @param from Start time in seconds (inclusive)
     * @param to End time in seconds (inclusive)
     * @return List of events sorted by timestamp
     */
    List<EventEntity> findBySymbolAndTimestampBetween(
        String symbol, 
        long from, 
        long to
    );
}
