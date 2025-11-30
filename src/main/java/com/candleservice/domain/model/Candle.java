package com.candleservice.domain.model;

public class Candle {
    private final long time;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;

    public Candle(long time, double open, double high, double low, double close, long volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public long getTime() { return time; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }
}
