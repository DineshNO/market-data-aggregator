package com.candleservice.domain.model;

public record BidAskEvent(String symbol, double bid, double ask, long timestampMillis) { }
