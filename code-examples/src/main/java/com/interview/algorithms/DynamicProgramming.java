package com.interview.algorithms;

import java.util.*;

/**
 * Classic Dynamic Programming problems for interview practice.
 *
 * Problems covered:
 * 1. Fibonacci (with all 4 approaches)
 * 2. 0/1 Knapsack
 * 3. Longest Common Subsequence (LCS)
 * 4. Coin Change
 * 5. Edit Distance
 * 6. Longest Increasing Subsequence (LIS)
 */
public class DynamicProgramming {

    // ────────────────────────────────────────────────────────────
    // 1. Fibonacci — Space-Optimized Bottom-Up
    // Time: O(n)  Space: O(1)
    // ────────────────────────────────────────────────────────────

    public static long fibonacci(int n) {
        if (n <= 1) return n;
        long prev2 = 0, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            long curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }

    // ────────────────────────────────────────────────────────────
    // 2. 0/1 Knapsack — Space-Optimized 1D DP
    // Time: O(n * W)  Space: O(W)
    // ────────────────────────────────────────────────────────────

    /**
     * @param weights item weights
     * @param values  item values
     * @param W       knapsack capacity
     * @return maximum value achievable without exceeding capacity
     */
    public static int knapsack(int[] weights, int[] values, int W) {
        int[] dp = new int[W + 1];
        for (int i = 0; i < weights.length; i++) {
            // Traverse right-to-left to avoid using the same item twice
            for (int w = W; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
        return dp[W];
    }

    // ────────────────────────────────────────────────────────────
    // 3. Longest Common Subsequence
    // Time: O(m * n)  Space: O(m * n)
    // ────────────────────────────────────────────────────────────

    public static int lcs(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // ────────────────────────────────────────────────────────────
    // 4. Coin Change — Minimum Coins
    // Time: O(amount * coins.length)  Space: O(amount)
    // ────────────────────────────────────────────────────────────

    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // sentinel: "impossible"
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // ────────────────────────────────────────────────────────────
    // 5. Edit Distance (Levenshtein)
    // Time: O(m * n)  Space: O(m * n)
    // ────────────────────────────────────────────────────────────

    public static int editDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                   Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    // ────────────────────────────────────────────────────────────
    // 6. Longest Increasing Subsequence
    // Time: O(n log n) with patience sorting  Space: O(n)
    // ────────────────────────────────────────────────────────────

    public static int lis(int[] nums) {
        // tails[i] = smallest tail element of increasing subsequences of length i+1
        int[] tails = new int[nums.length];
        int len = 0;
        for (int num : nums) {
            int lo = 0, hi = len;
            while (lo < hi) {                   // binary search for insertion point
                int mid = lo + (hi - lo) / 2;
                if (tails[mid] < num) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = num;
            if (lo == len) len++;
        }
        return len;
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Fibonacci ===");
        System.out.println("fib(10) = " + fibonacci(10)); // 55
        System.out.println("fib(50) = " + fibonacci(50)); // 12586269025

        System.out.println("\n=== 0/1 Knapsack ===");
        int[] weights = {1, 2, 3};
        int[] values  = {6, 10, 12};
        System.out.println("Max value (capacity=5): " + knapsack(weights, values, 5)); // 22

        System.out.println("\n=== LCS ===");
        System.out.println("LCS(abcde, ace) = " + lcs("abcde", "ace")); // 3
        System.out.println("LCS(abc, def) = " + lcs("abc", "def")); // 0

        System.out.println("\n=== Coin Change ===");
        System.out.println("coins=[1,5,11] amount=15: " + coinChange(new int[]{1,5,11}, 15)); // 3
        System.out.println("coins=[2] amount=3: " + coinChange(new int[]{2}, 3)); // -1

        System.out.println("\n=== Edit Distance ===");
        System.out.println("horse → ros: " + editDistance("horse", "ros")); // 3
        System.out.println("intention → execution: " + editDistance("intention", "execution")); // 5

        System.out.println("\n=== LIS ===");
        System.out.println("LIS([10,9,2,5,3,7,101,18]): " + lis(new int[]{10,9,2,5,3,7,101,18})); // 4
    }
}
