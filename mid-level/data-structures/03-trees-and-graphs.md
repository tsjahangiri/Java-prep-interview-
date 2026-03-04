# Trees & Graphs

## Key Concepts
- Binary tree traversals: inorder, preorder, postorder, level-order (BFS)
- Binary Search Tree (BST) properties
- DFS vs BFS — when to use each
- Graph representations: adjacency list, adjacency matrix
- Directed vs undirected, weighted vs unweighted

---

## Problem 1: Binary Tree Level-Order Traversal (BFS)

### Problem Statement
Given the root of a binary tree, return the values level by level (left to right). Each level should be a separate list.

### Examples
```
Input tree:
        3
       / \
      9  20
        /  \
       15   7

Output: [[3], [9, 20], [15, 7]]
```

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class BinaryTreeBFS {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Level-order traversal using a queue.
     * Time: O(n)  Space: O(n) — at most n/2 nodes in queue at leaf level
     */
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // number of nodes at current level
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null)  queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(level);
        }

        return result;
    }
}
```

---

## Problem 2: Validate Binary Search Tree

### Problem Statement
Determine if a binary tree is a valid BST. For each node, all values in the left subtree must be strictly less than the node's value, and all values in the right subtree must be strictly greater.

### Java 17+ Solution
```java
package com.interview.algorithms;

public class ValidateBST {

    /**
     * Passes min/max bounds through recursion.
     * Time: O(n)  Space: O(h) — h = height of tree
     */
    public static boolean isValidBST(BinaryTreeBFS.TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private static boolean validate(BinaryTreeBFS.TreeNode node, long min, long max) {
        if (node == null) return true;

        // Value must be strictly within (min, max) bounds
        if (node.val <= min || node.val >= max) return false;

        // Left subtree: upper bound becomes current node value
        // Right subtree: lower bound becomes current node value
        return validate(node.left, min, node.val)
            && validate(node.right, node.val, max);
    }
}
```

### Common Pitfall
❌ Only checking `node.val > node.left.val && node.val < node.right.val` — this fails for cases where a node in a deeper level violates the global BST property (e.g., a value in the right subtree that is less than the root).

---

## Problem 3: Number of Islands (Graph DFS)

### Problem Statement
Given an `m × n` grid of `'1'` (land) and `'0'` (water), count the number of islands. An island is surrounded by water and formed by connecting adjacent land cells (horizontally or vertically).

### Examples
```
Input:
  [["1","1","0","0","0"],
   ["1","1","0","0","0"],
   ["0","0","1","0","0"],
   ["0","0","0","1","1"]]

Output: 3
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class NumberOfIslands {

    /**
     * DFS flood-fill: mark visited cells as '0'.
     * Time: O(m * n)  Space: O(m * n) — call stack in worst case
     */
    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c); // sink the entire island
                }
            }
        }
        return count;
    }

    private static void dfs(char[][] grid, int r, int c) {
        // Boundary check and water/visited check
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
                || grid[r][c] != '1') {
            return;
        }

        grid[r][c] = '0'; // mark as visited (sink the cell)

        // Explore all 4 directions
        dfs(grid, r + 1, c);
        dfs(grid, r - 1, c);
        dfs(grid, r, c + 1);
        dfs(grid, r, c - 1);
    }

    public static void main(String[] args) {
        char[][] grid = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println(numIslands(grid)); // 3
    }
}
```

### Variant: BFS version (avoids deep recursion stack)
```java
private static void bfs(char[][] grid, int r, int c) {
    Queue<int[]> queue = new LinkedList<>();
    queue.offer(new int[]{r, c});
    grid[r][c] = '0';

    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    while (!queue.isEmpty()) {
        int[] pos = queue.poll();
        for (int[] d : dirs) {
            int nr = pos[0] + d[0], nc = pos[1] + d[1];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length
                    && grid[nr][nc] == '1') {
                grid[nr][nc] = '0';
                queue.offer(new int[]{nr, nc});
            }
        }
    }
}
```

### DFS vs BFS — When to Use
| Use DFS when... | Use BFS when... |
|---|---|
| Finding *any* path | Finding the *shortest* path |
| Cycle detection | Level-by-level processing |
| Topological sort | Spreading outward (e.g., distance, infection) |
| Exhaustive search (backtracking) | Minimum steps / moves |
