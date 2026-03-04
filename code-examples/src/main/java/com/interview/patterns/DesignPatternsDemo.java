package com.interview.patterns;

import java.util.*;

/**
 * Demonstrates key design patterns frequently asked in senior Java interviews.
 *
 * Patterns covered:
 * 1. Singleton (Initialization-on-Demand Holder)
 * 2. Builder
 * 3. Decorator
 * 4. Strategy
 * 5. Observer
 * 6. Factory Method
 */
public class DesignPatternsDemo {

    // ────────────────────────────────────────────────────────────
    // 1. Singleton — Initialization-on-Demand Holder
    // Thread-safe, lazy initialization, no synchronization overhead
    // ────────────────────────────────────────────────────────────

    static final class AppConfig {
        private final Map<String, String> properties;

        private AppConfig() {
            properties = new HashMap<>();
            properties.put("db.url", "jdbc:postgresql://localhost:5432/mydb");
            properties.put("cache.ttl", "300");
        }

        private static final class Holder {
            private static final AppConfig INSTANCE = new AppConfig();
        }

        public static AppConfig getInstance() { return Holder.INSTANCE; }
        public String get(String key) { return properties.get(key); }
    }

    // ────────────────────────────────────────────────────────────
    // 2. Builder — Immutable HTTP Request
    // ────────────────────────────────────────────────────────────

    static final class HttpRequest {
        private final String url;
        private final String method;
        private final String body;
        private final int timeoutMs;
        private final Map<String, String> headers;

        private HttpRequest(Builder b) {
            this.url = b.url;
            this.method = b.method;
            this.body = b.body;
            this.timeoutMs = b.timeoutMs;
            this.headers = Collections.unmodifiableMap(new HashMap<>(b.headers));
        }

        @Override
        public String toString() {
            return String.format("%s %s (timeout=%dms, headers=%s)", method, url, timeoutMs, headers);
        }

        static Builder builder(String url) { return new Builder(url); }

        static class Builder {
            private final String url;
            private String method = "GET";
            private String body;
            private int timeoutMs = 5000;
            private final Map<String, String> headers = new LinkedHashMap<>();

            private Builder(String url) {
                this.url = Objects.requireNonNull(url, "url required");
            }
            public Builder method(String m) { this.method = m; return this; }
            public Builder body(String b)   { this.body = b; return this; }
            public Builder timeout(int ms)  { this.timeoutMs = ms; return this; }
            public Builder header(String k, String v) { headers.put(k, v); return this; }
            public HttpRequest build() {
                if ("POST".equals(method) && (body == null || body.isEmpty()))
                    throw new IllegalStateException("POST requires a body");
                return new HttpRequest(this);
            }
        }
    }

    // ────────────────────────────────────────────────────────────
    // 3. Decorator — Text Processor
    // ────────────────────────────────────────────────────────────

    interface TextProcessor {
        String process(String text);
    }

    static class PlainTextProcessor implements TextProcessor {
        @Override public String process(String text) { return text; }
    }

    static class TrimDecorator implements TextProcessor {
        private final TextProcessor wrapped;
        TrimDecorator(TextProcessor w) { this.wrapped = w; }
        @Override public String process(String text) { return wrapped.process(text).trim(); }
    }

    static class UpperCaseDecorator implements TextProcessor {
        private final TextProcessor wrapped;
        UpperCaseDecorator(TextProcessor w) { this.wrapped = w; }
        @Override public String process(String text) { return wrapped.process(text).toUpperCase(); }
    }

    // ────────────────────────────────────────────────────────────
    // 4. Strategy — Sorting Strategy
    // ────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface SortStrategy { void sort(int[] arr); }

    static class Sorter {
        private SortStrategy strategy;
        Sorter(SortStrategy s) { this.strategy = s; }
        void setStrategy(SortStrategy s) { this.strategy = s; }
        void sort(int[] arr) { strategy.sort(arr); }
    }

    // ────────────────────────────────────────────────────────────
    // 5. Observer — Event Bus
    // ────────────────────────────────────────────────────────────

    static class EventBus<T> {
        private final List<java.util.function.Consumer<T>> listeners = new ArrayList<>();
        void subscribe(java.util.function.Consumer<T> l) { listeners.add(l); }
        void publish(T event) { listeners.forEach(l -> l.accept(event)); }
    }

    record OrderEvent(String orderId, double amount) {}

    // ────────────────────────────────────────────────────────────
    // 6. Factory Method — Notification channels
    // ────────────────────────────────────────────────────────────

    interface NotificationChannel {
        void send(String recipient, String message);
    }

    enum ChannelType { EMAIL, SMS, PUSH }

    static NotificationChannel createChannel(ChannelType type) {
        return switch (type) {
            case EMAIL -> (r, m) -> System.out.printf("[EMAIL] %s: %s%n", r, m);
            case SMS   -> (r, m) -> System.out.printf("[SMS]   %s: %s%n", r, m);
            case PUSH  -> (r, m) -> System.out.printf("[PUSH]  %s: %s%n", r, m);
        };
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Singleton ===");
        AppConfig config = AppConfig.getInstance();
        System.out.println("db.url: " + config.get("db.url"));
        System.out.println("Same instance? " + (AppConfig.getInstance() == config)); // true

        System.out.println("\n=== Builder ===");
        HttpRequest req = HttpRequest.builder("https://api.example.com/users")
            .method("POST")
            .header("Content-Type", "application/json")
            .body("{\"name\":\"Alice\"}")
            .timeout(3000)
            .build();
        System.out.println(req);

        System.out.println("\n=== Decorator ===");
        TextProcessor processor = new UpperCaseDecorator(
            new TrimDecorator(new PlainTextProcessor())
        );
        System.out.println(processor.process("  hello world  ")); // HELLO WORLD

        System.out.println("\n=== Strategy ===");
        Sorter sorter = new Sorter(Arrays::sort);
        int[] arr = {5, 2, 8, 1, 9};
        sorter.sort(arr);
        System.out.println("Sorted: " + Arrays.toString(arr));

        System.out.println("\n=== Observer ===");
        EventBus<OrderEvent> bus = new EventBus<>();
        bus.subscribe(e -> System.out.println("Email sent for: " + e.orderId()));
        bus.subscribe(e -> System.out.println("Analytics: " + e.amount() + " EUR"));
        bus.publish(new OrderEvent("ORD-001", 49.99));

        System.out.println("\n=== Factory Method ===");
        NotificationChannel email = createChannel(ChannelType.EMAIL);
        NotificationChannel sms   = createChannel(ChannelType.SMS);
        email.send("alice@example.com", "Your order shipped!");
        sms.send("+49123456789", "Your order shipped!");
    }
}
