package com.candleservice;

import com.candleservice.application.service.HistoryQueryService;
import com.candleservice.application.service.MarketDataIngestionService;
import com.candleservice.domain.port.in.IngestMarketDataUseCase;
import com.candleservice.domain.port.in.QueryHistoryUseCase;
import com.candleservice.domain.port.out.EventRepository;
import com.candleservice.domain.port.out.MarketDataSource;
import com.candleservice.infrastructure.repository.InMemoryEventRepository;
import com.candleservice.infrastructure.source.RandomMarketDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CandleServiceConfig {

    @Bean
    public EventRepository eventRepository() {
        return new InMemoryEventRepository();
    }

    @Bean
    public QueryHistoryUseCase queryHistoryUseCase(EventRepository eventRepository) {
        return new HistoryQueryService(eventRepository);
    }

    @Bean
    public MarketDataSource marketDataSource() {
        return new RandomMarketDataSource();
    }

    @Bean
    public IngestMarketDataUseCase ingestMarketDataUseCase(MarketDataSource source, EventRepository eventRepository) {
        var service = new MarketDataIngestionService(source, eventRepository);
        service.start();
        return service;
    }

}
