package com.interview.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * Demonstrates core concurrency patterns for interview practice.
 *
 * Topics:
 * - AtomicInteger for lock-free counting
 * - ReentrantReadWriteLock for read-heavy caches
 * - BlockingQueue for producer-consumer
 * - CompletableFuture for async pipelines
 * - ThreadPoolExecutor configuration
 */
public class ConcurrencyDemo {

    // ────────────────────────────────────────────────────────────
    // 1. Thread-Safe Counter with AtomicInteger
    // ────────────────────────────────────────────────────────────

    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public int increment() { return count.incrementAndGet(); }
        public int decrement() { return count.decrementAndGet(); }
        public int get()       { return count.get(); }

        public static void demo() throws InterruptedException {
            AtomicCounter counter = new AtomicCounter();
            int threads = 50, incrementsPerThread = 100;

            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(threads);

            for (int i = 0; i < threads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < incrementsPerThread; j++) counter.increment();
                    latch.countDown();
                });
            }
            latch.await();
            executor.shutdown();

            int expected = threads * incrementsPerThread;
            System.out.printf("AtomicCounter: expected=%d, actual=%d, correct=%b%n",
                expected, counter.get(), counter.get() == expected);
        }
    }

    // ────────────────────────────────────────────────────────────
    // 2. Read-Write Cache
    // ────────────────────────────────────────────────────────────

    static class ReadWriteCache<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public V get(K key) {
            lock.readLock().lock();
            try { return cache.get(key); }
            finally { lock.readLock().unlock(); }
        }

        public void put(K key, V value) {
            lock.writeLock().lock();
            try { cache.put(key, value); }
            finally { lock.writeLock().unlock(); }
        }

        public V computeIfAbsent(K key, java.util.function.Function<K, V> fn) {
            lock.readLock().lock();
            try {
                V v = cache.get(key);
                if (v != null) return v;
            } finally { lock.readLock().unlock(); }

            lock.writeLock().lock();
            try {
                return cache.computeIfAbsent(key, fn); // double-check after write lock
            } finally { lock.writeLock().unlock(); }
        }
    }

    // ────────────────────────────────────────────────────────────
    // 3. Producer-Consumer with BlockingQueue
    // ────────────────────────────────────────────────────────────

    static void producerConsumerDemo() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        int numItems = 10;
        int poisonPill = -1;

        Runnable producer = () -> {
            try {
                for (int i = 0; i < numItems; i++) {
                    queue.put(i);
                    System.out.printf("[Producer] put %d%n", i);
                }
                queue.put(poisonPill);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    int item = queue.take();
                    if (item == poisonPill) { queue.put(poisonPill); break; }
                    System.out.printf("[Consumer-%s] took %d%n",
                        Thread.currentThread().getName(), item);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        };

        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(producer);
        exec.submit(consumer);
        exec.submit(consumer);
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
    }

    // ────────────────────────────────────────────────────────────
    // 4. CompletableFuture Pipeline
    // ────────────────────────────────────────────────────────────

    static void completableFutureDemo() {
        CompletableFuture<String> pipeline = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("[Async] Fetching user...");
                return "user:42"; // simulated async fetch
            })
            .thenApply(userId -> {
                System.out.println("[Async] Enriching: " + userId);
                return userId.toUpperCase(); // transform
            })
            .exceptionally(ex -> {
                System.out.println("[Async] Error: " + ex.getMessage());
                return "UNKNOWN";
            });

        String result = pipeline.join(); // block until done
        System.out.println("[Main] Result: " + result);
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== AtomicCounter Demo ===");
        AtomicCounter.demo();

        System.out.println("\n=== ReadWriteCache Demo ===");
        ReadWriteCache<String, String> cache = new ReadWriteCache<>();
        cache.put("key1", "value1");
        System.out.println("get(key1): " + cache.get("key1"));
        System.out.println("computeIfAbsent(key2): " + cache.computeIfAbsent("key2", k -> "computed-" + k));
        System.out.println("get(key2) again: " + cache.get("key2"));

        System.out.println("\n=== Producer-Consumer Demo ===");
        producerConsumerDemo();

        System.out.println("\n=== CompletableFuture Demo ===");
        completableFutureDemo();
    }
}
