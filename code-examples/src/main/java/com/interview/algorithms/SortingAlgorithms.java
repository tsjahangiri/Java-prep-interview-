package com.interview.algorithms;

import java.util.Arrays;

/**
 * Implementations of common sorting algorithms for interview practice.
 *
 * All algorithms sort int[] in ascending order.
 * Each method is self-contained and can be run independently.
 *
 * Usage: compile with javac --release 17 SortingAlgorithms.java
 *        run with java com.interview.algorithms.SortingAlgorithms
 */
public class SortingAlgorithms {

    // ────────────────────────────────────────────────────────────
    // Merge Sort
    // Time: O(n log n) | Space: O(n) | Stable: Yes
    // ────────────────────────────────────────────────────────────

    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        // Copy both halves into temp
        System.arraycopy(arr, left, temp, left, right - left + 1);
        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {
            if      (i > mid)              arr[k] = temp[j++]; // left half exhausted
            else if (j > right)            arr[k] = temp[i++]; // right half exhausted
            else if (temp[i] <= temp[j])   arr[k] = temp[i++]; // stable: left wins ties
            else                           arr[k] = temp[j++];
        }
    }

    // ────────────────────────────────────────────────────────────
    // Quick Sort (median-of-three pivot)
    // Time: O(n log n) avg, O(n²) worst | Space: O(log n) | Stable: No
    // ────────────────────────────────────────────────────────────

    public static void quickSort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        // Median-of-three: reduces worst-case probability on sorted inputs
        int mid = low + (high - low) / 2;
        if (arr[mid] < arr[low])  swap(arr, mid, low);
        if (arr[high] < arr[low]) swap(arr, high, low);
        if (arr[mid] < arr[high]) swap(arr, mid, high);
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) swap(arr, ++i, j);
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // ────────────────────────────────────────────────────────────
    // Counting Sort (for integers in a known range)
    // Time: O(n + k) | Space: O(k) | Stable: Yes
    // ────────────────────────────────────────────────────────────

    public static void countingSort(int[] arr, int maxValue) {
        if (arr == null || arr.length <= 1) return;
        int[] count = new int[maxValue + 1];
        for (int val : arr) count[val]++;
        int idx = 0;
        for (int v = 0; v <= maxValue; v++) {
            while (count[v]-- > 0) arr[idx++] = v;
        }
    }

    // ────────────────────────────────────────────────────────────
    // Heap Sort
    // Time: O(n log n) | Space: O(1) | Stable: No
    // ────────────────────────────────────────────────────────────

    public static void heapSort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        int n = arr.length;
        // Build max-heap: heapify from last non-leaf to root
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
        // Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);   // move current root (max) to end
            heapify(arr, i, 0); // restore heap property for reduced heap
        }
    }

    private static void heapify(int[] arr, int n, int root) {
        int largest = root, left = 2 * root + 1, right = 2 * root + 2;
        if (left  < n && arr[left]  > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;
        if (largest != root) {
            swap(arr, root, largest);
            heapify(arr, n, largest);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        int[] original = {64, 34, 25, 12, 22, 11, 90, 3, 55, 42};

        int[] a1 = original.clone(); mergeSort(a1);
        System.out.println("Merge Sort:    " + Arrays.toString(a1));

        int[] a2 = original.clone(); quickSort(a2);
        System.out.println("Quick Sort:    " + Arrays.toString(a2));

        int[] a3 = original.clone(); countingSort(a3, 100);
        System.out.println("Counting Sort: " + Arrays.toString(a3));

        int[] a4 = original.clone(); heapSort(a4);
        System.out.println("Heap Sort:     " + Arrays.toString(a4));
    }
}
