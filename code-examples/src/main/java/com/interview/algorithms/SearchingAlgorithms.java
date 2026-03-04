package com.interview.algorithms;

/**
 * Common searching algorithms for interview practice.
 *
 * Covers: binary search and its variants (first/last occurrence,
 * search in rotated array, find peak element).
 */
public class SearchingAlgorithms {

    // ────────────────────────────────────────────────────────────
    // Standard Binary Search
    // Time: O(log n)  Space: O(1)
    // ────────────────────────────────────────────────────────────

    /**
     * Returns the index of target in a sorted array, or -1 if not found.
     */
    public static int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2; // avoids overflow
            if (nums[mid] == target) return mid;
            if (nums[mid] < target)  left = mid + 1;
            else                     right = mid - 1;
        }
        return -1;
    }

    // ────────────────────────────────────────────────────────────
    // First and Last Occurrence
    // ────────────────────────────────────────────────────────────

    public static int[] searchRange(int[] nums, int target) {
        return new int[]{firstOccurrence(nums, target), lastOccurrence(nums, target)};
    }

    private static int firstOccurrence(int[] nums, int target) {
        int left = 0, right = nums.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) { result = mid; right = mid - 1; } // keep searching left
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return result;
    }

    private static int lastOccurrence(int[] nums, int target) {
        int left = 0, right = nums.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) { result = mid; left = mid + 1; } // keep searching right
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // Search in Rotated Sorted Array
    // Time: O(log n)  Space: O(1)
    // ────────────────────────────────────────────────────────────

    public static int searchRotated(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[left] <= nums[mid]) {           // left half is sorted
                if (nums[left] <= target && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {                                  // right half is sorted
                if (nums[mid] < target && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    // ────────────────────────────────────────────────────────────
    // Find Peak Element
    // A peak is an element greater than its neighbours.
    // Time: O(log n)  Space: O(1)
    // ────────────────────────────────────────────────────────────

    public static int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) right = mid;  // peak is in left half
            else left = mid + 1;                          // peak is in right half
        }
        return left; // index of peak element
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        int[] sorted = {-1, 0, 3, 5, 9, 12};
        System.out.println("Binary Search for 9: " + binarySearch(sorted, 9));  // 4
        System.out.println("Binary Search for 2: " + binarySearch(sorted, 2));  // -1

        int[] withDups = {5, 7, 7, 8, 8, 10};
        System.out.println("Range of 8: " + java.util.Arrays.toString(searchRange(withDups, 8))); // [3,4]
        System.out.println("Range of 6: " + java.util.Arrays.toString(searchRange(withDups, 6))); // [-1,-1]

        int[] rotated = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("Search rotated for 0: " + searchRotated(rotated, 0)); // 4
        System.out.println("Search rotated for 3: " + searchRotated(rotated, 3)); // -1

        int[] peakArr = {1, 2, 3, 1};
        System.out.println("Peak element index: " + findPeakElement(peakArr)); // 2
    }
}
