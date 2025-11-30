package com.marketdata.infrastructure.repository;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import com.marketdata.domain.port.out.EventRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stores bid/ask events in PostgreSQL database.
 * Provides SQL-based candle aggregation for efficient querying.
 */
@Component
@Profile({"postgres", "default"})
public class PostgresEventRepository implements EventRepository {
    
    private static final String AGGREGATE_CANDLES_SQL = """
        SELECT
            bucket_time,
            (first_bid + first_ask) / 2.0 AS open,
            MAX((bid + ask) / 2.0) AS high,
            MIN((bid + ask) / 2.0) AS low,
            (last_bid + last_ask) / 2.0 AS close,
            COUNT(*) AS volume
        FROM (
            SELECT
                FLOOR(timestamp / :intervalSeconds) * :intervalSeconds AS bucket_time,
                bid,
                ask,
                FIRST_VALUE(bid) OVER (PARTITION BY FLOOR(timestamp / :intervalSeconds) ORDER BY timestamp) AS first_bid,
                FIRST_VALUE(ask) OVER (PARTITION BY FLOOR(timestamp / :intervalSeconds) ORDER BY timestamp) AS first_ask,
                FIRST_VALUE(bid) OVER (PARTITION BY FLOOR(timestamp / :intervalSeconds) ORDER BY timestamp DESC) AS last_bid,
                FIRST_VALUE(ask) OVER (PARTITION BY FLOOR(timestamp / :intervalSeconds) ORDER BY timestamp DESC) AS last_ask
            FROM bid_ask_events
            WHERE symbol = :symbol
              AND timestamp >= :from
              AND timestamp <= :to
        ) AS bucketed
        GROUP BY bucket_time, first_bid, first_ask, last_bid, last_ask
        ORDER BY bucket_time
        """;
    
    private final JpaEventRepository jpaRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    public PostgresEventRepository(JpaEventRepository jpaRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public void save(BidAskEvent event) {
        jpaRepository.save(EventEntity.from(event));
    }
    
    @Override
    public List<BidAskEvent> query(String symbol, long from, long to) {
        return jpaRepository
            .findBySymbolAndTimestampBetween(symbol, from, to)
            .stream()
            .map(EventEntity::toDomain)
            .toList();
    }
    
    /**
     * SQL-based candle aggregation using JDBC for type-safe result mapping.
     * More efficient than Java aggregation for large datasets.
     * Results are cached for 5 minutes to reduce database load.
     */
    @Override
    @Cacheable(value = "candles", key = "#symbol + '-' + #timeframe + '-' + #from + '-' + #to")
    public List<Candle> aggregateCandles(String symbol, Timeframe timeframe, long from, long to) {
        long intervalSeconds = timeframe.durationSeconds();
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("symbol", symbol)
            .addValue("from", from)
            .addValue("to", to)
            .addValue("intervalSeconds", intervalSeconds);
        
        return jdbcTemplate.query(AGGREGATE_CANDLES_SQL, params,
            (rs, rowNum) -> new Candle(
                rs.getLong("bucket_time"),
                rs.getDouble("open"),
                rs.getDouble("high"),
                rs.getDouble("low"),
                rs.getDouble("close"),
                rs.getLong("volume")
            )
        );
    }
}
