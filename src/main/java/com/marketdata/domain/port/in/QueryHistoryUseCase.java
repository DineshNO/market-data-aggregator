package com.marketdata.domain.port.in;

import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;

import java.util.List;

/**
 * Use case for querying historical candle data.
 * Primary/inbound port for retrieving candles.
 */
public interface QueryHistoryUseCase {
    /**
     * Retrieves historical candle data for a symbol within a time range.
     * Uses Java-based aggregation.
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    List<Candle> getHistory(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec);
    
    /**
     * Retrieves historical candle data using SQL aggregation if available.
     * Falls back to Java aggregation if SQL is not supported.
     * More efficient for large datasets.
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    List<Candle> getHistoryWithSql(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec);
}
