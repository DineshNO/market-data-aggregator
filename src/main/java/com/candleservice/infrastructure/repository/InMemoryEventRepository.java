package com.candleservice.infrastructure.repository;

import com.candleservice.domain.model.BidAskEvent;
import com.candleservice.domain.port.out.EventRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;

/**
 * Thread-safe in-memory storage for raw bid/ask events.
 * Events are stored per symbol and sorted by timestamp on query.
 */
public class InMemoryEventRepository implements EventRepository {

    private final Map<String, List<BidAskEvent>> events = new ConcurrentHashMap<>();

    @Override
    public void save(BidAskEvent event) {
        events.computeIfAbsent(event.symbol(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<BidAskEvent> query(String symbol, long fromMillis, long toMillis) {
        var symbolEvents = events.get(symbol);
        if (symbolEvents == null) {
            return List.of();
        }

        return symbolEvents.stream()
                .filter(e -> e.timestampMillis() >= fromMillis && e.timestampMillis() <= toMillis)
                .sorted(comparingLong(BidAskEvent::timestampMillis))
                .toList();
    }
}
