package com.marketdata;

import com.marketdata.application.service.HistoryQueryService;
import com.marketdata.application.service.MarketDataIngestionService;
import com.marketdata.domain.port.in.IngestMarketDataUseCase;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import com.marketdata.domain.port.out.EventRepository;
import com.marketdata.domain.port.out.MarketDataSource;
import com.marketdata.domain.service.CandleAggregator;
import com.marketdata.infrastructure.source.RandomMarketDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarketDataAggregatorConfig {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataAggregatorConfig.class);

    @Bean
    public CandleAggregator candleAggregator() {
        return new CandleAggregator();
    }

    @Bean
    public QueryHistoryUseCase queryHistoryUseCase(EventRepository eventRepository, CandleAggregator candleAggregator) {
        return new HistoryQueryService(eventRepository, candleAggregator);
    }

    @Bean
    public MarketDataSource marketDataSource() {
        return new RandomMarketDataSource();
    }

    /**
     * Auto-starts market data ingestion on application startup.
     * Events are continuously generated and stored in the database.
     */
    @Bean
    public IngestMarketDataUseCase ingestMarketDataUseCase(MarketDataSource source, EventRepository eventRepository) {
        var service = new MarketDataIngestionService(source, eventRepository);
        service.start();
        logger.info("✅ Market data ingestion started - events will be stored in database");
        return service;
    }

}
