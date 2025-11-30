# Market Data Aggregator

A high-performance Java backend service that aggregates real-time bid/ask market data into OHLCV (Open, High, Low, Close, Volume) candlestick charts for multiple timeframes.

## 📋 Project Overview

This service processes streaming market data events and provides historical candlestick data through a REST API. It's designed for scalability, maintainability, and extensibility.

### Key Features

- ✅ **Real-time ingestion** of bid/ask market data events
- ✅ **SQL-based aggregation** using window functions for OHLCV candles
- ✅ **Multiple timeframes**: 1m, 5m, 15m, 30m, 1h, 4h, 1d
- ✅ **Multiple symbols**: BTC-USD, ETH-USD (easily extensible)
- ✅ **REST API** with TradingView Lightweight Charts format
- ✅ **Input validation** on all API endpoints
- ✅ **Caching** with 5-minute TTL for query performance
- ✅ **Health check** endpoint for monitoring
- ✅ **Comprehensive tests** (37 tests including integration tests)

### Architecture

Built using **Hexagonal Architecture (Ports & Adapters)** with clean separation of concerns:

```
domain/              # Pure business logic
├── model/          # Entities: Candle, BidAskEvent, Timeframe
├── service/        # Domain services: CandleAggregator
└── port/           # Interfaces (dependency inversion)

application/        # Use cases
└── service/        # Application services

infrastructure/     # External adapters
├── repository/     # Database implementations
└── source/         # Market data sources

api/                # REST API
├── controller/     # HTTP endpoints
├── dto/            # Data transfer objects
└── exception/      # Exception handlers
```

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd market-data-aggregator

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### Example API Usage

**Get historical candles:**

```bash
# Get BTC-USD 1-minute candles for the last 5 minutes
NOW=$(date +%s)
FROM=$((NOW - 300))

curl "http://localhost:8080/history/sql?symbol=BTC-USD&interval=1m&from=$FROM&to=$NOW"
```

**Response:**

```json
{
  "s": "ok",
  "t": [1764508320, 1764508380, 1764508440, 1764508500, 1764508560],
  "o": [88163.74, 86415.40, 91184.84, 80667.25, 90805.35],
  "h": [103351.57, 99855.89, 103340.50, 101887.55, 98102.12],
  "l": [78130.39, 81942.99, 78624.09, 78576.98, 80131.23],
  "c": [93261.57, 95034.05, 95485.64, 86807.74, 94948.95],
  "v": [56, 60, 60, 60, 13]
}
```

**Check service health:**

```bash
curl http://localhost:8080/health
```

**Response:**

```json
{
  "status": "UP",
  "service": "market-data-aggregator",
  "timestamp": 1764508584
}
```

## 📊 API Endpoints

### 1. Query Historical Candles (Java Aggregation)

```
GET /history?symbol={symbol}&interval={interval}&from={from}&to={to}
```

**Description**: Retrieves historical candle data using **Java-based aggregation**. Queries raw events from the database and computes candles in-memory using the `CandleAggregator` domain service.

**Parameters**:
- `symbol` (required): Trading pair (e.g., `BTC-USD`, `ETH-USD`)
- `interval` (required): Timeframe (`1m`, `5m`, `15m`, `30m`, `1h`, `4h`, `1d`)
- `from` (required): Start timestamp (Unix seconds, >= 0)
- `to` (required): End timestamp (Unix seconds, >= from)

**Use Case**: Works with any database, portable, good for small datasets

**Example**:
```bash
curl "http://localhost:8080/history?symbol=BTC-USD&interval=5m&from=1732968000&to=1732969800"
```

---

### 2. Query Historical Candles (SQL Aggregation)

```
GET /history/sql?symbol={symbol}&interval={interval}&from={from}&to={to}
```

**Description**: Retrieves historical candle data using **SQL-based aggregation** with window functions. More efficient for large datasets as aggregation happens in the database.

**Parameters**: Same as `/history`

**Use Case**: Production use, better performance for large datasets, leverages database optimization

**Caching**: Results are cached for 5 minutes to reduce database load

**Example**:
```bash
curl "http://localhost:8080/history/sql?symbol=BTC-USD&interval=5m&from=1732968000&to=1732969800"
```

---

### 3. Health Check

```
GET /health
```

**Description**: Returns service health status for monitoring and load balancers.

**Response**:
```json
{
  "status": "UP",
  "service": "market-data-aggregator",
  "timestamp": 1732968584
}
```

**Example**:
```bash
curl http://localhost:8080/health
```

---

### Response Format (TradingView Compatible)

Both history endpoints return data in TradingView Lightweight Charts format:

```json
{
  "s": "ok",
  "t": [1732968000, 1732968300, 1732968600],
  "o": [90123.45, 90234.56, 90345.67],
  "h": [90500.00, 90600.00, 90700.00],
  "l": [90000.00, 90100.00, 90200.00],
  "c": [90234.56, 90345.67, 90456.78],
  "v": [120, 115, 118]
}
```

**Fields**:
- `s`: Status (`ok` or `no_data`)
- `t`: Timestamps (Unix seconds)
- `o`: Open prices
- `h`: High prices
- `l`: Low prices
- `c`: Close prices
- `v`: Volume (event count)

---

### Comparison: `/history` vs `/history/sql`

| Aspect | `/history` (Java) | `/history/sql` (SQL) |
|--------|-------------------|----------------------|
| **Aggregation** | In-memory (Java) | Database (SQL) |
| **Performance** | Good for small data | Better for large data |
| **Caching** | No | Yes (5 min TTL) |
| **Portability** | Works anywhere | Requires SQL window functions |
| **Use Case** | Development, testing | Production |
| **Database Load** | Higher (fetches all events) | Lower (aggregates in DB) |

**Recommendation**: Use `/history/sql` for production queries.

## 🧪 Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=PostgresEventRepositoryTest
```

### Test Coverage

- **37 tests total**
- Unit tests for domain logic
- Integration tests for SQL aggregation
- Controller tests with MockMvc
- Repository tests with H2 in-memory database

**Key test classes:**

- `PostgresEventRepositoryTest` - SQL aggregation integration tests (6 tests)
- `MarketDataAggregatorIntegrationTest` - End-to-end integration tests
- `HistoryControllerTest` - REST API tests
- `TimeframeTest`, `CandleTest` - Domain model tests

## 📐 Assumptions & Design Decisions

### Assumptions

1. **Timestamps in seconds**: All timestamps are Unix epoch in seconds (not milliseconds)
2. **Event ordering**: Events arrive roughly in order (small delays acceptable)
3. **Symbol format**: Symbols follow `BASE-QUOTE` format (e.g., BTC-USD)
4. **Limited symbols**: Currently generates data for BTC-USD and ETH-USD only (easily extensible to more symbols)
5. **Development environment**: H2 in-memory database for simplicity (production would use PostgreSQL)
6. **Data generation timing**: Events are generated from application startup time onwards (forward-moving only). No historical data is pre-loaded - you need to wait a few minutes after startup to accumulate data for queries
7. **Event frequency**: Current implementation generates 1 event/second per symbol (2 events/second total). For production high-frequency scenarios (100+ events/second), consider implementing batch inserts and async processing

### Design Decisions

#### 1. Dual Aggregation Strategy

**Decision**: Provide both SQL-based and Java-based aggregation options

**Rationale**:
- ✅ **SQL aggregation** (`/history/sql`): Leverages database optimization, better for large datasets
- ✅ **Java aggregation** (`/history`): Portable, works with any database, good for small datasets
- ✅ Flexibility to choose based on use case
- ✅ SQL version is cached for better performance
#### 2. Direct Event Storage

**Decision**: Save events directly without batching

**Rationale**:
- ✅ Simpler implementation
- ✅ Lower latency per event
- ✅ No risk of data loss from buffer
- ✅ Easier to debug and test

**Trade-off**: More database round-trips (acceptable for development; production would use batch inserts)

#### 3. Caching Strategy

**Decision**: Cache at repository layer with 5-minute TTL using Caffeine (in-memory cache)

**Rationale**:
- ✅ Reduces database load for repeated queries
- ✅ Reusable across multiple consumers
- ✅ Transparent to API layer
- ✅ Appropriate TTL for historical data

**Note**: Caffeine is an application-level (in-memory) cache suitable for development and single-instance deployments. For production with multiple instances, it can be easily replaced with Redis or other distributed cache solutions by swapping the Spring `CacheManager` implementation without any code changes.

**Trade-off**: Stale data for up to 5 minutes (acceptable for historical queries)

#### 4. In-Memory Database

**Decision**: Use H2 in-memory database for development

**Rationale**:
- ✅ Zero setup required
- ✅ Fast for testing
- ✅ PostgreSQL-compatible mode
- ✅ Easy to switch to PostgreSQL in production

**Trade-off**: Data lost on restart (acceptable for development)

## ⚠️ Limitations & Trade-offs

### Current Limitations

1. **No persistence across restarts**: In-memory database clears on restart
2. **Single instance**: No distributed processing or horizontal scaling
3. **No event replay**: Cannot replay missed events after downtime
4. **Basic error handling**: Limited retry logic for failures
5. **No authentication**: API is publicly accessible

### Known Trade-offs

| Aspect | Current Choice | Alternative | Reason |
|--------|---------------|-------------|--------|
| **Database** | H2 in-memory | PostgreSQL | Simplicity for development |
| **Batching** | Direct save | Batch inserts | Simpler code, lower latency |
| **Aggregation** | SQL | In-memory Java | Better performance at scale |
| **Caching** | 5-min TTL | Shorter/longer | Balance freshness vs load |
| **Validation** | Spring Validation | Custom | Standard, well-tested |

## 🏗️ Technology Stack

- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3.2** - Application framework
- **Spring Data JPA** - Database abstraction
- **H2 Database** - In-memory database (PostgreSQL mode)
- **Caffeine** - High-performance caching
- **SLF4J + Logback** - Logging
- **JUnit 5** - Testing framework
- **Maven** - Build tool

## 🔧 Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database (switch to PostgreSQL for production)
spring.datasource.url=jdbc:h2:mem:candledb;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver

# Logging
logging.level.com.market-data-aggregator=DEBUG
```

## 🚀 Production Considerations

For production deployment, consider:

1. **Database**: Switch to PostgreSQL with connection pooling
2. **High-frequency ingestion**: For high-throughput scenarios (100+ events/second):
   - Implement batch inserts (buffer events and flush periodically)
   - Use async processing with CompletableFuture
   - Add connection pooling (HikariCP)
   - Consider message queue (Kafka) for decoupling
3. **Batching**: Implement batch inserts for high-throughput ingestion
4. **Scaling**: Add horizontal scaling with load balancer
5. **Monitoring**: Add metrics (Prometheus), distributed tracing
6. **Security**: Add authentication, rate limiting, HTTPS
7. **Event replay**: Implement event sourcing or Kafka for reliability
8. **Caching**: Use Redis for distributed caching
9. **Health checks**: Add detailed health indicators (database, dependencies)

## 📝 Future Enhancements

- [ ] Kafka integration for event ingestion
- [ ] More symbols and exchanges
- [ ] Custom timeframe support
- [ ] Data retention policies
- [ ] Metrics and monitoring dashboard
- [ ] API rate limiting
- [ ] Authentication and authorization

## 📄 License

This project is for demonstration purposes.

---

