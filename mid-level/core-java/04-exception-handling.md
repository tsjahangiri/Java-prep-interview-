# Exception Handling

## Key Concepts to Know
- Checked vs unchecked exceptions
- Exception hierarchy: `Throwable` → `Error` / `Exception` → `RuntimeException`
- `try-with-resources` (AutoCloseable)
- Multi-catch blocks
- Custom exceptions — when and how to create them
- Exception chaining with `initCause()` / constructor
- Anti-patterns: swallowing exceptions, over-catching

---

## Core Interview Questions

**Q: What is the difference between checked and unchecked exceptions?**
- **Checked exceptions** extend `Exception` (but not `RuntimeException`). The compiler enforces handling with `try-catch` or declaring with `throws`. Example: `IOException`, `SQLException`.
- **Unchecked exceptions** extend `RuntimeException`. No compiler enforcement. Example: `NullPointerException`, `IllegalArgumentException`.
- Use checked exceptions for recoverable conditions callers should handle. Use unchecked for programming errors (precondition violations).

**Q: What does `finally` guarantee?**
The `finally` block runs after `try`/`catch` regardless of whether an exception was thrown or caught — **except** when `System.exit()` is called or the JVM crashes. It runs even if there is a `return` statement in the `try` block.

**Q: Explain `try-with-resources`.**
Any object implementing `AutoCloseable` (or `Closeable`) declared in the `try(...)` header is automatically closed when the block exits, even on exception. Suppressed exceptions from `close()` are attached to the primary exception via `getSuppressed()`.

```java
// Pre-Java 7 — prone to resource leaks
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    // ...
} finally {
    if (br != null) br.close(); // what if close() throws?
}

// Java 7+ — clean, exception-safe
try (var br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} // br.close() called automatically
```

---

## Problem: Custom Exception Hierarchy

### Problem Statement
Design a custom exception hierarchy for a banking application that distinguishes between insufficient funds, invalid account, and service unavailable errors.

### Java 17+ Solution
```java
package com.interview.collections;

// Base application exception — checked, callers are expected to handle it
public class BankingException extends Exception {

    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause); // preserve original cause for debugging
    }
}

// Specific sub-exception: insufficient balance
public class InsufficientFundsException extends BankingException {

    private final double requested;
    private final double available;

    public InsufficientFundsException(double requested, double available) {
        super(String.format(
            "Requested %.2f but only %.2f available", requested, available));
        this.requested = requested;
        this.available = available;
    }

    public double getShortfall() {
        return requested - available;
    }
}

// Service availability — may wrap a lower-level IOException
public class BankServiceUnavailableException extends BankingException {

    public BankServiceUnavailableException(String service, Throwable cause) {
        super("Service unavailable: " + service, cause);
    }
}

// Usage example
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);
        }
        balance -= amount;
    }

    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.0);
        try {
            account.withdraw(150.0);
        } catch (InsufficientFundsException e) {
            System.out.println("Cannot withdraw: " + e.getMessage());
            System.out.printf("Shortfall: %.2f%n", e.getShortfall());
        }
    }
}
```

---

## Common Anti-Patterns

```java
// ❌ Swallowing exception — hides bugs silently
try {
    riskyOperation();
} catch (Exception e) {
    // nothing here — terrible practice
}

// ❌ Catching Exception/Throwable too broadly
try {
    connect();
} catch (Exception e) {
    // catches NullPointerException, OutOfMemoryError, everything
}

// ❌ Using exceptions for flow control (performance + readability)
try {
    int value = Integer.parseInt(input);
} catch (NumberFormatException e) {
    // not a number — should have checked first
}
// ✅ Better
if (input.matches("\\d+")) {
    int value = Integer.parseInt(input);
}

// ✅ Always log or rethrow
try {
    riskyOperation();
} catch (IOException e) {
    log.error("Operation failed", e); // preserve stack trace
    throw new ServiceException("Failed to complete operation", e);
}
```
