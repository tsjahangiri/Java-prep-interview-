# Advanced Concurrency

## java.util.concurrent Deep Dive

### Lock Hierarchy
```
Lock (interface)
  └── ReentrantLock — reentrant exclusive lock, fairness option
  └── ReadWriteLock (interface)
        └── ReentrantReadWriteLock — multiple readers OR one writer
StampedLock — optimistic reading, Java 8+
```

---

## Problem 1: Read-Write Cache with ReentrantReadWriteLock

### Problem Statement
Implement a thread-safe cache where many threads can read concurrently but writes are exclusive.

### Java 17+ Solution
```java
package com.interview.concurrency;

import java.util.*;
import java.util.concurrent.locks.*;

public class ReadWriteCache<K, V> {

    private final Map<K, V> cache = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock  = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    /**
     * Multiple threads can read simultaneously — no blocking between readers.
     */
    public V get(K key) {
        readLock.lock();
        try {
            return cache.get(key);
        } finally {
            readLock.unlock(); // ALWAYS unlock in finally
        }
    }

    /**
     * Exclusive write: all readers and other writers are blocked.
     */
    public void put(K key, V value) {
        writeLock.lock();
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Lock downgrade: acquire write lock → read lock → release write lock.
     * Ensures atomicity: no other writer can modify between write and read.
     */
    public V computeIfAbsent(K key, java.util.function.Function<K, V> mappingFn) {
        readLock.lock();
        try {
            V value = cache.get(key);
            if (value != null) return value;
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            // Re-check after acquiring write lock (another thread may have written)
            V value = cache.get(key);
            if (value == null) {
                value = mappingFn.apply(key);
                cache.put(key, value);
            }
            return value;
        } finally {
            writeLock.unlock();
        }
    }
}
```

---

## Problem 2: Custom Thread Pool with Rejection Policy

### Java 17+ Solution
```java
package com.interview.concurrency;

import java.util.concurrent.*;

public class CustomThreadPoolDemo {

    public static void main(String[] args) {
        // ThreadPoolExecutor constructor parameters:
        // corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, rejectionPolicy
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                                   // core threads (always alive)
            4,                                   // max threads
            60, TimeUnit.SECONDS,                // idle threads kept alive
            new ArrayBlockingQueue<>(10),        // bounded work queue
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "worker-" + count.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // rejected tasks run in caller's thread
        );

        // Submit tasks
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.printf("[%s] Processing task %d%n",
                    Thread.currentThread().getName(), taskId);
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## Problem 3: Implementing a Rate Limiter with Semaphore

### Java 17+ Solution
```java
package com.interview.concurrency;

import java.util.concurrent.*;

/**
 * Simple token-bucket rate limiter: allows at most N concurrent requests.
 */
public class RateLimiter {

    private final Semaphore semaphore;
    private final int maxPermits;

    public RateLimiter(int maxConcurrentRequests) {
        this.maxPermits = maxConcurrentRequests;
        this.semaphore = new Semaphore(maxConcurrentRequests, true); // fair
    }

    public <T> T execute(Callable<T> task) throws Exception {
        semaphore.acquire(); // block if no permits available
        try {
            return task.call();
        } finally {
            semaphore.release(); // always release, even on exception
        }
    }

    public boolean tryExecute(Callable<Void> task, long timeout, TimeUnit unit)
            throws Exception {
        if (!semaphore.tryAcquire(timeout, unit)) {
            return false; // could not acquire within timeout
        }
        try {
            task.call();
            return true;
        } finally {
            semaphore.release();
        }
    }
}
```

---

## Advanced Theory Questions

**Q: What is `CompletableFuture`? How does it differ from `Future`?**
- `Future` (Java 5): get the result of async computation. Only blocking `.get()`, no composition.
- `CompletableFuture` (Java 8+): supports non-blocking callbacks (`thenApply`, `thenCompose`, `thenCombine`), exception handling (`exceptionally`, `handle`), and composing pipelines.

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchUser(id))            // runs in ForkJoinPool
    .thenApply(user -> user.getName())            // transform result
    .exceptionally(ex -> "Unknown");              // handle error

String name = future.join(); // non-blocking until needed
```

**Q: Explain the Fork/Join framework.**
Designed for divide-and-conquer parallel tasks. `ForkJoinPool` uses work-stealing — idle threads steal tasks from busy threads' queues, maximizing CPU utilization.

```java
class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;
    private final long[] arr;
    private final int start, end;

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += arr[i];
            return sum;
        }
        int mid = (start + end) / 2;
        SumTask left  = new SumTask(arr, start, mid);
        SumTask right = new SumTask(arr, mid, end);
        left.fork();           // async computation of left
        return right.compute() + left.join(); // compute right, then join left
    }
}
```

**Q: What is a `volatile` long or double in Java?**
On 32-bit JVMs, reads/writes to `long` and `double` are non-atomic (two 32-bit operations). Declaring them `volatile` ensures atomic 64-bit reads/writes. On 64-bit JVMs (nearly all modern environments), this is typically not an issue, but `volatile` is still needed for visibility guarantees.

**Q: What is the ABA problem in lock-free programming?**
In CAS (compare-and-swap): thread reads value A, another thread changes A → B → A. The first thread's CAS succeeds even though the state changed. Fix: use `AtomicStampedReference` which pairs a reference with a version stamp.
