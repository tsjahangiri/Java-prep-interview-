# Java Interview Preparation — Mid-Level & Senior Roles (Germany)

> A structured, production-quality resource for Java developers preparing for technical interviews at German companies. Covers core Java, data structures & algorithms, system design, and interview soft skills.

---

## 📁 Repository Structure

```
Java-prep-interview-/
├── README.md                          ← You are here
├── mid-level/
│   ├── README.md
│   ├── core-java/
│   │   ├── 01-collections-and-generics.md
│   │   ├── 02-multithreading-concurrency.md
│   │   ├── 03-java8-streams-lambdas.md
│   │   ├── 04-exception-handling.md
│   │   └── 05-oop-principles.md
│   ├── data-structures/
│   │   ├── 01-arrays-and-strings.md
│   │   ├── 02-linked-lists.md
│   │   ├── 03-trees-and-graphs.md
│   │   ├── 04-stacks-and-queues.md
│   │   └── 05-hash-maps.md
│   └── algorithms/
│       ├── 01-sorting-algorithms.md
│       ├── 02-searching-algorithms.md
│       ├── 03-dynamic-programming-basics.md
│       └── 04-recursion.md
├── senior-level/
│   ├── README.md
│   ├── advanced-java/
│   │   ├── 01-jvm-internals.md
│   │   ├── 02-concurrency-advanced.md
│   │   ├── 03-design-patterns.md
│   │   └── 04-performance-tuning.md
│   ├── algorithms/
│   │   ├── 01-dynamic-programming-advanced.md
│   │   ├── 02-graph-algorithms.md
│   │   └── 03-complex-data-structures.md
│   └── behavioral/
│       ├── 01-star-method.md
│       └── 02-common-behavioral-questions.md
├── system-design/
│   ├── README.md
│   ├── 01-fundamentals.md
│   ├── 02-scalability-patterns.md
│   ├── 03-microservices.md
│   ├── 04-databases-and-caching.md
│   ├── 05-message-queues.md
│   └── 06-common-design-problems.md
├── practice-strategy/
│   ├── README.md
│   ├── 01-daily-schedule.md
│   ├── 02-live-coding-approach.md
│   ├── 03-verbalizing-tradeoffs.md
│   └── 04-german-interview-culture.md
└── code-examples/
    └── src/main/java/com/interview/
        ├── collections/
        │   └── CollectionsDemo.java
        ├── concurrency/
        │   └── ConcurrencyDemo.java
        ├── algorithms/
        │   ├── SortingAlgorithms.java
        │   ├── SearchingAlgorithms.java
        │   └── DynamicProgramming.java
        └── patterns/
            └── DesignPatternsDemo.java
```

---

## 🚀 How to Use This Repository

### For Beginners to Interview Prep
1. Start with **`practice-strategy/01-daily-schedule.md`** to set up your routine.
2. Read **`practice-strategy/04-german-interview-culture.md`** to understand expectations.
3. Work through `mid-level/` topics in order — read the theory, then implement the code examples.

### For Experienced Developers Brushing Up
1. Skim the mid-level topics and focus on the **variants and follow-ups** sections.
2. Deep-dive into `senior-level/advanced-java/` and `system-design/`.
3. Practice verbalizing your solutions using **`practice-strategy/03-verbalizing-tradeoffs.md`**.

### For Each Coding Topic
Each problem file follows this format:
- **Problem Statement** — clear description with constraints
- **Examples** — inputs and expected outputs
- **Step-by-Step Explanation** — before the code
- **Java 17+ Solution** — with inline comments
- **Complexity Analysis** — time and space
- **Variants & Follow-ups** — with brief solution ideas
- **Common Pitfalls** — things to avoid

---

## 📅 Recommended Daily Practice Schedule

| Day | Focus | Time |
|-----|-------|------|
| Mon | Core Java + 2 coding problems | 90 min |
| Tue | Data Structures + 2 coding problems | 90 min |
| Wed | Algorithms + 1 system design | 90 min |
| Thu | Senior Java topics + 2 hard problems | 90 min |
| Fri | Mock interview (timed, no hints) | 60 min |
| Sat | Review mistakes + behavioral prep | 60 min |
| Sun | Rest or light review | 30 min |

See [`practice-strategy/01-daily-schedule.md`](practice-strategy/01-daily-schedule.md) for the full 8-week program.

---

## 🎤 Live Coding Interview Approach

German companies (especially Berlin/Munich tech scene) typically:
- Value **methodical problem-solving** over just producing the answer
- Appreciate when you **clarify requirements** before coding
- Respect candidates who discuss **trade-offs** openly
- Often use real-world scenarios rather than pure algorithmic puzzles

### The 5-Step Framework
1. **Understand** — Repeat the problem, ask clarifying questions (edge cases, constraints)
2. **Plan** — Discuss your approach before writing any code
3. **Code** — Write clean, readable code; think out loud
4. **Test** — Walk through test cases manually
5. **Reflect** — Discuss complexity, improvements, and alternatives

---

## 💬 Verbalizing Trade-offs and Complexity

When discussing solutions:

**For time/space complexity:**
> *"This solution runs in O(n log n) because we sort the input array first, which dominates the O(n) scan that follows. The space complexity is O(1) extra space since the sort is in-place."*

**For trade-offs:**
> *"I chose a HashMap here for O(1) average lookup, at the cost of O(n) extra space. If memory is constrained, a sorted array with binary search would give O(log n) lookup with O(1) extra space — but that changes the overall complexity."*

**For alternative approaches:**
> *"Another approach would be to use a two-pointer technique. It has the same time complexity but avoids the hash map overhead. I'll go with the hash map version first for clarity, and we can optimize if needed."*

---

## 🛠 Running the Code Examples

Requirements: Java 17+, Maven or your IDE of choice.

```bash
# Compile a single file
javac --release 17 code-examples/src/main/java/com/interview/algorithms/SortingAlgorithms.java

# Or open the code-examples/ folder in IntelliJ IDEA / VS Code
```

---

## 📌 Suggested Repository Name
`java-interview-prep-germany`

## 📝 Suggested First Commit Message
`feat: add structured Java interview prep materials for mid/senior roles`

---

## Git Setup Commands

```bash
git init
git add .
git commit -m "feat: add structured Java interview prep materials for mid/senior roles"
git remote add origin https://github.com/<your-username>/java-interview-prep-germany.git
git branch -M main
git push -u origin main
```
