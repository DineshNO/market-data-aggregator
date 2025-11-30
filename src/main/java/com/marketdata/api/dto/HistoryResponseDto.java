package com.marketdata.api.dto;

import com.marketdata.domain.model.Candle;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Trading View Lightweight Charts format response.
 */
public class HistoryResponseDto {

    @JsonProperty("s")
    private String status;

    @JsonProperty("t")
    private List<Long> timestamps;

    @JsonProperty("o")
    private List<Double> open;

    @JsonProperty("h")
    private List<Double> high;

    @JsonProperty("l")
    private List<Double> low;

    @JsonProperty("c")
    private List<Double> close;

    @JsonProperty("v")
    private List<Long> volume;

    @JsonProperty("errmsg")
    private String errorMessage;

    public HistoryResponseDto() {
        this.status = "ok";
        this.timestamps = new ArrayList<>();
        this.open = new ArrayList<>();
        this.high = new ArrayList<>();
        this.low = new ArrayList<>();
        this.close = new ArrayList<>();
        this.volume = new ArrayList<>();
    }

    public static HistoryResponseDto from(List<Candle> candles) {
        var dto = new HistoryResponseDto();
        for (var candle : candles) {
            dto.timestamps.add(candle.getTime());
            dto.open.add(candle.getOpen());
            dto.high.add(candle.getHigh());
            dto.low.add(candle.getLow());
            dto.close.add(candle.getClose());
            dto.volume.add(candle.getVolume());
        }
        return dto;
    }

    public static HistoryResponseDto error(String message) {
        var dto = new HistoryResponseDto();
        dto.status = "error";
        dto.errorMessage = message;
        return dto;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Long> getTimestamps() { return timestamps; }
    public void setTimestamps(List<Long> timestamps) { this.timestamps = timestamps; }

    public List<Double> getOpen() { return open; }
    public void setOpen(List<Double> open) { this.open = open; }

    public List<Double> getHigh() { return high; }
    public void setHigh(List<Double> high) { this.high = high; }

    public List<Double> getLow() { return low; }
    public void setLow(List<Double> low) { this.low = low; }

    public List<Double> getClose() { return close; }
    public void setClose(List<Double> close) { this.close = close; }

    public List<Long> getVolume() { return volume; }
    public void setVolume(List<Long> volume) { this.volume = volume; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
