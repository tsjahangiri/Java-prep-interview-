# Performance Tuning

## Profiling Workflow

```
1. Measure, don't guess → use profilers (JFR, async-profiler, VisualVM)
2. Find the bottleneck → CPU, memory, I/O, or lock contention?
3. Fix ONE thing at a time
4. Measure again to confirm improvement
5. Document findings
```

---

## Java Flight Recorder (JFR)

Built into the JVM (free since Java 11). Near-zero overhead profiling.

```bash
# Start recording
java -XX:StartFlightRecording=duration=60s,filename=recording.jfr MyApp

# Or attach to running process
jcmd <pid> JFR.start duration=60s filename=recording.jfr

# Analyze with JDK Mission Control (JMC) or jfr tool
jfr print --events jdk.CPULoad recording.jfr
```

---

## Common Performance Anti-Patterns and Fixes

### Anti-Pattern 1: String Concatenation in a Loop
```java
// ❌ O(n²) — creates a new String object each iteration
String result = "";
for (int i = 0; i < 10_000; i++) {
    result += i + ",";
}

// ✅ O(n) — StringBuilder pre-allocates buffer
StringBuilder sb = new StringBuilder(10_000 * 4);
for (int i = 0; i < 10_000; i++) {
    sb.append(i).append(',');
}
String result = sb.toString();
```

### Anti-Pattern 2: Unnecessary Object Creation
```java
// ❌ Autoboxing in a tight loop — millions of Integer objects
long sum = 0;
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 1_000_000; i++) list.add(i);
for (Integer n : list) sum += n; // unboxing each iteration

// ✅ Use primitive collections (Eclipse Collections, IntStream)
long sum = IntStream.range(0, 1_000_000).asLongStream().sum();
```

### Anti-Pattern 3: N+1 Query Problem (JPA/Hibernate)
```java
// ❌ N+1: one query for all orders, then N queries for each user
List<Order> orders = orderRepo.findAll();
for (Order order : orders) {
    System.out.println(order.getUser().getName()); // lazy load fires per order
}

// ✅ Fetch join: single query with JOIN
@Query("SELECT o FROM Order o JOIN FETCH o.user")
List<Order> findAllWithUsers();
```

### Anti-Pattern 4: Locking Too Much
```java
// ❌ Synchronizing entire method when only map access needs protection
public synchronized String processAndCache(String key) {
    String cached = cache.get(key);
    if (cached != null) return cached;
    String value = expensiveCompute(key); // no need to hold lock here!
    cache.put(key, value);
    return value;
}

// ✅ ConcurrentHashMap's computeIfAbsent — atomic, finer-grained
private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

public String processAndCache(String key) {
    return cache.computeIfAbsent(key, this::expensiveCompute);
}
```

---

## GC Tuning Checklist

1. **Reduce allocation rate**: fewer short-lived objects → less GC pressure
2. **Right-size heap**: `-Xmx` too small → frequent GC; too large → long GC pauses
3. **Choose the right GC**: latency-sensitive → ZGC/Shenandoah; throughput → Parallel GC
4. **Avoid object retention**: nullify references, avoid static collections holding objects
5. **Use off-heap storage**: for large caches, consider off-heap (e.g., Chronicle Map, Caffeine)

---

## Interview Questions

**Q: What tools do you use to diagnose a memory leak in production?**
1. Monitor heap usage over time (`jstat -gcutil <pid> 1000`).
2. Take heap dumps during leak: `jmap -dump:live,format=b,file=heap.hprof <pid>`.
3. Analyze with Eclipse MAT: look for retained heap, dominator tree, leak suspects.
4. Common causes: static collections, unclosed listeners, ThreadLocal with no remove, improper cache eviction.

**Q: How do you identify CPU hotspots?**
1. `async-profiler` or JFR with CPU profiling mode.
2. Look for: tight loops, excessive synchronization (lock contention), GC CPU time, I/O blocking.
3. Use flame graphs to visualize call stacks.

**Q: What is false sharing, and how do you fix it?**
When two variables on the same CPU cache line are written by different threads, each write invalidates the entire line for other CPUs — causing cache thrashing.
Fix: pad the variable to occupy its own cache line (64 bytes on x86).
```java
// Java 8+: @Contended annotation (requires -XX:-RestrictContended)
@jdk.internal.vm.annotation.Contended
private volatile long counter;
```

**Q: When would you use object pooling?**
When objects are expensive to create/destroy (e.g., DB connections, byte buffers, HTTP clients) and the usage pattern is borrow-then-return. Examples: `HikariCP` (connection pool), `ByteBufAllocator` (Netty). Avoid pooling for cheap objects — the pool overhead may exceed the creation cost.
