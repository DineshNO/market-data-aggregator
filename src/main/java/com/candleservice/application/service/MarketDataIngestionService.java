package com.candleservice.application.service;

import com.candleservice.domain.model.BidAskEvent;
import com.candleservice.domain.port.in.IngestMarketDataUseCase;
import com.candleservice.domain.port.out.EventRepository;
import com.candleservice.domain.port.out.MarketDataSource;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingests raw bid/ask events and stores them directly.
 * No pre-aggregation or caching - events stored as-is.
 */
public class MarketDataIngestionService implements IngestMarketDataUseCase {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataIngestionService.class);

    private final MarketDataSource source;
    private final EventRepository eventRepository;

    public MarketDataIngestionService(MarketDataSource source, EventRepository eventRepository) {
        this.source = source;
        this.eventRepository = eventRepository;
    }

    /**
     * Starts ingesting market data from the configured source.
     * Events are stored directly without any aggregation.
     */
    public void start() {
        source.start(this::handle);
        logger.info("Market data ingestion started");
    }

    /**
     * Stops market data ingestion.
     * Called automatically on application shutdown.
     */
    @PreDestroy
    public void stop() {
        source.stop();
        logger.info("Market data ingestion stopped");
    }

    private void handle(BidAskEvent event) {
        eventRepository.save(event);
        logger.trace("Stored event: {} at {}", event.symbol(), event.timestampMillis());
    }
}
