package com.marketdata.infrastructure.source;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.port.out.MarketDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RandomMarketDataSource implements MarketDataSource {
    private static final Logger logger = LoggerFactory.getLogger(RandomMarketDataSource.class);
    private static final long MILLIS_TO_SECONDS = 1000L;

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final Random rnd = new Random();
    private volatile boolean running = false;

    @Override
    public void start(Consumer<BidAskEvent> handler) {
        running = true;
        logger.info("Starting random market data generation (every 1 second)");
        exec.scheduleAtFixedRate(() -> generateEvents(handler), 0, 1, TimeUnit.SECONDS);
    }
    
    private void generateEvents(Consumer<BidAskEvent> handler) {
        if (!running) return;
        
        var now = System.currentTimeMillis() / MILLIS_TO_SECONDS;
        
        generateEventForSymbol("BTC-USD", 90_000.0, 5000.0, now, handler);
        generateEventForSymbol("ETH-USD", 3_500.0, 200.0, now, handler);
    }
    
    private void generateEventForSymbol(String symbol, double basePrice, double volatility, long timestamp, Consumer<BidAskEvent> handler) {
        var price = basePrice + rnd.nextGaussian() * volatility;
        var spread = rnd.nextDouble();
        var bid = price - spread;
        var ask = price + spread;
        var event = new BidAskEvent(symbol, bid, ask, timestamp);
        handler.accept(event);
    }

    @Override
    public void stop() {
        running = false;
        exec.shutdownNow();
        logger.info("Stopped market data generation");
    }
}
