package com.marketdata.application.service;

import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.in.QueryHistoryUseCase;
import com.marketdata.domain.port.out.EventRepository;
import com.marketdata.domain.service.CandleAggregator;

import java.util.List;

/**
 * Application service for querying historical candle data.
 * Orchestrates between repository and domain service.
 */
public class HistoryQueryService implements QueryHistoryUseCase {
    private final EventRepository eventRepository;
    private final CandleAggregator candleAggregator;

    public HistoryQueryService(EventRepository eventRepository, CandleAggregator candleAggregator) {
        this.eventRepository = eventRepository;
        this.candleAggregator = candleAggregator;
    }

    /**
     * Retrieves historical candle data for a symbol within a time range.
     * Uses Java-based aggregation (works with any repository).
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    public List<Candle> getHistory(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec) {
        var events = eventRepository.query(symbol, fromEpochSec, toEpochSec);
        return candleAggregator.aggregate(events, timeframe);
    }
    
    /**
     * Retrieves historical candle data using SQL aggregation.
     * More efficient than Java aggregation for large datasets.
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    public List<Candle> getHistoryWithSql(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec) {
        return eventRepository.aggregateCandles(symbol, timeframe, fromEpochSec, toEpochSec);
    }
}
