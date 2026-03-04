# Arrays & Strings

Arrays and strings are the most frequently tested topics in Java interviews. Most problems involve: two-pointer technique, sliding window, prefix sums, or sorting.

---

## Problem 1: Two Sum

### Problem Statement
Given an integer array `nums` and a target integer, return the indices of the two numbers that add up to `target`. You may assume exactly one solution exists, and you may not use the same element twice.

**Constraints:** 2 ≤ n ≤ 10⁴, -10⁹ ≤ nums[i] ≤ 10⁹

### Examples
```
Input:  nums = [2, 7, 11, 15], target = 9
Output: [0, 1]   // nums[0] + nums[1] = 2 + 7 = 9

Input:  nums = [3, 2, 4], target = 6
Output: [1, 2]
```

### Step-by-Step Explanation
**Brute force** (O(n²)): for each pair (i, j), check if nums[i] + nums[j] == target.

**Optimal** (O(n)): As we iterate, store each number's index in a HashMap. For the current element, check if `target - nums[i]` is already in the map.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class TwoSum {

    /**
     * Finds indices of two numbers that sum to target.
     * Time: O(n)  Space: O(n)
     */
    public static int[] twoSum(int[] nums, int target) {
        // Map from value → its index
        Map<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (seen.containsKey(complement)) {
                // Found the pair: complement was seen earlier at index seen.get(complement)
                return new int[]{seen.get(complement), i};
            }

            // Store current value and its index for future lookups
            seen.put(nums[i], i);
        }

        throw new IllegalArgumentException("No solution exists"); // per problem guarantee, unreachable
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));  // [0, 1]
        System.out.println(Arrays.toString(twoSum(new int[]{3, 2, 4}, 6)));       // [1, 2]
    }
}
```

### Complexity Analysis
| | Brute Force | Optimal |
|--|--|--|
| **Time** | O(n²) | O(n) |
| **Space** | O(1) | O(n) |

### Variants & Follow-ups
1. **Return values instead of indices**: sort the array and use two pointers — O(n log n) time, O(1) space.
2. **Three Sum** (find triplets summing to zero): sort + two pointers, O(n²) time.
3. **Two Sum in sorted array**: two pointers `left` and `right` — O(n) time, O(1) space.

### Common Pitfalls
- ❌ Storing the index before checking — you might use the same element twice. Check FIRST, then store.
- ❌ Assuming the array contains no duplicates — the problem allows them.

---

## Problem 2: Longest Substring Without Repeating Characters

### Problem Statement
Given a string `s`, find the length of the longest substring without repeating characters.

**Constraints:** 0 ≤ n ≤ 5×10⁴, `s` contains English letters, digits, symbols, spaces.

### Examples
```
Input:  "abcabcbb"   Output: 3  ("abc")
Input:  "bbbbb"      Output: 1  ("b")
Input:  "pwwkew"     Output: 3  ("wke")
```

### Step-by-Step Explanation
**Sliding Window**: maintain a window `[left, right]` with no duplicates.
- Expand right pointer.
- When a duplicate is found at `right`, shrink the window from the left until the duplicate is removed.
- Track the maximum window size.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class LongestSubstringNonRepeating {

    /**
     * Sliding window approach.
     * Time: O(n)  Space: O(min(n, alphabet_size))
     */
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>(); // char → last seen index
        int maxLen = 0;
        int left = 0; // left boundary of current window

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // If character was seen and is within current window, shrink from left
            if (lastIndex.containsKey(c) && lastIndex.get(c) >= left) {
                left = lastIndex.get(c) + 1; // move left past the duplicate
            }

            lastIndex.put(c, right); // update last seen index
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(lengthOfLongestSubstring("bbbbb"));    // 1
        System.out.println(lengthOfLongestSubstring("pwwkew"));   // 3
        System.out.println(lengthOfLongestSubstring(""));         // 0
    }
}
```

### Complexity Analysis
| | Complexity |
|--|--|
| **Time** | O(n) — each character visited at most twice (once by `right`, once by `left`) |
| **Space** | O(min(n, k)) — k = alphabet size |

### Common Pitfalls
- ❌ Checking `lastIndex.containsKey(c)` without the `>= left` guard — the old index may be outside the current window.
- ❌ Not handling the empty string case (`""` → return `0`).

---

## Problem 3: Maximum Subarray (Kadane's Algorithm)

### Problem Statement
Find the contiguous subarray with the largest sum.

### Examples
```
Input:  [-2, 1, -3, 4, -1, 2, 1, -5, 4]
Output: 6   (subarray: [4, -1, 2, 1])
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class MaximumSubarray {

    /**
     * Kadane's algorithm.
     * Time: O(n)  Space: O(1)
     */
    public static int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            // Either extend the previous subarray or start fresh from nums[i]
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        System.out.println(maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4})); // 6
        System.out.println(maxSubArray(new int[]{1}));                               // 1
        System.out.println(maxSubArray(new int[]{-1, -2}));                         // -1
    }
}
```

### Variants & Follow-ups
1. **Return the subarray itself**: track `start`, `end`, and `tempStart` indices.
2. **Maximum product subarray**: track both `maxProd` and `minProd` (because a negative × negative = positive).
3. **Circular subarray**: `max(normal max subarray, total sum - min subarray)`.
