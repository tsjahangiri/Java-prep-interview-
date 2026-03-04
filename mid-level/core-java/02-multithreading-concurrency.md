# Multithreading & Concurrency

## Key Concepts to Know
- `Thread`, `Runnable`, `Callable`, `Future`
- `synchronized` keyword — method-level vs block-level locking
- `volatile` — visibility guarantee, not atomicity
- `java.util.concurrent`: `ExecutorService`, `ThreadPoolExecutor`, `CountDownLatch`, `Semaphore`
- Common problems: race conditions, deadlocks, starvation, livelock

---

## Problem 1: Producer-Consumer with BlockingQueue

### Problem Statement
Implement a thread-safe producer-consumer pattern. Producers generate integers and place them in a shared buffer; consumers read and process them. Use Java's `BlockingQueue` to manage the buffer.

### Step-by-Step Explanation
1. `BlockingQueue` handles the synchronization internally — `put()` blocks when full, `take()` blocks when empty.
2. Use `ArrayBlockingQueue` with a fixed capacity as the shared buffer.
3. Producers run in separate threads, consumers in other threads.
4. Use a poison-pill sentinel (a special value) to signal consumers to stop.

### Java 17+ Solution
```java
package com.interview.concurrency;

import java.util.concurrent.*;

public class ProducerConsumerDemo {

    private static final int BUFFER_CAPACITY = 10;
    private static final int POISON_PILL = -1;
    private static final int NUM_ITEMS = 20;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Producer: generates numbers 0..NUM_ITEMS-1, then sends poison pill
        Runnable producer = () -> {
            try {
                for (int i = 0; i < NUM_ITEMS; i++) {
                    buffer.put(i);
                    System.out.printf("[Producer] Produced: %d%n", i);
                    Thread.sleep(50); // simulate work
                }
                buffer.put(POISON_PILL); // signal consumer to stop
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Consumer: takes items until it receives the poison pill
        Runnable consumer = () -> {
            try {
                while (true) {
                    int item = buffer.take();
                    if (item == POISON_PILL) {
                        buffer.put(POISON_PILL); // re-enqueue for other consumers
                        break;
                    }
                    System.out.printf("[Consumer-%s] Consumed: %d%n",
                            Thread.currentThread().getName(), item);
                    Thread.sleep(100); // simulate processing
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(producer);
        executor.submit(consumer);
        executor.submit(consumer); // two consumers, one producer

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("All done.");
    }
}
```

### Complexity Analysis
| | Complexity |
|---|---|
| **Time** | O(1) per `put`/`take` (amortized, assuming non-contended) |
| **Space** | O(capacity) — the buffer size |

### Common Pitfalls
- ❌ Using `wait()`/`notify()` manually when `BlockingQueue` covers the same use case more safely.
- ❌ Forgetting the poison-pill pattern — consumers will block forever if the producer finishes without signaling.
- ❌ Not re-enqueuing the poison pill for multiple consumers.

---

## Problem 2: Thread-Safe Counter with AtomicInteger

### Problem Statement
Implement a counter that is safely incremented by multiple threads without using `synchronized`.

### Java 17+ Solution
```java
package com.interview.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter {

    private final AtomicInteger count = new AtomicInteger(0);

    public int increment() {
        return count.incrementAndGet(); // atomic compare-and-swap, no locks
    }

    public int get() {
        return count.get();
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();
        int numThreads = 100;
        int incrementsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
                    }
                } finally {
                    latch.countDown(); // signal that this thread is done
                }
            });
        }

        latch.await(); // wait for all threads to complete
        executor.shutdown();

        // Expected: 100 * 1000 = 100,000
        System.out.printf("Final count: %d (expected: %d)%n",
                counter.get(), numThreads * incrementsPerThread);
    }
}
```

### Complexity Analysis
| | Complexity |
|---|---|
| **Time** | O(1) per increment (CAS operation) |
| **Space** | O(1) |

---

## Core Java Theory Questions

**Q: What is the difference between `synchronized` and `volatile`?**
- `synchronized` provides both **mutual exclusion** (only one thread at a time) and **visibility** (changes flushed to main memory).
- `volatile` provides only **visibility** — reads always go to main memory, writes are immediately visible. It does NOT ensure atomicity for compound operations like `i++`.

**Q: What is a race condition? Give an example.**
A race condition occurs when the correctness of a computation depends on the relative timing of threads.
Example: Two threads both read `counter = 5`, both compute `counter + 1 = 6`, and both write back `6` — the net result is `6` instead of `7`.

**Q: What is a deadlock? How do you prevent it?**
Deadlock: Thread A holds lock X and waits for lock Y; Thread B holds lock Y and waits for lock X.
Prevention strategies:
1. **Lock ordering** — always acquire locks in the same order.
2. **Timeouts** — use `tryLock(timeout)` from `ReentrantLock`.
3. **Lock-free data structures** — e.g., `AtomicInteger`, `ConcurrentHashMap`.

**Q: Explain `CountDownLatch` vs `CyclicBarrier`.**
- `CountDownLatch`: one-shot. A set of threads count down; one or more threads wait for it to reach zero. Cannot be reset.
- `CyclicBarrier`: reusable. All threads wait at the barrier until all have arrived, then all proceed together. Useful for iterative algorithms.

**Q: What is the Java Memory Model (JMM)?**
The JMM defines how threads interact through memory. Key rules:
- Actions in a thread are ordered (program order).
- A `synchronized` block establishes a happens-before relationship.
- A `volatile` write happens-before any subsequent read of that variable.
- Without happens-before guarantees, the JVM may reorder instructions, causing unexpected behavior.
