# Common System Design Problems

---

## Design a URL Shortener (e.g., bit.ly)

### Requirements (Clarify First!)
**Functional**: given a long URL, return a short URL; redirect short URL to long URL.
**Non-functional**: 100M URLs, 10B redirects/month, < 10ms redirect latency, highly available.

### Back-of-Envelope
- Write QPS: 100M / (30 days × 86400s) ≈ **40 writes/sec**
- Read QPS: 10B / (30 × 86400) ≈ **4000 reads/sec** (read-heavy 100:1 ratio)
- Storage: 100M × 500 bytes ≈ **50 GB** (fits in memory for hot data)

### Core Design
```
Client → CDN → Load Balancer → Short URL Service → Cache (Redis) → DB (PostgreSQL)
                                    ↓
                              ID Generator Service
```

**Short URL generation**:
1. Generate unique ID (e.g., using a distributed ID generator like Snowflake).
2. Encode in Base62 (`a-z, A-Z, 0-9`) → 7 characters covers 62⁷ ≈ 3.5 trillion URLs.
3. Store `{short_code → long_url}` in DB.

**Redirect**: HTTP 301 (permanent, client caches) vs HTTP 302 (temporary, every redirect goes through server — use 302 for analytics).

**Caching**: 80/20 rule — 20% of URLs get 80% of traffic. Cache hot URLs in Redis with LRU eviction.

---

## Design a Rate Limiter

### Requirements
- Limit API calls per user: 100 requests/minute
- Distributed (multiple app servers)
- Minimal added latency

### Design
```
Client → API Gateway → [Rate Limiter (Redis)] → Upstream Service
```

**Algorithm**: Sliding window counter with Redis:
```
Key: rate_limit:{userId}:{minute_bucket}
Value: request count
TTL: 2 minutes

On each request:
  1. INCR key → count
  2. If count == 1: EXPIRE key 120
  3. If count > limit: return 429 Too Many Requests
```

**Headers to return**:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 40
X-RateLimit-Reset: 1620000060
```

---

## Design a Notification System

### Requirements
- Send email, SMS, push notifications
- 10M users, 1M notifications/day
- At-least-once delivery
- Priority support (high-priority = immediate, low-priority = batched)

### Design
```
Service → Notification API → Kafka (topic: notifications)
                                  ↓
              ┌───────────────────┼───────────────────┐
              ↓                   ↓                   ↓
         Email Worker        SMS Worker          Push Worker
        (SendGrid)           (Twilio)           (FCM/APNS)
              ↓
         Retry Queue (with exponential backoff)
              ↓
         DLQ (failed after 3 retries → alert + manual review)
```

**Priority routing**: use two Kafka topics or priorities — high-priority messages processed by dedicated consumers.

**Idempotency**: deduplicate with `notification_id` — store in Redis with TTL to avoid sending twice on retry.

---

## Design a Leaderboard System

### Requirements
- Real-time global leaderboard for a game
- 10M players, update scores frequently
- Read top-100 and user's rank instantly

### Design
**Redis Sorted Set** — perfect fit:
```
ZADD game:leaderboard {score} {userId}     # O(log N) insert/update
ZREVRANK game:leaderboard {userId}          # O(log N) user's rank
ZREVRANGEBYSCORE game:leaderboard +inf -inf # O(log N + k) top-k
ZSCORE game:leaderboard {userId}            # O(1) user's score
```

**Persistence**: write-through to PostgreSQL for durability; Redis as read cache.

**Sharding at scale**: shard by user_id range; merge-sort the top-K from each shard for global leaderboard.

---

## Design a Distributed Cache

### Requirements
- 10M keys, 1KB average value → 10 GB total
- < 1ms p99 read latency
- Auto-scaling, fault-tolerant

### Design
- Use **consistent hashing** to distribute keys across cache nodes
- Each node has a **replica** for fault tolerance
- **Hot key problem**: popular keys (e.g., homepage data) → replicate across multiple nodes; add jitter to TTLs
- **Cache stampede**: many requests miss simultaneously → mutex/coalescing, probabilistic early expiration, or background refresh

### Cache Stampede Prevention (pseudocode)
```
// Probabilistic early expiration: occasionally refresh before actual expiry
beta = 1.0
delta = time to recompute the cached value
rand = -1.0 * log(random())
if (currentTime - ttl + beta * delta * rand >= expiryTime):
    // recompute now, before actual expiry, to avoid a stampede at expiry
```
