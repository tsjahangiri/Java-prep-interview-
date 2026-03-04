# Dynamic Programming — Basics

## What is Dynamic Programming?
DP solves problems by breaking them into overlapping subproblems and caching results (memoization or tabulation). Key insight: if the same subproblem appears multiple times, solve it once and store the result.

## When to Apply DP
- Optimal substructure: optimal solution builds on optimal subproblem solutions
- Overlapping subproblems: same subproblems recur multiple times

---

## Problem 1: Fibonacci Number

### Step-by-Step Progression
```
Naive recursion:     O(2^n) time, O(n) space
Memoization:         O(n) time, O(n) space
Bottom-up DP:        O(n) time, O(n) space
Space-optimized:     O(n) time, O(1) space
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.HashMap;
import java.util.Map;

public class Fibonacci {

    // ❌ Naive — exponential
    public static long fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    // ✅ Top-down memoization
    private static Map<Integer, Long> memo = new HashMap<>();

    public static long fibMemo(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    // ✅ Bottom-up tabulation
    public static long fibDP(int n) {
        if (n <= 1) return n;
        long[] dp = new long[n + 1];
        dp[0] = 0; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i-1] + dp[i-2];
        return dp[n];
    }

    // ✅ Space-optimized bottom-up
    public static long fibOptimal(int n) {
        if (n <= 1) return n;
        long prev2 = 0, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            long curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }
}
```

---

## Problem 2: 0/1 Knapsack

### Problem Statement
Given `n` items, each with a weight and value, and a knapsack capacity `W`, find the maximum value you can carry. Each item can only be used once.

### Examples
```
weights = [1, 2, 3], values = [6, 10, 12], W = 5
Output: 22  (items 1 and 2: weight 1+2=3 ≤ 5, value 6+10=16... 
             or items 0 and 2: weight 1+3=4 ≤ 5, value 6+12=18...
             or items 1 and 2: 2+3=5 ≤ 5, value 10+12=22 ← optimal)
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class Knapsack {

    /**
     * 0/1 Knapsack using 2D DP table.
     * Time: O(n * W)  Space: O(n * W)
     */
    public static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        // dp[i][w] = max value using first i items with capacity w
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                // Option 1: skip item i
                dp[i][w] = dp[i-1][w];

                // Option 2: take item i (if it fits)
                if (weights[i-1] <= w) {
                    dp[i][w] = Math.max(dp[i][w],
                        dp[i-1][w - weights[i-1]] + values[i-1]);
                }
            }
        }

        return dp[n][capacity];
    }

    /**
     * Space-optimized version: 1D DP array.
     * Time: O(n * W)  Space: O(W)
     * Key: iterate capacity in DECREASING order to avoid using an item twice.
     */
    public static int knapsack1D(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];

        for (int i = 0; i < weights.length; i++) {
            // Traverse right-to-left so we reference old values
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }

        return dp[capacity];
    }

    public static void main(String[] args) {
        int[] weights = {1, 2, 3};
        int[] values  = {6, 10, 12};
        System.out.println(knapsack(weights, values, 5));   // 22
        System.out.println(knapsack1D(weights, values, 5)); // 22
    }
}
```

---

## Problem 3: Longest Common Subsequence

### Problem Statement
Given two strings, find the length of their longest common subsequence (LCS). A subsequence is characters in order, but not necessarily contiguous.

### Examples
```
"abcde" and "ace"   → 3  (LCS: "ace")
"abc"   and "abc"   → 3
"abc"   and "def"   → 0
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class LongestCommonSubsequence {

    /**
     * Bottom-up DP.
     * Time: O(m * n)  Space: O(m * n)
     */
    public static int lcs(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i-1) == text2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1] + 1; // characters match
                } else {
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]); // skip one character
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        System.out.println(lcs("abcde", "ace")); // 3
        System.out.println(lcs("abc", "def"));   // 0
    }
}
```

## DP Problem-Solving Framework

1. **Define the state**: `dp[i]` or `dp[i][j]` — what does this cell represent?
2. **Recurrence**: how does `dp[i]` depend on previous states?
3. **Base cases**: what are the initial/boundary values?
4. **Fill order**: top-down (memoization) or bottom-up (tabulation)?
5. **Answer**: which cell(s) contain the final answer?
