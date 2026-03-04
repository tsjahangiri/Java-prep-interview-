# Searching Algorithms

---

## Problem 1: Binary Search

### Problem Statement
Given a **sorted** array and a target value, return the index of the target. If it does not exist, return `-1`.

### Java 17+ Solution
```java
package com.interview.algorithms;

public class BinarySearch {

    /**
     * Standard binary search on a sorted array.
     * Time: O(log n)  Space: O(1)
     */
    public static int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // avoids integer overflow
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) left = mid + 1;
            else                   right = mid - 1;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {-1, 0, 3, 5, 9, 12};
        System.out.println(binarySearch(arr, 9));  // 4
        System.out.println(binarySearch(arr, 2));  // -1
    }
}
```

---

## Problem 2: Search in Rotated Sorted Array

### Problem Statement
A sorted array was rotated at some pivot. Given the rotated array and a target, return its index or `-1`.

### Examples
```
Input:  nums = [4, 5, 6, 7, 0, 1, 2], target = 0    Output: 4
Input:  nums = [4, 5, 6, 7, 0, 1, 2], target = 3    Output: -1
```

### Step-by-Step Explanation
Standard binary search still works — one half of the array is always sorted:
1. Check if the left half is sorted (`nums[left] <= nums[mid]`).
2. If yes, check if target falls in `[nums[left], nums[mid]]`; if so, search left. Otherwise, search right.
3. If the right half is sorted, apply the same logic for the right half.

### Java 17+ Solution
```java
package com.interview.algorithms;

public class SearchRotatedArray {

    /**
     * Modified binary search on a rotated sorted array.
     * Time: O(log n)  Space: O(1)
     */
    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return mid;

            // Left half is sorted
            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1; // target in left half
                } else {
                    left = mid + 1;  // target in right half
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;  // target in right half
                } else {
                    right = mid - 1; // target in left half
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        System.out.println(search(new int[]{4,5,6,7,0,1,2}, 0)); // 4
        System.out.println(search(new int[]{4,5,6,7,0,1,2}, 3)); // -1
    }
}
```

---

## Problem 3: Find First and Last Position (Binary Search on Boundary)

### Problem Statement
Given a sorted array with possible duplicates and a target, find the first and last position of the target. Return `[-1, -1]` if not found.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.Arrays;

public class FirstLastPosition {

    public static int[] searchRange(int[] nums, int target) {
        return new int[]{findFirst(nums, target), findLast(nums, target)};
    }

    private static int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // keep searching left for earlier occurrence
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private static int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // keep searching right for later occurrence
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(searchRange(new int[]{5,7,7,8,8,10}, 8)));
        // [3, 4]
        System.out.println(Arrays.toString(searchRange(new int[]{5,7,7,8,8,10}, 6)));
        // [-1, -1]
    }
}
```

### Common Pitfall
❌ Using `mid = (left + right) / 2` — can overflow when both `left` and `right` are large ints. Always use `left + (right - left) / 2`.
