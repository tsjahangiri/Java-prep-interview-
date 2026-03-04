# JVM Internals

## JVM Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        JVM                                  │
│  ┌──────────────────┐   ┌────────────────────────────────┐  │
│  │  Class Loader    │   │         Runtime Data Areas     │  │
│  │  - Bootstrap     │   │  ┌──────┐ ┌──────┐ ┌────────┐ │  │
│  │  - Extension     │   │  │Heap  │ │Stack │ │Metaspace│ │  │
│  │  - Application   │   │  └──────┘ └──────┘ └────────┘ │  │
│  └──────────────────┘   └────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Execution Engine: Interpreter → JIT Compiler → GC   │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## Memory Areas

### Heap
- Where objects live; shared among all threads
- Divided into: **Young Generation** (Eden + Survivor S0/S1) and **Old Generation (Tenured)**
- Managed by GC
- Configurable: `-Xms` (initial), `-Xmx` (max)

### Stack (per thread)
- Holds stack frames for method calls
- Each frame: local variables, operand stack, reference to constant pool
- `StackOverflowError` when too deep (e.g., infinite recursion)
- Configurable: `-Xss`

### Metaspace (Java 8+, replaced PermGen)
- Stores class metadata, method bytecode, static variables
- Grows dynamically by default (limited by native memory)
- Configurable: `-XX:MaxMetaspaceSize`

### Method Area / Code Cache
- JIT-compiled native code lives in Code Cache
- Configurable: `-XX:ReservedCodeCacheSize`

---

## Garbage Collection

### Generational Hypothesis
Most objects die young. New objects go to Eden; survivors are promoted through Survivor spaces to Old Gen.

### GC Algorithms (Java 17)

| GC | Flag | Strengths | Use Case |
|---|---|---|---|
| G1GC | `-XX:+UseG1GC` | Balanced throughput & latency | Default since Java 9 |
| ZGC | `-XX:+UseZGC` | Sub-millisecond pauses (Java 15+ production) | Low-latency services |
| Shenandoah | `-XX:+UseShenandoahGC` | Concurrent compaction | Low-latency (Red Hat JDK) |
| Serial | `-XX:+UseSerialGC` | Minimal overhead | Single-threaded, small heap |
| Parallel | `-XX:+UseParallelGC` | High throughput | Batch processing |

### GC Tuning Example
```bash
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=16m \
     -XX:+PrintGCDetails \
     -Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=20m \
     MyApplication
```

---

## Class Loading

### Delegation Model
1. Check if already loaded
2. Delegate to parent classloader
3. If parent fails, load self

```
Bootstrap ClassLoader (JVM built-in, loads rt.jar/java.base)
     ↑
Extension/Platform ClassLoader (loads jdk.* modules)
     ↑
Application ClassLoader (loads classpath)
     ↑
Custom ClassLoaders (OSGi, hot-reload, containers)
```

### Class Loading Phases
1. **Loading**: read `.class` bytes
2. **Linking**: Verify → Prepare (default field values) → Resolve (symbolic references)
3. **Initialization**: execute `<clinit>` (static initializers)

---

## JIT Compilation

- JVM starts with bytecode interpretation (fast startup)
- Hot methods are detected (default: called 10,000 times)
- HotSpot JIT compiles hot methods to native code (C1 → C2 tiered compilation)
- Key optimizations: inlining, escape analysis, loop unrolling, dead code elimination

### Checking JIT Compilations
```bash
java -XX:+PrintCompilation MyApp  # prints methods as they are JIT-compiled
```

---

## Interview Questions

**Q: What is the difference between `String`, `StringBuilder`, and `StringBuffer`?**
- `String`: immutable. Each concatenation creates a new object. Stored in String Pool.
- `StringBuilder`: mutable, single-threaded. Use in loops and local builders.
- `StringBuffer`: mutable, thread-safe (synchronized). Slower; rarely needed; use `StringBuilder` + external sync if needed.

**Q: Explain `String` interning and the String Pool.**
String literals are automatically interned — stored in a pool. `"hello" == "hello"` is `true` because both point to the same object. `new String("hello") == "hello"` is `false`. Use `String.intern()` to manually intern a dynamically created string.

**Q: What causes `OutOfMemoryError`? How do you diagnose it?**
- `java.lang.OutOfMemoryError: Java heap space` — heap full; objects not GC'd
- `java.lang.OutOfMemoryError: Metaspace` — classloaders leaking; too many classes loaded
- `java.lang.OutOfMemoryError: GC overhead limit exceeded` — GC spending >98% of time recovering <2% heap

Diagnosis: heap dump (`jmap -dump:live,format=b,file=heap.hprof <pid>`), analyze with Eclipse MAT or VisualVM.

**Q: What is escape analysis?**
JIT optimization: if an object does not escape the creating method (not passed outside), it may be allocated on the stack instead of heap — avoiding GC pressure. Enabled by default since Java 6: `-XX:+DoEscapeAnalysis`.
