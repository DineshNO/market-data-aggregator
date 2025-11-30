package com.candleservice.infrastructure.source;

import com.candleservice.domain.model.BidAskEvent;
import com.candleservice.domain.port.out.MarketDataSource;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class RandomMarketDataSource implements MarketDataSource {

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final Random rnd = new Random();
    private volatile boolean running = false;

    @Override
    public void start(Consumer<BidAskEvent> handler) {
        running = true;
        exec.scheduleAtFixedRate(() -> {
            if (!running) return;
            var now = System.currentTimeMillis();
            var symbols = new String[]{"BTC-USD", "ETH-USD"};
            for (var symbol : symbols) {
                var basePrice = 30_000 + rnd.nextGaussian() * 200;
                var spread = rnd.nextDouble();
                var bid = basePrice - spread;
                var ask = basePrice + spread;
                handler.accept(new BidAskEvent(symbol, bid, ask, now));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        running = false;
        exec.shutdownNow();
    }
}
