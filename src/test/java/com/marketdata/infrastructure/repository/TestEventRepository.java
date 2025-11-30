package com.marketdata.infrastructure.repository;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.out.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Comparator.comparingLong;

/**
 * In-memory test implementation of EventRepository.
 * Used for unit and integration tests.
 */
public class TestEventRepository implements EventRepository {

    private final Map<String, List<BidAskEvent>> events = new ConcurrentHashMap<>();

    @Override
    public void save(BidAskEvent event) {
        events.computeIfAbsent(event.symbol(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<BidAskEvent> query(String symbol, long from, long to) {
        var symbolEvents = events.get(symbol);
        if (symbolEvents == null) {
            return List.of();
        }

        return symbolEvents.stream()
                .filter(e -> e.timestamp() >= from && e.timestamp() <= to)
                .sorted(comparingLong(BidAskEvent::timestamp))
                .toList();
    }

    @Override
    public List<Candle> aggregateCandles(String symbol, Timeframe timeframe, long from, long to) {
        // Test implementation doesn't support SQL aggregation
        return List.of();
    }
}
