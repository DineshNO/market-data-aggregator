package com.marketdata.infrastructure.source;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.port.out.MarketDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Skeleton implementation for consuming market data from Kafka.
 * This is a placeholder for future integration with Kafka-based market data feeds.
 * 
 * To implement:
 * 1. Add Kafka dependencies (spring-kafka)
 * 2. Configure Kafka consumer properties
 * 3. Subscribe to market data topic
 * 4. Deserialize messages to BidAskEvent
 * 5. Handle consumer lifecycle and error handling
 */
public class KafkaMarketDataSource implements MarketDataSource {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMarketDataSource.class);

    
    @Override
    public void start(Consumer<BidAskEvent> handler) {
        logger.info("Kafka market data source - not implemented");
        throw new UnsupportedOperationException("Kafka source not implemented yet");
    }
    
    @Override
    public void stop() {
        logger.info("Stopping Kafka market data source");
    }
}
