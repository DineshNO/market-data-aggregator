package com.marketdata.infrastructure.repository;

import com.marketdata.domain.model.BidAskEvent;
import com.marketdata.domain.model.Candle;
import com.marketdata.domain.model.Timeframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for PostgresEventRepository with real H2 database.
 * Tests SQL aggregation logic without mocking.
 */
@DataJpaTest
@ActiveProfiles("default")
@Import(PostgresEventRepository.class)
class PostgresEventRepositoryTest {

    @Autowired
    private JpaEventRepository jpaRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private PostgresEventRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostgresEventRepository(jpaRepository, jdbcTemplate);
        jpaRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void testSqlAggregation_SingleBucket() {
        // Given: Multiple events in the same 1-minute bucket
        long baseTime = 1620000000L;
        repository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, baseTime));
        repository.save(new BidAskEvent("BTC-USD", 50100.0, 50101.0, baseTime + 10));
        repository.save(new BidAskEvent("BTC-USD", 49900.0, 49901.0, baseTime + 20));

        // When: Aggregate to 1-minute candles
        List<Candle> candles = repository.aggregateCandles("BTC-USD", Timeframe.M1, baseTime, baseTime + 60);

        // Then: Should return 1 candle with correct OHLC
        assertEquals(1, candles.size());
        Candle candle = candles.get(0);
        assertEquals(1620000000L, candle.getTime()); // Bucket start
        assertEquals(50000.5, candle.getOpen(), 0.1); // First event mid-price
        assertEquals(50100.5, candle.getHigh(), 0.1); // Highest mid-price
        assertEquals(49900.5, candle.getLow(), 0.1);  // Lowest mid-price
        assertEquals(49900.5, candle.getClose(), 0.1); // Last event mid-price
        assertEquals(3, candle.getVolume()); // Event count
    }

    @Test
    void testSqlAggregation_MultipleBuckets() {
        // Given: Events in different 1-minute buckets
        repository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000000L)); // Minute 0
        repository.save(new BidAskEvent("BTC-USD", 51000.0, 51001.0, 1620000060L)); // Minute 1
        repository.save(new BidAskEvent("BTC-USD", 52000.0, 52001.0, 1620000120L)); // Minute 2

        // When: Aggregate
        List<Candle> candles = repository.aggregateCandles("BTC-USD", Timeframe.M1, 1620000000L, 1620000180L);

        // Then: Should return 3 candles sorted by time
        assertEquals(3, candles.size());
        assertEquals(1620000000L, candles.get(0).getTime());
        assertEquals(1620000060L, candles.get(1).getTime());
        assertEquals(1620000120L, candles.get(2).getTime());
    }

    @Test
    void testSqlAggregation_FiltersBySymbol() {
        // Given: Events for different symbols
        repository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000000L));
        repository.save(new BidAskEvent("ETH-USD", 3000.0, 3001.0, 1620000000L));

        // When: Aggregate for BTC only
        List<Candle> btcCandles = repository.aggregateCandles("BTC-USD", Timeframe.M1, 1620000000L, 1620000060L);
        List<Candle> ethCandles = repository.aggregateCandles("ETH-USD", Timeframe.M1, 1620000000L, 1620000060L);

        // Then: Should return separate candles
        assertEquals(1, btcCandles.size());
        assertEquals(50000.5, btcCandles.get(0).getOpen(), 0.1);
        
        assertEquals(1, ethCandles.size());
        assertEquals(3000.5, ethCandles.get(0).getOpen(), 0.1);
    }

    @Test
    void testSqlAggregation_FiltersByTimeRange() {
        // Given: Events at different times
        repository.save(new BidAskEvent("BTC-USD", 49000.0, 49001.0, 1620000000L));
        repository.save(new BidAskEvent("BTC-USD", 50000.0, 50001.0, 1620000060L));
        repository.save(new BidAskEvent("BTC-USD", 51000.0, 51001.0, 1620000120L));
        repository.save(new BidAskEvent("BTC-USD", 52000.0, 52001.0, 1620000180L));

        // When: Query only middle range
        List<Candle> candles = repository.aggregateCandles("BTC-USD", Timeframe.M1, 1620000060L, 1620000120L);

        // Then: Should return only 2 candles
        assertEquals(2, candles.size());
        assertEquals(1620000060L, candles.get(0).getTime());
        assertEquals(1620000120L, candles.get(1).getTime());
    }

    @Test
    void testSqlAggregation_EmptyResult() {
        // Given: No events

        // When: Aggregate
        List<Candle> candles = repository.aggregateCandles("BTC-USD", Timeframe.M1, 1620000000L, 1620000060L);

        // Then: Should return empty list
        assertTrue(candles.isEmpty());
    }

    @Test
    void testSqlAggregation_DifferentTimeframes() {
        // Given: Events spanning 5 minutes
        for (int i = 0; i < 5; i++) {
            repository.save(new BidAskEvent("BTC-USD", 50000.0 + i * 100, 50001.0 + i * 100, 1620000000L + i * 60));
        }

        // When: Aggregate to 5-minute candles
        List<Candle> candles = repository.aggregateCandles("BTC-USD", Timeframe.M5, 1620000000L, 1620000300L);

        // Then: Should return 1 candle covering all events
        assertEquals(1, candles.size());
        assertEquals(5, candles.get(0).getVolume());
    }
}
