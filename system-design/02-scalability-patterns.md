# Scalability Patterns

---

## Load Balancing

### Algorithms
| Algorithm | Best For | Limitation |
|-----------|---------|------------|
| Round Robin | Homogeneous servers | Ignores server load |
| Weighted Round Robin | Heterogeneous servers | Static weights |
| Least Connections | Long-lived connections | Doesn't account for request cost |
| IP Hash | Session affinity | Uneven distribution |
| Random | Simple, works well at scale | No awareness of load |

### Layer 4 vs Layer 7
- **L4 (Transport)**: routes by IP/TCP. Fast, but can't inspect HTTP content.
- **L7 (Application)**: routes by HTTP headers, URL, cookies. Supports A/B testing, path-based routing, SSL termination.

---

## Caching

### Cache Placement Strategies
```
Client → [CDN] → [API Gateway] → [App Cache] → [DB Cache] → [DB]
                                    ↓
                             [Distributed Cache: Redis]
```

### Cache Write Strategies

**Write-Through**: write to cache AND DB synchronously.
- Pro: cache always has fresh data
- Con: write latency increases

**Write-Behind (Write-Back)**: write to cache; async write to DB.
- Pro: low write latency
- Con: data loss if cache fails before DB write

**Write-Around**: write to DB only; cache loaded on read.
- Pro: avoids caching rarely-read data
- Con: cache miss on first read

### Cache Invalidation Strategies
1. **TTL-based expiry**: simple, may serve stale data briefly
2. **Cache-aside (Lazy Loading)**: check cache → if miss, load from DB → write to cache
3. **Read-through**: cache itself loads from DB on miss
4. **Event-driven invalidation**: DB event/trigger publishes to cache to invalidate

### Cache Eviction Policies
- **LRU** (Least Recently Used): evict the least recently accessed item — most common
- **LFU** (Least Frequently Used): evict the least frequently accessed item
- **FIFO**: evict the oldest item
- **Random**: evict a random item

---

## Database Sharding

### What is Sharding?
Horizontal partitioning: split data across multiple DB instances (shards) to distribute load.

### Sharding Strategies

**Range-based**: shard by key range (e.g., users A-M → shard 1, N-Z → shard 2).
- Pro: simple, supports range queries
- Con: hot spots if data is not uniformly distributed

**Hash-based**: shard by `hash(key) % num_shards`.
- Pro: even distribution
- Con: range queries require all-shard fan-out; resharding is expensive

**Directory-based**: a lookup table maps keys to shards.
- Pro: flexible, supports resharding
- Con: lookup table is a bottleneck and single point of failure

### Consistent Hashing
Used in distributed caches (Redis Cluster, Cassandra). Placing servers and data on a virtual ring — when a server is added/removed, only `K/N` keys need to be remapped (K = keys, N = servers) vs. all keys in naive hashing.

---

## Content Delivery Network (CDN)

**Pull CDN**: origin server has the content; CDN fetches and caches on first request.
- Pro: no manual uploads
- Con: cold start latency

**Push CDN**: content pushed to CDN nodes proactively.
- Pro: zero cold start
- Con: requires content management; storage cost

**Use CDN for**: static assets (JS, CSS, images, videos), regional latency reduction, DDoS protection.

---

## Rate Limiting

### Algorithms

**Token Bucket**: tokens added at fixed rate; requests consume tokens. Allows bursts up to bucket size.

**Leaky Bucket**: requests enter a queue; processed at fixed rate. Smooths bursts but adds latency.

**Fixed Window Counter**: count requests per time window. Simple but edge case at window boundary.

**Sliding Window Log**: track exact timestamps; accurate but memory-intensive.

**Sliding Window Counter**: combination — weighted average of previous + current window. Good balance.

### Distributed Rate Limiter
Use Redis with atomic Lua scripts or `INCR` + `EXPIRE` for distributed rate limiting across app instances.

```
// Redis-based token bucket (pseudocode)
local current = redis.incr(key)
if current == 1 then redis.expire(key, window_seconds) end
if current > limit then reject() else allow() end
```
