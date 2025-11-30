package com.marketdata.application.service;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.port.in.IngestMarketDataUseCase;
import com.marketdata.domain.port.out.EventRepository;
import com.marketdata.domain.port.out.MarketDataSource;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketDataIngestionService implements IngestMarketDataUseCase {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataIngestionService.class);

    private final MarketDataSource source;
    private final EventRepository eventRepository;

    public MarketDataIngestionService(MarketDataSource source, EventRepository eventRepository) {
        this.source = source;
        this.eventRepository = eventRepository;
    }

    public void start() {
        source.start(this::handle);
        logger.info("Market data ingestion started");
    }

    @PreDestroy
    public void stop() {
        source.stop();
        logger.info("Market data ingestion stopped");
    }

    private void handle(BidAskEvent event) {
        eventRepository.save(event);
        logger.trace("Stored event: {} at {}", event.symbol(), event.timestamp());
    }
}
