# Sorting Algorithms

## Algorithm Comparison

| Algorithm | Best | Average | Worst | Space | Stable? |
|---|---|---|---|---|---|
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | No |
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) | No |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |
| Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Yes |

**Java's `Arrays.sort()`** uses:
- Dual-Pivot Quicksort for primitives (fast in practice)
- TimSort (Merge + Insertion) for objects (stable, O(n log n))

---

## Problem: Merge Sort Implementation

### Problem Statement
Implement merge sort on an integer array. Explain why merge sort is preferred over quicksort when stability is required.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.Arrays;

public class MergeSort {

    /**
     * Merge sort — divide and conquer.
     * Time: O(n log n)  Space: O(n) for auxiliary array
     */
    public static void mergeSort(int[] arr) {
        if (arr.length <= 1) return;
        int[] temp = new int[arr.length];
        sort(arr, temp, 0, arr.length - 1);
    }

    private static void sort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) return; // base case: single element

        int mid = left + (right - left) / 2; // avoid overflow vs (left+right)/2
        sort(arr, temp, left, mid);           // sort left half
        sort(arr, temp, mid + 1, right);      // sort right half
        merge(arr, temp, left, mid, right);   // merge two sorted halves
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        // Copy both halves into temp
        System.arraycopy(arr, left, temp, left, right - left + 1);

        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {
            if      (i > mid)            arr[k] = temp[j++]; // left half exhausted
            else if (j > right)          arr[k] = temp[i++]; // right half exhausted
            else if (temp[i] <= temp[j]) arr[k] = temp[i++]; // stable: left wins ties
            else                         arr[k] = temp[j++];
        }
    }

    public static void main(String[] args) {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        mergeSort(arr);
        System.out.println(Arrays.toString(arr)); // [11, 12, 22, 25, 34, 64, 90]
    }
}
```

---

## Problem: Quick Sort with Median-of-Three Pivot

### Java 17+ Solution
```java
package com.interview.algorithms;

public class QuickSort {

    public static void quickSort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    private static void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sort(arr, low, pivotIndex - 1);
            sort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        // Median-of-three pivot selection to reduce worst-case probability
        int mid = low + (high - low) / 2;
        if (arr[mid] < arr[low])  swap(arr, mid, low);
        if (arr[high] < arr[low]) swap(arr, high, low);
        if (arr[mid] < arr[high]) swap(arr, mid, high);
        // arr[high] is now the median; use as pivot
        int pivot = arr[high];

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                swap(arr, ++i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }
}
```

---

## Interview Theory Questions

**Q: Why is merge sort preferred for linked lists?**
Linked lists don't support random access, making the in-place swapping of quicksort expensive. Merge sort naturally splits lists at the midpoint (using slow/fast pointer) and merges them without extra space.

**Q: When would you use counting sort over comparison-based sorts?**
When values are integers in a known, small range [0, k]. Counting sort is O(n + k) — faster than O(n log n) when k is small. Used for sorting strings by character, radix sort subprocedure, etc.

**Q: What makes TimSort efficient in practice?**
TimSort detects natural "runs" (already-sorted sequences) in the data and merges them. Real-world data is often partially sorted, which means TimSort's O(n) best case is frequently achieved.
