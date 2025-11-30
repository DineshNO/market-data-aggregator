package com.marketdata.domain.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps trading interval formats to Timeframe enum values.
 * Supports: 1m, 5m, 15m, 30m, 1h, 5h, 1d, 1w, 1M
 */
public class TimeframeMapper {
    
    private static final Map<String, Timeframe> INTERVAL_MAP = new HashMap<>();
    
    static {
        INTERVAL_MAP.put("1m", Timeframe.M1);
        INTERVAL_MAP.put("5m", Timeframe.M5);
        INTERVAL_MAP.put("15m", Timeframe.M15);
        INTERVAL_MAP.put("30m", Timeframe.M30);
        INTERVAL_MAP.put("1h", Timeframe.H1);
        INTERVAL_MAP.put("5h", Timeframe.H5);
        INTERVAL_MAP.put("1d", Timeframe.D1);
        INTERVAL_MAP.put("1w", Timeframe.W1);
        INTERVAL_MAP.put("1M", Timeframe.MN1);
    }
    
    /**
     * Parse interval string to Timeframe enum.
     * Supports: 1m, 5m, 15m, 30m, 1h, 5h, 1d, 1w, 1M
     * 
     * @param interval the interval string
     * @return the corresponding Timeframe
     * @throws IllegalArgumentException if interval format is not recognized
     */
    public static Timeframe parse(String interval) {
        if (interval == null || interval.isEmpty()) {
            throw new IllegalArgumentException("Interval cannot be null or empty");
        }
        
        var timeframe = INTERVAL_MAP.get(interval);
        if (timeframe == null) {
            throw new IllegalArgumentException(
                "Invalid interval: " + interval + 
                ". Supported formats: 1m, 5m, 15m, 30m, 1h, 5h, 1d, 1w, 1M"
            );
        }
        
        return timeframe;
    }
}
