package com.candleservice.domain.port.out;

import com.candleservice.domain.model.BidAskEvent;

import java.util.function.Consumer;

/**
 * Input port: source of market events.
 */
public interface MarketDataSource {
    void start(Consumer<BidAskEvent> handler);
    void stop();
}
