package com.candleservice.domain.port.in;

import com.candleservice.domain.model.Candle;
import com.candleservice.domain.model.Timeframe;

import java.util.List;

/**
 * Use case for querying historical candle data.
 * Primary/inbound port for retrieving candles.
 */
public interface QueryHistoryUseCase {
    /**
     * Retrieves historical candle data for a symbol within a time range.
     *
     * @param symbol Trading symbol (e.g., "BTC-USD")
     * @param timeframe Desired candle timeframe
     * @param fromEpochSec Start time in epoch seconds (inclusive)
     * @param toEpochSec End time in epoch seconds (inclusive)
     * @return List of candles sorted by time
     */
    List<Candle> getHistory(String symbol, Timeframe timeframe, long fromEpochSec, long toEpochSec);
}
