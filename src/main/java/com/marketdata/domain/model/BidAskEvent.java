package com.marketdata.domain.model;

/**
 * Represents a bid/ask price quote at a specific point in time.
 * 
 * @param symbol Trading pair (e.g., "BTC-USD")
 * @param bid Bid price
 * @param ask Ask price
 * @param timestamp Unix timestamp in seconds
 */
public record BidAskEvent(String symbol, double bid, double ask, long timestamp) { }
