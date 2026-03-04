# OOP Principles

## SOLID Principles Quick Reference

| Letter | Principle | One-Liner |
|--------|-----------|-----------|
| **S** | Single Responsibility | A class should have one reason to change |
| **O** | Open/Closed | Open for extension, closed for modification |
| **L** | Liskov Substitution | Subtypes must be substitutable for their base type |
| **I** | Interface Segregation | Many specific interfaces > one general interface |
| **D** | Dependency Inversion | Depend on abstractions, not concretions |

---

## Problem: Design a Notification Service (OOP Design)

### Problem Statement
Design a notification service that can send messages via Email, SMS, and Push notifications. The system should be easily extensible to add new channels without modifying existing code.

### Step-by-Step Design
1. Define a `NotificationChannel` interface (single abstract method → open for extension).
2. Create concrete implementations for each channel.
3. A `NotificationService` depends on the interface, not concrete classes (DIP).
4. Use a registry to add channels dynamically (OCP — add channels without modifying `NotificationService`).

### Java 17+ Solution
```java
package com.interview.collections;

import java.util.*;

// Interface — NotificationService depends on this abstraction, not concrete classes (DIP)
public interface NotificationChannel {
    void send(String recipient, String message);
    String channelName();
}

// Email implementation
class EmailChannel implements NotificationChannel {

    @Override
    public void send(String recipient, String message) {
        System.out.printf("[EMAIL] To: %s | Message: %s%n", recipient, message);
        // In production: call email service API
    }

    @Override
    public String channelName() { return "EMAIL"; }
}

// SMS implementation
class SmsChannel implements NotificationChannel {

    @Override
    public void send(String recipient, String message) {
        System.out.printf("[SMS] To: %s | Message: %s%n", recipient, message);
    }

    @Override
    public String channelName() { return "SMS"; }
}

// Push notification implementation — new channel added without modifying existing code (OCP)
class PushChannel implements NotificationChannel {

    @Override
    public void send(String recipient, String message) {
        System.out.printf("[PUSH] To: %s | Message: %s%n", recipient, message);
    }

    @Override
    public String channelName() { return "PUSH"; }
}

// NotificationService — orchestrates sending; does not know channel internals (DIP)
class NotificationService {

    // Using Map for O(1) lookup by channel name
    private final Map<String, NotificationChannel> channels = new HashMap<>();

    public void registerChannel(NotificationChannel channel) {
        channels.put(channel.channelName(), channel);
    }

    public void sendVia(String channelName, String recipient, String message) {
        NotificationChannel channel = channels.get(channelName);
        if (channel == null) {
            throw new IllegalArgumentException("Unknown channel: " + channelName);
        }
        channel.send(recipient, message);
    }

    public void sendToAll(String recipient, String message) {
        channels.values().forEach(ch -> ch.send(recipient, message));
    }
}

// Demo
class OOPDemo {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.registerChannel(new EmailChannel());
        service.registerChannel(new SmsChannel());
        service.registerChannel(new PushChannel());

        service.sendVia("EMAIL", "alice@example.com", "Your order shipped!");
        service.sendToAll("bob@example.com", "System maintenance tonight.");
    }
}
```

---

## Key OOP Interview Questions

**Q: What is the difference between composition and inheritance? When to use each?**
- **Inheritance** ("is-a"): models an IS-A relationship. Beware deep hierarchies — they become fragile (tight coupling).
- **Composition** ("has-a"): one class contains another. More flexible; behaviour can be changed at runtime by swapping the contained object.
- **Rule of thumb**: Prefer composition over inheritance unless a genuine IS-A relationship exists and the Liskov Substitution Principle holds.

**Q: What is the Liskov Substitution Principle? Give a counterexample.**
LSP: wherever a base type is used, you must be able to substitute a subtype without altering program correctness.

Classic violation — `Square extends Rectangle`:
```java
Rectangle r = new Square();
r.setWidth(5);   // Square also sets height to 5
r.setHeight(3);  // Square also sets width to 3
// Expected area: 15; actual: 9 — LSP violated
```
Fix: do not inherit `Square` from `Rectangle` if their invariants differ.

**Q: Abstract class vs interface — when to use which?**
- **Abstract class**: when subclasses share state/implementation, and you want a "template". Can have constructors, instance fields, concrete methods.
- **Interface**: when unrelated classes share behaviour (e.g., `Comparable`, `Serializable`). Java 8+ interfaces can have `default` and `static` methods.
- **Java 8+ rule**: if you need default behaviour in an interface without state, use `default` methods. If you need state (fields), use an abstract class.
