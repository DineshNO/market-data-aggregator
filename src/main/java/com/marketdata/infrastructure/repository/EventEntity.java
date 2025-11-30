package com.marketdata.infrastructure.repository;

import com.marketdata.domain.model.BidAskEvent;
import jakarta.persistence.*;

/**
 * JPA entity for storing raw bid/ask events.
 * Indexed by symbol and timestamp for efficient querying.
 */
@Entity
@Table(name = "bid_ask_events", indexes = {
    @Index(name = "idx_symbol_timestamp", columnList = "symbol, timestamp")
})
public class EventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String symbol;
    
    @Column(nullable = false)
    private double bid;
    
    @Column(nullable = false)
    private double ask;
    
    @Column(name = "timestamp", nullable = false)
    private long timestamp;
    
    protected EventEntity() {
    }
    
    public EventEntity(String symbol, double bid, double ask, long timestamp) {
        this.symbol = symbol;
        this.bid = bid;
        this.ask = ask;
        this.timestamp = timestamp;
    }
    
    public static EventEntity from(BidAskEvent event) {
        return new EventEntity(
            event.symbol(),
            event.bid(),
            event.ask(),
            event.timestamp()
        );
    }
    
    public BidAskEvent toDomain() {
        return new BidAskEvent(symbol, bid, ask, timestamp);
    }
    
    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public double getBid() { return bid; }
    public double getAsk() { return ask; }
    public long getTimestamp() { return timestamp; }
}
