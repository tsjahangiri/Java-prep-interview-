# Recursion

## Key Concepts
- Base case and recursive case
- Call stack depth — O(n) or O(log n) stack space
- Tail recursion (not optimized by JVM, but conceptually relevant)
- Backtracking — exploring all possibilities

---

## Problem 1: Generate All Subsets

### Problem Statement
Given an integer array with distinct elements, return all possible subsets (the power set).

### Examples
```
Input:  [1, 2, 3]
Output: [[], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]]
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class Subsets {

    /**
     * Backtracking approach.
     * Time: O(2^n * n)  Space: O(n) for recursion stack
     */
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, int start,
                                   List<Integer> current,
                                   List<List<Integer>> result) {
        // Add current subset to results (including empty subset at start)
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);          // choose
            backtrack(nums, i + 1, current, result); // explore
            current.remove(current.size() - 1);      // un-choose
        }
    }

    public static void main(String[] args) {
        System.out.println(subsets(new int[]{1, 2, 3}));
        // [[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]
    }
}
```

---

## Problem 2: Permutations

### Problem Statement
Given an array of distinct integers, return all possible permutations.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class Permutations {

    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, boolean[] used,
                                   List<Integer> current,
                                   List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue; // skip already chosen elements
            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
```

---

## Problem 3: N-Queens (Classic Backtracking)

### Problem Statement
Place `n` queens on an `n×n` chessboard such that no two queens attack each other. Return all valid board configurations.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class NQueens {

    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        backtrack(board, 0, result);
        return result;
    }

    private static void backtrack(char[][] board, int row,
                                   List<List<String>> result) {
        if (row == board.length) {
            result.add(boardToList(board));
            return;
        }

        for (int col = 0; col < board.length; col++) {
            if (isValid(board, row, col)) {
                board[row][col] = 'Q';
                backtrack(board, row + 1, result);
                board[row][col] = '.'; // backtrack
            }
        }
    }

    private static boolean isValid(char[][] board, int row, int col) {
        int n = board.length;
        // Check column
        for (int i = 0; i < row; i++)
            if (board[i][col] == 'Q') return false;
        // Check upper-left diagonal
        for (int i = row-1, j = col-1; i >= 0 && j >= 0; i--, j--)
            if (board[i][j] == 'Q') return false;
        // Check upper-right diagonal
        for (int i = row-1, j = col+1; i >= 0 && j < n; i--, j++)
            if (board[i][j] == 'Q') return false;
        return true;
    }

    private static List<String> boardToList(char[][] board) {
        List<String> list = new ArrayList<>();
        for (char[] row : board) list.add(new String(row));
        return list;
    }
}
```

## Backtracking Template
```java
void backtrack(state, choices) {
    if (isGoal(state)) {
        addToResult(state);
        return;
    }
    for (choice : choices) {
        if (isValid(choice)) {
            makeChoice(choice);         // modify state
            backtrack(state, choices);  // recurse
            undoChoice(choice);         // restore state
        }
    }
}
```
