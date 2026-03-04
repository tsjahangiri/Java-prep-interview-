# Dynamic Programming — Advanced

---

## Problem 1: Edit Distance (Levenshtein Distance)

### Problem Statement
Given two strings `word1` and `word2`, return the minimum number of operations (insert, delete, replace a character) to convert `word1` to `word2`.

### Examples
```
"horse" → "ros"  : 3 operations
  horse → rorse (replace h→r)
  rorse → rose (delete r)
  rose  → ros (delete e)

"intention" → "execution" : 5
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class EditDistance {

    /**
     * dp[i][j] = min edits to convert word1[0..i-1] to word2[0..j-1]
     * Time: O(m*n)  Space: O(m*n) — reducible to O(min(m,n))
     */
    public static int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Base cases: converting to/from empty string
        for (int i = 0; i <= m; i++) dp[i][0] = i; // delete all chars of word1
        for (int j = 0; j <= n; j++) dp[0][j] = j; // insert all chars of word2

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i-1) == word2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1]; // characters match, no operation
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],  // replace
                                   Math.min(dp[i-1][j],      // delete from word1
                                            dp[i][j-1]));    // insert into word1
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        System.out.println(minDistance("horse", "ros"));       // 3
        System.out.println(minDistance("intention", "execution")); // 5
        System.out.println(minDistance("", "abc"));            // 3
    }
}
```

---

## Problem 2: Word Break

### Problem Statement
Given a string `s` and a dictionary of strings `wordDict`, return `true` if `s` can be segmented into space-separated dictionary words.

### Examples
```
s = "leetcode", wordDict = ["leet","code"]  → true
s = "applepenapple", wordDict = ["apple","pen"]  → true
s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]  → false
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class WordBreak {

    /**
     * dp[i] = can s[0..i-1] be segmented using words in wordDict?
     * Time: O(n^2 * m)  Space: O(n + dict size)
     */
    public static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict); // O(1) lookup
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true; // empty string is always segmentable

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                // If s[0..j-1] is segmentable AND s[j..i-1] is in dict
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break; // no need to check further j values for this i
                }
            }
        }

        return dp[s.length()];
    }
}
```

---

## Problem 3: Coin Change (Unbounded Knapsack)

### Problem Statement
Given an array of coin denominations and an amount, find the minimum number of coins to make up the amount. Each coin can be used unlimited times. Return `-1` if impossible.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.Arrays;

public class CoinChange {

    /**
     * dp[i] = min coins to make amount i
     * Time: O(amount * coins.length)  Space: O(amount)
     */
    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // initialize with "impossible" value
        dp[0] = 0; // 0 coins to make amount 0

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        System.out.println(coinChange(new int[]{1, 5, 11}, 15)); // 3 (5+5+5)
        System.out.println(coinChange(new int[]{2}, 3));         // -1
        System.out.println(coinChange(new int[]{1, 2, 5}, 11));  // 3 (5+5+1)
    }
}
```

---

## Problem 4: Burst Balloons (Interval DP)

### Problem Statement
Given `n` balloons indexed `0` to `n-1`, each with a number. Bursting balloon `i` earns `nums[left] * nums[i] * nums[right]` coins (neighbors). Find the maximum coins you can collect by bursting all balloons.

### Java 17+ Solution
```java
package com.interview.algorithms;

public class BurstBalloons {

    /**
     * Interval DP: dp[i][j] = max coins from bursting all balloons in (i, j) exclusive.
     * Think backwards: k is the LAST balloon burst in interval [i, j].
     * Time: O(n^3)  Space: O(n^2)
     */
    public static int maxCoins(int[] nums) {
        int n = nums.length;
        // Pad with 1s at both ends (virtual boundary balloons)
        int[] padded = new int[n + 2];
        padded[0] = 1; padded[n + 1] = 1;
        for (int i = 0; i < n; i++) padded[i + 1] = nums[i];

        int size = padded.length;
        int[][] dp = new int[size][size];

        // Fill for increasing interval lengths
        for (int len = 2; len < size; len++) {
            for (int left = 0; left < size - len; left++) {
                int right = left + len;
                for (int k = left + 1; k < right; k++) {
                    // k is the last balloon burst in open interval (left, right)
                    dp[left][right] = Math.max(dp[left][right],
                        padded[left] * padded[k] * padded[right]
                        + dp[left][k] + dp[k][right]);
                }
            }
        }

        return dp[0][size - 1];
    }

    public static void main(String[] args) {
        System.out.println(maxCoins(new int[]{3, 1, 5, 8})); // 167
    }
}
```
