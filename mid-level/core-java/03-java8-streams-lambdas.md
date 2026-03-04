# Java 8+ Streams & Lambdas

## Key Concepts to Know
- Lambda syntax and functional interfaces (`Function`, `Predicate`, `Consumer`, `Supplier`)
- Stream pipeline: source → intermediate operations → terminal operation
- Lazy evaluation in streams
- `Optional` — proper use and anti-patterns
- Method references (`Class::method`, `instance::method`, `Class::new`)
- Collectors: `toList()`, `groupingBy()`, `partitioningBy()`, `joining()`

---

## Problem 1: Salary Statistics with Streams

### Problem Statement
Given a list of `Employee` objects (with `name`, `department`, and `salary` fields), compute:
1. The average salary per department.
2. The highest-paid employee in each department.
3. All employees earning above the company average.

### Java 17+ Solution
```java
package com.interview.collections;

import java.util.*;
import java.util.stream.*;

public class SalaryStatistics {

    record Employee(String name, String department, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("Alice",   "Engineering", 90_000),
            new Employee("Bob",     "Engineering", 85_000),
            new Employee("Carol",   "Marketing",   70_000),
            new Employee("Dave",    "Marketing",   75_000),
            new Employee("Eve",     "Engineering", 95_000),
            new Employee("Frank",   "HR",          60_000)
        );

        // 1. Average salary per department
        Map<String, Double> avgByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.averagingDouble(Employee::salary)
            ));
        System.out.println("Average salary by department: " + avgByDept);

        // 2. Highest-paid per department
        Map<String, Optional<Employee>> topEarnerByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.maxBy(Comparator.comparingDouble(Employee::salary))
            ));
        topEarnerByDept.forEach((dept, emp) ->
            emp.ifPresent(e -> System.out.printf("Top earner in %-15s: %s (%.0f)%n",
                dept, e.name(), e.salary())));

        // 3. Employees above company average
        double companyAvg = employees.stream()
            .mapToDouble(Employee::salary)
            .average()
            .orElse(0.0);

        List<String> aboveAverage = employees.stream()
            .filter(e -> e.salary() > companyAvg)
            .map(Employee::name)
            .sorted()
            .collect(Collectors.toList());

        System.out.printf("Company average: %.0f%n", companyAvg);
        System.out.println("Above average: " + aboveAverage);
    }
}
```

### Complexity Analysis
| Operation | Time | Space |
|---|---|---|
| groupingBy average | O(n) | O(k) — k departments |
| maxBy per dept | O(n) | O(k) |
| filter above avg | O(n) | O(n) worst case |

### Variants & Follow-ups

**1. Top 3 earners company-wide:**
```java
employees.stream()
    .sorted(Comparator.comparingDouble(Employee::salary).reversed())
    .limit(3)
    .collect(Collectors.toList());
```

**2. Partition into above/below average:**
```java
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() > companyAvg));
```

---

## Problem 2: Flatten and Deduplicate Nested Lists

### Problem Statement
Given a `List<List<Integer>>`, flatten it into a single list with duplicates removed, sorted in ascending order.

### Java 17+ Solution
```java
package com.interview.collections;

import java.util.*;
import java.util.stream.*;

public class FlattenAndDeduplicate {

    public static List<Integer> flattenAndSort(List<List<Integer>> nested) {
        return nested.stream()
            .flatMap(Collection::stream)   // flatten nested lists
            .distinct()                     // remove duplicates
            .sorted()                       // sort ascending
            .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<List<Integer>> input = List.of(
            List.of(3, 1, 4, 1),
            List.of(5, 9, 2, 6),
            List.of(5, 3, 5)
        );
        System.out.println(flattenAndSort(input)); // [1, 2, 3, 4, 5, 6, 9]
    }
}
```

### Complexity Analysis
| | Complexity |
|---|---|
| **Time** | O(N log N) — N = total elements, dominated by sort |
| **Space** | O(N) — for the result list |

---

## Common Pitfalls with Streams

- ❌ **Reusing a consumed stream** — a stream can only be traversed once; calling a terminal operation twice throws `IllegalStateException`.
- ❌ **Modifying source collection inside a stream** — causes `ConcurrentModificationException`.
- ❌ **Using `Optional.get()` without checking** — equivalent to a null check you forgot to write. Use `orElse()`, `orElseThrow()`, or `ifPresent()`.
- ❌ **Parallel streams on small collections** — the overhead of thread coordination often exceeds the benefit. Use parallel streams only for CPU-intensive, large-data operations.
- ❌ **Stateful lambdas in parallel streams** — mutable state shared across lambda invocations leads to race conditions.

---

## Interview Theory Questions

**Q: What is a functional interface? Name five built-in ones.**
A functional interface has exactly one abstract method. Java 8+ built-ins:
- `Function<T,R>` — takes T, returns R
- `Predicate<T>` — takes T, returns boolean
- `Consumer<T>` — takes T, returns void
- `Supplier<T>` — takes nothing, returns T
- `BiFunction<T,U,R>` — takes T and U, returns R

**Q: What is the difference between `map()` and `flatMap()`?**
- `map()`: applies a function to each element, producing one output per input. Result is `Stream<Stream<R>>` if the function itself returns a stream.
- `flatMap()`: applies a function that returns a stream per element, then flattens all those streams into one. Result is `Stream<R>`.

**Q: When should you use `Optional`?**
- Return type of methods that may legitimately produce no value.
- **Not** for method parameters (use overloads instead).
- **Not** for class fields (it is not serializable and adds overhead).
- **Not** as a nullable replacement for collections (return empty collections instead).
