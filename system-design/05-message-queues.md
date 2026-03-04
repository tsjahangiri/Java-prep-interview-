# Message Queues & Event Streaming

---

## RabbitMQ vs Apache Kafka

| Feature | RabbitMQ | Apache Kafka |
|---------|----------|-------------|
| **Model** | Message queue (push) | Event log (pull) |
| **Message retention** | Deleted after consumed | Retained for configurable period |
| **Consumer model** | Competing consumers (each message consumed once) | Consumer groups (replay possible) |
| **Throughput** | ~50K msgs/sec | Millions/sec |
| **Ordering** | Per-queue | Per-partition |
| **Use case** | Task queues, RPC, routing | Event sourcing, streaming analytics, audit logs |

---

## Kafka Core Concepts

```
Producer → [Topic: orders] → Partition 0: [msg1, msg2, msg3...]
                            → Partition 1: [msg4, msg5, msg6...]
                            → Partition 2: [msg7, msg8, msg9...]
                                    ↓
                          Consumer Group A (one consumer per partition)
                          Consumer Group B (independent offset tracking)
```

### Key Properties
- **Topics**: logical stream of messages
- **Partitions**: unit of parallelism; messages in a partition are ordered
- **Consumer Groups**: multiple consumers in a group share the partitions; each partition assigned to one consumer
- **Offset**: position of a consumer in a partition (stored in `__consumer_offsets` topic)
- **Replication Factor**: how many brokers store a copy (typically 3 for production)

### Delivery Semantics
- **At-most-once**: commit offset before processing — may lose messages
- **At-least-once**: commit offset after processing — may process duplicates (idempotency required)
- **Exactly-once**: Kafka Transactions API — most complex, some overhead

### Java Kafka Consumer (Spring Boot)
```java
@KafkaListener(topics = "orders", groupId = "order-processor")
public void processOrder(@Payload String orderJson,
                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                          Acknowledgment acknowledgment) {
    try {
        Order order = objectMapper.readValue(orderJson, Order.class);
        orderService.process(order);
        acknowledgment.acknowledge(); // manual commit after successful processing
    } catch (Exception e) {
        log.error("Failed to process order: {}", orderJson, e);
        // Send to dead-letter topic instead of retrying forever
        deadLetterTemplate.send("orders.DLT", orderJson);
        acknowledgment.acknowledge();
    }
}
```

---

## Event Sourcing

Instead of storing current state, store the sequence of events that led to it.

```
Events: [AccountCreated, MoneyDeposited(100), MoneyWithdrawn(30), MoneyDeposited(50)]
State:  balance = 0 + 100 - 30 + 50 = 120
```

**Benefits**:
- Full audit log for free
- Time-travel: rebuild state at any point in time
- Event replay for new projections

**Challenges**:
- Schema evolution: old events must remain processable
- Snapshot pattern: rebuild state from scratch is slow; periodically snapshot current state + apply events since snapshot
- Eventual consistency in read projections

---

## Dead Letter Queue (DLQ)

Messages that cannot be processed go to a DLQ for:
- Manual inspection
- Alert triggering
- Retry after bug fix

**Pattern**: max retry policy → DLQ → alerting → manual replay after fix.

---

## Outbox Pattern

Ensures DB write and event publish are atomic — without a distributed transaction:

1. In the same DB transaction as the business write, insert an event record into an `outbox` table.
2. A background process (CDC or polling) reads the `outbox` table and publishes events to Kafka.
3. Mark events as published.

```sql
-- Same transaction:
INSERT INTO orders (id, ...) VALUES (...);
INSERT INTO outbox (event_type, payload, status) VALUES ('OrderCreated', '...', 'PENDING');

-- Background relay:
SELECT * FROM outbox WHERE status = 'PENDING' LIMIT 100;
-- publish to Kafka
UPDATE outbox SET status = 'PUBLISHED' WHERE id IN (...);
```

**Tools**: Debezium (CDC), Spring Modulith outbox support, custom polling.
