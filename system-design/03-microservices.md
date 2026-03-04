# Microservices Architecture

---

## Service Decomposition Principles

### How to Split Services
1. **By business capability**: each service owns a bounded context (DDD)
2. **By data**: each service has its own database (database-per-service pattern)
3. **By team**: Conway's Law — system design mirrors communication structure

### When NOT to use Microservices
- Small team (< 5 engineers) — distributed overhead exceeds benefits
- Low traffic — monolith is simpler to operate
- Rapid prototyping — decomposition decisions are expensive to undo

---

## Communication Patterns

### Synchronous (Request-Response)
- **REST**: simple, stateless, HTTP/JSON. Best for CRUD operations.
- **gRPC**: Protocol Buffers, HTTP/2, streaming. Best for internal services, high throughput.
- **GraphQL**: flexible queries, avoids over/under-fetching. Best for client-facing APIs with diverse clients.

### Asynchronous (Event-Driven)
- **Message Queue** (RabbitMQ, SQS): point-to-point; one consumer per message.
- **Event Streaming** (Kafka): log-based; multiple consumers; replay events.
- **Outbox Pattern**: write events to DB in same transaction as state change; background process publishes to broker — ensures exactly-once.

---

## API Gateway

**Responsibilities**: routing, SSL termination, authentication, rate limiting, request transformation, circuit breaking.

```
Client
  ↓
API Gateway (authentication, rate limiting, routing)
  ↓              ↓              ↓
User Service   Order Service  Payment Service
```

**Products**: Kong, AWS API Gateway, Nginx, Traefik, Spring Cloud Gateway.

---

## Service Discovery

**Client-side discovery**: client queries a registry (Eureka) and selects a service instance itself. More control, but client must implement load balancing.

**Server-side discovery**: client calls a load balancer which queries the registry. Client is simpler; load balancer is a potential bottleneck.

---

## Resilience Patterns

### Circuit Breaker
Prevents cascading failures by short-circuiting calls to a failing service.

```
CLOSED (normal) → failures exceed threshold → OPEN (fail fast)
    ↑                                              ↓
  success                              half-open probe after timeout
```

Java implementation: **Resilience4j** (preferred over Hystrix which is no longer maintained).

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)         // open at 50% failure rate
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .slidingWindowSize(10)
    .build();
CircuitBreaker cb = CircuitBreaker.of("paymentService", config);

String result = cb.executeSupplier(() -> paymentService.charge(request));
```

### Retry with Exponential Backoff
```java
RetryConfig config = RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .retryExceptions(IOException.class)
    .ignoreExceptions(ValidationException.class)
    .build();
```

### Bulkhead
Isolate resources so that a failure in one component doesn't consume all resources.

---

## Distributed Data Management

### Saga Pattern (Distributed Transactions)
ACID transactions don't span services. Use Sagas:

**Choreography-based**: each service listens for events and emits its own.
- Pro: no central coordinator
- Con: hard to track state; complex failure handling

**Orchestration-based**: a central saga orchestrator sends commands.
- Pro: clear flow, easier to debug
- Con: orchestrator becomes a bottleneck/SPOF

### CQRS (Command Query Responsibility Segregation)
Separate read and write models. Writes go to a command model; reads from a query model (often a separate read-optimized DB).

**Use case**: complex queries that would be slow on the write model, or when read and write scaling requirements differ.

---

## Service Mesh

A dedicated infrastructure layer for service-to-service communication.

**Features**: mutual TLS (mTLS), observability (metrics/traces), traffic management, circuit breaking — without modifying application code.

**Products**: Istio, Linkerd, Consul Connect.

**When to use**: > 10 services with complex cross-cutting communication concerns.
