# Graph Algorithms

---

## Problem 1: Dijkstra's Shortest Path

### Problem Statement
Given a weighted directed graph, find the shortest path from a source node to all other nodes. All edge weights are non-negative.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class Dijkstra {

    /**
     * Dijkstra's algorithm using a min-heap (priority queue).
     * Time: O((V + E) log V)  Space: O(V + E)
     *
     * @param graph adjacency list: graph.get(u) = list of [v, weight]
     * @param src   source node
     * @param n     number of nodes (0-indexed)
     * @return shortest distances from src to all nodes
     */
    public static int[] shortestPath(List<List<int[]>> graph, int src, int n) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Min-heap: [distance, node]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], u = curr[1];

            // Skip if we've already found a better path (stale entry)
            if (d > dist[u]) continue;

            for (int[] edge : graph.get(u)) {
                int v = edge[0], weight = edge[1];
                int newDist = dist[u] + weight;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        int n = 5;
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        // Add edges: [from, to, weight]
        int[][] edges = {{0,1,10},{0,2,3},{1,3,2},{2,1,4},{2,3,8},{2,4,2},{3,4,5}};
        for (int[] e : edges) graph.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dist = shortestPath(graph, 0, n);
        System.out.println("Shortest distances from node 0:");
        for (int i = 0; i < n; i++) System.out.printf("  Node %d: %d%n", i, dist[i]);
        // 0→0:0, 0→1:7, 0→2:3, 0→3:9, 0→4:5
    }
}
```

---

## Problem 2: Topological Sort (Course Schedule)

### Problem Statement
Given `n` courses (0 to n-1) and prerequisites `[a, b]` (must take b before a), determine if you can finish all courses (i.e., the graph has no cycle).

### Java 17+ Solution (Kahn's Algorithm — BFS)
```java
package com.interview.algorithms;

import java.util.*;

public class CourseSchedule {

    /**
     * Kahn's algorithm: BFS-based topological sort.
     * Time: O(V + E)  Space: O(V + E)
     */
    public static boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]); // b → a
            inDegree[pre[0]]++;
        }

        // Start with all courses that have no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int completed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            completed++;
            for (int next : adj.get(course)) {
                if (--inDegree[next] == 0) queue.offer(next);
            }
        }

        return completed == numCourses; // all courses completed → no cycle
    }

    public static void main(String[] args) {
        System.out.println(canFinish(2, new int[][]{{1,0}}));        // true
        System.out.println(canFinish(2, new int[][]{{1,0},{0,1}}));  // false (cycle)
    }
}
```

---

## Problem 3: Word Ladder (BFS Shortest Path in Implicit Graph)

### Problem Statement
Given `beginWord`, `endWord`, and a `wordList`, find the length of the shortest transformation sequence where each step changes exactly one letter and the intermediate word must be in the list.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class WordLadder {

    /**
     * BFS on implicit graph where edges connect words differing by one letter.
     * Time: O(M^2 * N)  where M = word length, N = wordList size
     */
    public static int ladderLength(String beginWord, String endWord,
                                    List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        int steps = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[j] = c;
                        String next = new String(chars);
                        if (next.equals(endWord)) return steps + 1;
                        if (wordSet.contains(next) && !visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                    chars[j] = original; // restore
                }
            }
            steps++;
        }

        return 0; // no path found
    }
}
```

---

## Problem 4: Union-Find (Disjoint Set Union)

```java
package com.interview.algorithms;

public class UnionFind {

    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    // Path compression
    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    // Union by rank
    public boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false; // already connected
        if (rank[px] < rank[py]) { int tmp = px; px = py; py = tmp; }
        parent[py] = px;
        if (rank[px] == rank[py]) rank[px]++;
        components--;
        return true;
    }

    public boolean connected(int x, int y) { return find(x) == find(y); }
    public int componentCount() { return components; }
}
```

**Use cases**: number of connected components, minimum spanning tree (Kruskal's), cycle detection in undirected graphs, social network groupings.
