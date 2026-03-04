# Complex Data Structures

---

## Trie (Prefix Tree)

### Use Cases
- Autocomplete / search suggestions
- Spell checking
- IP routing
- Word puzzles

### Java 17+ Implementation
```java
package com.interview.algorithms;

import java.util.*;

public class Trie {

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    private final TrieNode root = new TrieNode();

    /** Insert a word into the trie. Time: O(m) where m = word length */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
    }

    /** Returns true if the exact word exists in the trie. Time: O(m) */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /** Returns true if any word in the trie starts with the prefix. Time: O(m) */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /** Returns all words with the given prefix. Time: O(prefix + output) */
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) dfs(node, new StringBuilder(prefix), results);
        return results;
    }

    private TrieNode findNode(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            if (!node.children.containsKey(c)) return null;
            node = node.children.get(c);
        }
        return node;
    }

    private void dfs(TrieNode node, StringBuilder current, List<String> results) {
        if (node.isEndOfWord) results.add(current.toString());
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            current.append(entry.getKey());
            dfs(entry.getValue(), current, results);
            current.deleteCharAt(current.length() - 1); // backtrack
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple"); trie.insert("app"); trie.insert("application");
        trie.insert("apply"); trie.insert("banana");

        System.out.println(trie.search("app"));           // true
        System.out.println(trie.startsWith("appl"));      // true
        System.out.println(trie.autocomplete("app"));
        // [app, apple, application, apply]
    }
}
```

---

## LRU Cache

### Problem Statement
Design a data structure that follows the Least Recently Used (LRU) cache eviction policy. It should support `get(key)` and `put(key, value)` in O(1) time.

### Java 17+ Solution
```java
package com.interview.algorithms;

import java.util.*;

public class LRUCache {

    private final int capacity;
    // LinkedHashMap with access-order maintains LRU order automatically
    private final LinkedHashMap<Integer, Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        // accessOrder=true: iteration order = LRU to MRU
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity; // evict when over capacity
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }

    // Manual implementation without LinkedHashMap (for deeper understanding)
    public static class LRUCacheManual {
        private final int capacity;
        private final Map<Integer, Node> map;
        private final Node head; // dummy head (most recent end)
        private final Node tail; // dummy tail (LRU end)

        private static class Node {
            int key, val;
            Node prev, next;
            Node(int key, int val) { this.key = key; this.val = val; }
        }

        public LRUCacheManual(int capacity) {
            this.capacity = capacity;
            this.map = new HashMap<>();
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            if (!map.containsKey(key)) return -1;
            Node node = map.get(key);
            moveToFront(node); // mark as recently used
            return node.val;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                Node node = map.get(key);
                node.val = value;
                moveToFront(node);
            } else {
                if (map.size() == capacity) {
                    Node lru = tail.prev; // least recently used
                    remove(lru);
                    map.remove(lru.key);
                }
                Node node = new Node(key, value);
                insertAtFront(node);
                map.put(key, node);
            }
        }

        private void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void insertAtFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        private void moveToFront(Node node) {
            remove(node);
            insertAtFront(node);
        }
    }
}
```

---

## Segment Tree (Range Sum / Range Min)

```java
package com.interview.algorithms;

public class SegmentTree {

    private final int[] tree;
    private final int n;

    public SegmentTree(int[] nums) {
        this.n = nums.length;
        this.tree = new int[4 * n];
        build(nums, 0, 0, n - 1);
    }

    private void build(int[] nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = (start + end) / 2;
            build(nums, 2*node+1, start, mid);
            build(nums, 2*node+2, mid+1, end);
            tree[node] = tree[2*node+1] + tree[2*node+2]; // sum variant
        }
    }

    /** Range sum query [l, r]. Time: O(log n) */
    public int query(int l, int r) { return query(0, 0, n-1, l, r); }

    private int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return 0;        // out of range
        if (l <= start && end <= r) return tree[node]; // fully in range
        int mid = (start + end) / 2;
        return query(2*node+1, start, mid, l, r)
             + query(2*node+2, mid+1, end, l, r);
    }

    /** Point update. Time: O(log n) */
    public void update(int idx, int val) { update(0, 0, n-1, idx, val); }

    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = (start + end) / 2;
            if (idx <= mid) update(2*node+1, start, mid, idx, val);
            else            update(2*node+2, mid+1, end, idx, val);
            tree[node] = tree[2*node+1] + tree[2*node+2];
        }
    }
}
```
