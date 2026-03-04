# Stacks & Queues

## Key Concepts
- Stack: LIFO — `push`, `pop`, `peek` — backed by `ArrayDeque` in Java
- Queue: FIFO — `offer`, `poll`, `peek` — backed by `LinkedList` or `ArrayDeque`
- `Deque` as both stack and queue
- Monotonic stack pattern
- Priority Queue / Min-Heap

---

## Problem 1: Valid Parentheses

### Problem Statement
Given a string containing only `(`, `)`, `{`, `}`, `[`, `]`, determine if the string is valid. Each open bracket must be closed in the correct order.

### Examples
```
"()"      → true
"()[]{}"  → true
"(]"      → false
"([)]"    → false
"{[]}"    → true
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class ValidParentheses {

    /**
     * Stack-based solution.
     * Time: O(n)  Space: O(n)
     */
    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');

        for (char c : s.toCharArray()) {
            if (!pairs.containsKey(c)) {
                stack.push(c); // opening bracket — push to stack
            } else {
                // Closing bracket: stack must have the matching opening bracket on top
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            }
        }

        return stack.isEmpty(); // all brackets matched
    }

    public static void main(String[] args) {
        System.out.println(isValid("()"));      // true
        System.out.println(isValid("()[]{}")); // true
        System.out.println(isValid("(]"));     // false
        System.out.println(isValid("{[]}"));   // true
    }
}
```

---

## Problem 2: Daily Temperatures (Monotonic Stack)

### Problem Statement
Given an array `temperatures`, return an array `answer` where `answer[i]` is the number of days you have to wait after day `i` to get a warmer temperature. If there is no such day, `answer[i] = 0`.

### Examples
```
Input:  [73, 74, 75, 71, 69, 72, 76, 73]
Output: [1,  1,  4,  2,  1,  1,  0,  0]
```

### Step-by-Step Explanation
Use a **monotonic decreasing stack** storing indices:
1. For each day `i`, while the stack is non-empty and `temperatures[i] > temperatures[stack.top()]`:
   - Pop the index `j`; `answer[j] = i - j` (found a warmer day).
2. Push `i` onto the stack.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class DailyTemperatures {

    /**
     * Monotonic stack approach.
     * Time: O(n)  Space: O(n)
     */
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n]; // default 0
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices of pending days

        for (int i = 0; i < n; i++) {
            // Pop all days that found a warmer day at index i
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int j = stack.pop();
                answer[j] = i - j;
            }
            stack.push(i);
        }

        return answer;
    }
}
```

---

## Problem 3: Kth Largest Element (Priority Queue)

### Problem Statement
Find the `k`th largest element in an unsorted array.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.PriorityQueue;

public class KthLargest {

    /**
     * Min-heap of size k.
     * Time: O(n log k)  Space: O(k)
     */
    public static int findKthLargest(int[] nums, int k) {
        // Min-heap: the root is always the smallest of the k largest elements seen so far
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // remove smallest — keeps only k largest
            }
        }

        return minHeap.peek(); // kth largest
    }

    public static void main(String[] args) {
        System.out.println(findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2)); // 5
        System.out.println(findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4)); // 4
    }
}
```

### Variants
1. **Kth smallest**: use a max-heap of size k; pop when size > k.
2. **Streaming top-k**: the min-heap approach works naturally for streaming data.
3. **QuickSelect** (O(n) average): partition-based; faster on average but worst case O(n²).
