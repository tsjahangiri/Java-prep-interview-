# Design Patterns

## Creational Patterns

### Singleton — Thread-Safe (Initialization-on-Demand Holder)
```java
package com.interview.patterns;

public class DatabaseConnection {

    // Private constructor — no direct instantiation
    private DatabaseConnection() {
        // initialize connection pool
    }

    // Holder class is loaded lazily; class initialization is thread-safe by JVM spec
    private static class Holder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return Holder.INSTANCE;
    }

    public void query(String sql) {
        System.out.println("Executing: " + sql);
    }
}
```
**Why not `double-checked locking` with `synchronized`?** The Holder idiom is simpler and equally thread-safe.

---

### Builder Pattern
```java
package com.interview.patterns;

public class HttpRequest {

    private final String url;
    private final String method;
    private final String body;
    private final int timeoutMs;
    private final java.util.Map<String, String> headers;

    private HttpRequest(Builder builder) {
        this.url       = builder.url;
        this.method    = builder.method;
        this.body      = builder.body;
        this.timeoutMs = builder.timeoutMs;
        this.headers   = java.util.Collections.unmodifiableMap(builder.headers);
    }

    public static Builder builder(String url) {
        return new Builder(url);
    }

    public static class Builder {
        private final String url;
        private String method = "GET";
        private String body;
        private int timeoutMs = 5000;
        private java.util.Map<String, String> headers = new java.util.HashMap<>();

        private Builder(String url) {
            this.url = java.util.Objects.requireNonNull(url, "url must not be null");
        }

        public Builder method(String method) { this.method = method; return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder timeout(int ms) { this.timeoutMs = ms; return this; }
        public Builder header(String key, String value) {
            this.headers.put(key, value); return this;
        }

        public HttpRequest build() {
            if ("POST".equals(method) && (body == null || body.isEmpty())) {
                throw new IllegalStateException("POST request requires a body");
            }
            return new HttpRequest(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s (timeout=%dms, headers=%s, body=%s)",
            method, url, timeoutMs, headers, body);
    }

    public static void main(String[] args) {
        HttpRequest request = HttpRequest.builder("https://api.example.com/users")
            .method("POST")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer token123")
            .body("{\"name\":\"Alice\"}")
            .timeout(3000)
            .build();
        System.out.println(request);
    }
}
```

---

## Structural Patterns

### Decorator Pattern
```java
package com.interview.patterns;

// Component interface
interface TextProcessor {
    String process(String text);
}

// Concrete component
class PlainTextProcessor implements TextProcessor {
    @Override
    public String process(String text) { return text; }
}

// Base decorator
abstract class TextProcessorDecorator implements TextProcessor {
    protected final TextProcessor wrapped;
    TextProcessorDecorator(TextProcessor wrapped) { this.wrapped = wrapped; }
}

// Concrete decorators
class TrimDecorator extends TextProcessorDecorator {
    TrimDecorator(TextProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String text) { return wrapped.process(text).trim(); }
}

class UpperCaseDecorator extends TextProcessorDecorator {
    UpperCaseDecorator(TextProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String text) { return wrapped.process(text).toUpperCase(); }
}

class LoggingDecorator extends TextProcessorDecorator {
    LoggingDecorator(TextProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String text) {
        String result = wrapped.process(text);
        System.out.println("[LOG] Processed: " + result);
        return result;
    }
}

// Usage: chain decorators at runtime
class DecoratorDemo {
    public static void main(String[] args) {
        TextProcessor processor = new LoggingDecorator(
            new UpperCaseDecorator(
                new TrimDecorator(
                    new PlainTextProcessor()
                )
            )
        );
        processor.process("  hello world  "); // [LOG] Processed: HELLO WORLD
    }
}
```

---

## Behavioral Patterns

### Strategy Pattern
```java
package com.interview.patterns;

import java.util.*;

// Strategy interface — Java 8+: can be a functional interface
@FunctionalInterface
interface SortStrategy {
    void sort(int[] arr);
}

// Context
class Sorter {
    private SortStrategy strategy;

    public Sorter(SortStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(SortStrategy strategy) { this.strategy = strategy; }

    public void sort(int[] arr) { strategy.sort(arr); }
}

class StrategyDemo {
    public static void main(String[] args) {
        int[] arr1 = {5, 2, 8, 1};
        int[] arr2 = arr1.clone();

        // Swap strategies at runtime
        Sorter sorter = new Sorter(Arrays::sort); // quick sort via lambda
        sorter.sort(arr1);
        System.out.println(Arrays.toString(arr1)); // [1, 2, 5, 8]

        sorter.setStrategy(arr -> {
            // Bubble sort strategy (for demo)
            for (int i = 0; i < arr.length - 1; i++)
                for (int j = 0; j < arr.length - 1 - i; j++)
                    if (arr[j] > arr[j+1]) { int t = arr[j]; arr[j] = arr[j+1]; arr[j+1] = t; }
        });
        sorter.sort(arr2);
        System.out.println(Arrays.toString(arr2)); // [1, 2, 5, 8]
    }
}
```

---

### Observer Pattern
```java
package com.interview.patterns;

import java.util.*;

interface EventListener<T> {
    void onEvent(T event);
}

class EventBus<T> {
    private final List<EventListener<T>> listeners = new ArrayList<>();

    public void subscribe(EventListener<T> listener) { listeners.add(listener); }

    public void publish(T event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}

record OrderPlacedEvent(String orderId, double amount) {}

class OrderEventDemo {
    public static void main(String[] args) {
        EventBus<OrderPlacedEvent> bus = new EventBus<>();

        bus.subscribe(event -> System.out.println("Email sent for order: " + event.orderId()));
        bus.subscribe(event -> System.out.println("Inventory updated for: " + event.orderId()));
        bus.subscribe(event -> System.out.printf("Analytics: %.2f EUR sale%n", event.amount()));

        bus.publish(new OrderPlacedEvent("ORD-001", 49.99));
    }
}
```

---

## Interview Questions

**Q: What is the difference between Strategy and Template Method patterns?**
- **Strategy**: behaviour is defined in a separate class (composition). The algorithm can be swapped at runtime.
- **Template Method**: the algorithm skeleton is defined in a base class; subclasses fill in specific steps (inheritance). Steps cannot be swapped at runtime.

**Q: When would you choose Decorator over Inheritance?**
Decorator allows combining behaviours dynamically without a class explosion. With inheritance, 3 behaviours = up to 8 subclasses for all combinations. With Decorator, you wrap as needed.

**Q: What is the difference between Factory Method and Abstract Factory?**
- **Factory Method**: one method creates one type of product; subclasses decide which concrete product.
- **Abstract Factory**: a family of related products; ensures they are compatible (e.g., `MacOSFactory` creates `MacButton` + `MacScrollbar`).
