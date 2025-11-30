package com.marketdata.domain.model;

public enum Timeframe {
    M1(60),
    M5(300),
    M15(900),
    M30(1800),
    H1(3600),
    H5(18000),
    D1(86400),
    W1(604800),
    MN1(2592000);

    private final long seconds;

    Timeframe(long seconds) { this.seconds = seconds; }

    public long getSeconds() { return seconds; }
    
    public long durationSeconds() { return seconds; }

    /**
     * Calculates the bucket start time in epoch seconds from epoch milliseconds.
     * Rounds down to the nearest timeframe boundary.
     * 
     * @param epochMilli timestamp in milliseconds
     * @return bucket start time in epoch seconds
     */
    public long bucketStartEpochSeconds(long epochMilli) {
        long epochSec = epochMilli / 1000;
        return (epochSec / seconds) * seconds;
    }
    
    /**
     * Calculates the bucket start time for a given timestamp.
     * Rounds down to the nearest timeframe boundary using integer division.
     * 
     * Example: For 5-minute buckets (300 seconds):
     *   - Input: 1620000123 (14:35:23)
     *   - Output: 1620000000 (14:35:00)
     * 
     * @param epochSec timestamp in epoch seconds
     * @return bucket start time in epoch seconds, aligned to timeframe boundary
     */
    public long bucketStart(long epochSec) {
        return (epochSec / seconds) * seconds;
    }
}
