# System Design Fundamentals

## CAP Theorem

A distributed system can guarantee at most **2 of 3** properties simultaneously:

| Property | Meaning |
|----------|---------|
| **C**onsistency | Every read receives the most recent write or an error |
| **A**vailability | Every request receives a response (not necessarily the latest data) |
| **P**artition Tolerance | The system continues operating despite network partitions |

> **In practice**: Network partitions are inevitable, so you must choose **CP** or **AP**.

### Examples
- **CP systems**: HBase, ZooKeeper, etcd (strong consistency; unavailable during partition)
- **AP systems**: DynamoDB, Cassandra, CouchDB (always available; may return stale data)
- **CA systems**: Traditional RDBMS on a single node (no partition tolerance — not practical for distributed systems)

---

## PACELC

An extension of CAP: when a partition occurs, choose between **A**vailability and **C**onsistency; **E**lse (normal operation), choose between **L**atency and **C**onsistency.

| System | Partition | Normal |
|--------|-----------|--------|
| DynamoDB (default) | PA | EL |
| PostgreSQL | PC | EC |
| Cassandra | PA | EL |

---

## Consistency Models (Strongest to Weakest)

1. **Linearizability**: operations appear instantaneous; any read reflects the latest write globally.
2. **Sequential consistency**: all nodes see operations in the same order (not necessarily real-time).
3. **Causal consistency**: causally related operations are seen in order; concurrent ones may differ.
4. **Eventual consistency**: given no new writes, all replicas converge to the same value — eventually.
5. **Read-your-writes**: a user always sees their own writes immediately.

---

## ACID vs BASE

### ACID (Relational Databases)
- **A**tomicity: transaction is all-or-nothing
- **C**onsistency: transaction brings DB from one valid state to another
- **I**solation: concurrent transactions are isolated from each other
- **D**urability: committed transaction survives failures

### BASE (NoSQL/Distributed)
- **B**asically **A**vailable: system is available most of the time
- **S**oft state: state may change over time (even without input)
- **E**ventually consistent: system will become consistent given time

---

## Latency Numbers Every Engineer Should Know (Approximate, 2024)

| Operation | Latency |
|-----------|---------|
| L1 cache reference | 1 ns |
| L2 cache reference | 4 ns |
| Main memory reference | 100 ns |
| SSD random read | 100 μs |
| HDD seek | 10 ms |
| Round trip within datacenter | 0.5 ms |
| Round trip across continents | 150 ms |

---

## Scalability Concepts

### Vertical Scaling (Scale Up)
Add more resources to a single machine (CPU, RAM, SSD). Simple but has limits and a single point of failure.

### Horizontal Scaling (Scale Out)
Add more machines. Requires load balancing and stateless services. No single point of failure.

### Stateless vs Stateful Services
- **Stateless**: any instance can handle any request. Easy to scale horizontally. Session data in Redis/cookie.
- **Stateful**: session tied to specific instance. Requires sticky sessions or shared state.

---

## Reliability Concepts

### SLA / SLO / SLI
- **SLI** (Service Level Indicator): measured metric (e.g., request latency p99)
- **SLO** (Service Level Objective): target value (e.g., p99 latency < 500ms)
- **SLA** (Service Level Agreement): contractual commitment with penalties

### Availability Calculations

| Availability | Annual Downtime |
|---|---|
| 99% ("two nines") | 87.6 hours |
| 99.9% ("three nines") | 8.76 hours |
| 99.99% ("four nines") | 52.6 minutes |
| 99.999% ("five nines") | 5.26 minutes |

For a system of N components in series: `Total availability = A₁ × A₂ × ... × Aₙ`
For N components in parallel (redundancy): `Total unavailability = (1-A₁) × (1-A₂) × ... × (1-Aₙ)`
