package com.candleservice.domain.port.in;

/**
 * Use case for ingesting market data.
 * Primary/inbound port for starting and stopping data ingestion.
 */
public interface IngestMarketDataUseCase {
    /**
     * Starts ingesting market data from the configured source.
     */
    void start();

    /**
     * Stops market data ingestion.
     */
    void stop();
}
