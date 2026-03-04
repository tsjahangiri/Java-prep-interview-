# Hash Maps

## Key Concepts
- Hash functions, collision resolution (chaining, open addressing)
- Load factor and rehashing
- `HashMap`, `LinkedHashMap`, `TreeMap`, `ConcurrentHashMap`
- Common patterns: frequency counting, complement lookup, grouping

---

## Problem 1: Top K Frequent Elements

### Problem Statement
Given an integer array, return the `k` most frequent elements. Order does not matter.

### Examples
```
Input:  nums = [1,1,1,2,2,3], k = 2
Output: [1, 2]

Input:  nums = [1], k = 1
Output: [1]
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class TopKFrequent {

    /**
     * Bucket sort approach — O(n) time.
     */
    public static int[] topKFrequent(int[] nums, int k) {
        // Step 1: count frequencies
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        // Step 2: bucket sort by frequency
        // buckets[i] = list of numbers that appear exactly i times
        @SuppressWarnings("unchecked")
        List<Integer>[] buckets = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int f = e.getValue();
            if (buckets[f] == null) buckets[f] = new ArrayList<>();
            buckets[f].add(e.getKey());
        }

        // Step 3: collect top k from highest-frequency buckets
        int[] result = new int[k];
        int idx = 0;
        for (int f = buckets.length - 1; f >= 0 && idx < k; f--) {
            if (buckets[f] != null) {
                for (int num : buckets[f]) {
                    if (idx == k) break;
                    result[idx++] = num;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(topKFrequent(new int[]{1,1,1,2,2,3}, 2)));
        // [1, 2]
    }
}
```

### Complexity Analysis
| | Time | Space |
|--|--|--|
| Heap approach | O(n log k) | O(n) |
| Bucket sort approach | O(n) | O(n) |

---

## Problem 2: Subarray Sum Equals K

### Problem Statement
Given an integer array and an integer `k`, return the total number of subarrays whose sum equals `k`.

### Examples
```
Input:  nums = [1, 1, 1], k = 2    Output: 2
Input:  nums = [1, 2, 3], k = 3    Output: 2
```

### Step-by-Step Explanation
Use a **prefix sum + HashMap**:
- `prefixSum[i]` = sum of `nums[0..i-1]`
- A subarray `[j+1..i]` sums to k iff `prefixSum[i] - prefixSum[j] = k`, i.e., `prefixSum[j] = prefixSum[i] - k`.
- Count how many times we've seen `prefixSum[i] - k` in the map.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class SubarraySumEqualsK {

    /**
     * Prefix sum + HashMap.
     * Time: O(n)  Space: O(n)
     */
    public static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1); // empty prefix has sum 0, count 1

        int count = 0;
        int prefixSum = 0;

        for (int num : nums) {
            prefixSum += num;

            // Check if any earlier prefix sum makes a subarray sum to k
            count += prefixCount.getOrDefault(prefixSum - k, 0);

            // Record this prefix sum
            prefixCount.merge(prefixSum, 1, Integer::sum);
        }

        return count;
    }

    public static void main(String[] args) {
        System.out.println(subarraySum(new int[]{1, 1, 1}, 2)); // 2
        System.out.println(subarraySum(new int[]{1, 2, 3}, 3)); // 2
    }
}
```

### Common Pitfall
❌ Forgetting to seed the map with `{0: 1}` — without it, subarrays starting at index 0 that sum to k are missed.
