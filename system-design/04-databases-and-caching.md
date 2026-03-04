# Databases & Caching

---

## SQL vs NoSQL Decision Matrix

| Factor | SQL (PostgreSQL, MySQL) | NoSQL |
|--------|------------------------|-------|
| **Data model** | Fixed schema, relational | Flexible schema |
| **Consistency** | Strong (ACID) | Eventual (configurable) |
| **Scalability** | Vertical + limited horizontal | Horizontal (by design) |
| **Query language** | SQL (expressive joins) | Limited (key-value, document) |
| **Use cases** | Financial, ERP, complex queries | Social, IoT, time-series, caching |
| **Examples** | PostgreSQL, MySQL, Oracle | MongoDB, Cassandra, DynamoDB, Redis |

**Rule of thumb**: Default to PostgreSQL. Switch to NoSQL when:
- Data model is document/graph/time-series
- Write throughput > millions/sec
- Need geographic distribution
- Schema is truly dynamic

---

## Database Replication

### Master-Slave (Primary-Replica)
- All writes go to primary
- Reads distributed to replicas (read scaling)
- Failover: promote a replica to primary (may have lag)
- Replication lag: replicas are eventually consistent

### Multi-Master
- Multiple nodes accept writes
- Conflict resolution needed (last-write-wins, merge, application-level)
- Higher write availability

### Synchronous vs Asynchronous Replication
- **Synchronous**: primary waits for replica confirmation → no data loss, higher latency
- **Asynchronous**: primary doesn't wait → lower latency, possible data loss on primary failure

---

## Indexing

### B-Tree Indexes (Default)
- Good for equality and range queries
- O(log n) lookup, insert, delete
- Overhead on write-heavy workloads

### Hash Indexes
- O(1) equality lookup
- No range queries
- Used internally in hash joins

### Composite Indexes
- Multiple columns: `CREATE INDEX idx ON orders(user_id, created_at)`
- Left-prefix rule: `WHERE user_id = ?` ✅, `WHERE created_at = ?` ❌ (doesn't use index)

### Covering Index
An index that contains all columns needed by a query — avoids returning to the table.

### When to Add/Avoid Indexes
- Add: high-selectivity columns used in WHERE, JOIN, ORDER BY
- Avoid: low-cardinality (boolean, status enum), write-heavy tables, rarely queried columns

---

## Redis Patterns

### Data Structures and Use Cases
| Structure | Use Case |
|-----------|---------|
| String | Session storage, simple counters, feature flags |
| Hash | User profile objects, configuration |
| List | Message queues, activity feeds (FIFO) |
| Set | Unique visitors, tags, social graph edges |
| Sorted Set | Leaderboards, rate limiting, time-series |
| Stream | Event sourcing, log aggregation |

### Common Patterns

**Distributed Lock**:
```
SET lock_key unique_value NX PX 30000  # NX=only if not exists, PX=expiry in ms
# ... critical section ...
DEL lock_key  # release (use Lua to make it atomic)
```

**Cache-aside with TTL**:
```java
String cached = redis.get(key);
if (cached == null) {
    String value = db.query(key);
    redis.setex(key, 300, value); // cache for 5 minutes
    return value;
}
return cached;
```

**Leaderboard with Sorted Set**:
```
ZADD leaderboard 1500 user:123  # score=1500
ZREVRANK leaderboard user:123   # rank from top
ZREVRANGEBYSCORE leaderboard +inf -inf WITHSCORES LIMIT 0 10  # top 10
```

---

## Database Connection Pooling

### Why Connection Pooling?
Creating a DB connection is expensive (~100ms for TCP + TLS + authentication). Pooling reuses connections.

### HikariCP Configuration (Spring Boot)
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20         # Start with: (CPU cores * 2) + spindle_count
      minimum-idle: 5
      connection-timeout: 20000     # 20 seconds to get a connection from pool
      idle-timeout: 600000          # idle connections closed after 10 min
      max-lifetime: 1800000         # connections recycled after 30 min
      leak-detection-threshold: 60000 # warn if connection held for 60s
```

### Pool Size Heuristic
`pool_size = (cpu_cores × 2) + effective_spindle_count`
For an app on a 4-core machine with SSD: max ~9–10 connections per instance.
This is counterintuitive — most apps need fewer connections than developers think.
