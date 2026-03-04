# Collections & Generics

## Key Concepts to Know

- The Java Collections Framework hierarchy
- When to use `List`, `Set`, `Map`, `Queue`, and their implementations
- Differences between `ArrayList` vs `LinkedList`, `HashMap` vs `TreeMap` vs `LinkedHashMap`
- Generics: bounded type parameters, wildcards (`? extends T`, `? super T`)
- `Comparable` vs `Comparator`

---

## Problem 1: Find the First Non-Repeating Character

### Problem Statement
Given a string, find the first character that does not repeat. Return the character, or `'\0'` if all characters repeat.

**Constraints:**
- The string contains only lowercase English letters.
- Length: 1 ≤ n ≤ 10⁵

### Examples
```
Input:  "leetcode"
Output: 'l'

Input:  "aabb"
Output: '\0'

Input:  "abcabc"
Output: '\0'
```

### Step-by-Step Explanation
1. We need to count occurrences of each character — use a `LinkedHashMap` to preserve insertion order.
2. Iterate the string and populate the frequency map.
3. Iterate the map in insertion order; return the first key whose value is 1.

### Java 17+ Solution
```java
package com.interview.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class FirstNonRepeatingChar {

    /**
     * Finds the first non-repeating character in the given string.
     *
     * @param s input string (lowercase letters only)
     * @return first non-repeating character, or '\0' if none exists
     */
    public static char firstNonRepeating(String s) {
        // LinkedHashMap preserves insertion order — essential for "first" requirement
        Map<Character, Integer> frequency = new LinkedHashMap<>();

        // First pass: count each character's frequency
        for (char c : s.toCharArray()) {
            frequency.merge(c, 1, Integer::sum);
        }

        // Second pass: return first character with frequency 1
        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return '\0'; // no non-repeating character found
    }

    public static void main(String[] args) {
        System.out.println(firstNonRepeating("leetcode"));  // 'l'
        System.out.println(firstNonRepeating("aabb"));      // '\0'
        System.out.println(firstNonRepeating("abcabc"));    // '\0'
        System.out.println(firstNonRepeating("z"));         // 'z'
    }
}
```

### Complexity Analysis
| | Complexity |
|---|---|
| **Time** | O(n) — two linear passes over the string |
| **Space** | O(k) — where k is the alphabet size (≤ 26 for lowercase letters) |

### Variants & Follow-ups

**1. Return the index instead of the character:**
Same approach; when iterating the original string a second time (instead of the map), return the index when frequency is 1.
```java
for (int i = 0; i < s.length(); i++) {
    if (frequency.get(s.charAt(i)) == 1) return i;
}
return -1;
```

**2. Stream-based solution (Java 8+):**
```java
return s.chars()
        .mapToObj(c -> (char) c)
        .filter(c -> s.indexOf(c) == s.lastIndexOf(c))
        .findFirst()
        .orElse('\0');
```
Note: This is O(n²) due to repeated `indexOf`/`lastIndexOf` calls — elegant but not optimal.

**3. Unicode support (not just ASCII):**
The `LinkedHashMap<Character, Integer>` approach already handles full Unicode.

### Common Pitfalls
- ❌ Using a plain `HashMap` — iteration order is not guaranteed; you may return the wrong character.
- ❌ Forgetting the case where no non-repeating character exists (return `'\0'` or `-1`).
- ❌ Not handling the empty string: add a guard `if (s == null || s.isEmpty()) return '\0';`.

---

## Problem 2: Group Anagrams

### Problem Statement
Given an array of strings, group all anagrams together. The order of groups and elements within groups does not matter.

**Constraints:**
- 1 ≤ strs.length ≤ 10⁴
- 0 ≤ strs[i].length ≤ 100
- Strings contain only lowercase English letters.

### Examples
```
Input:  ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]

Input:  [""]
Output: [[""]]

Input:  ["a"]
Output: [["a"]]
```

### Step-by-Step Explanation
1. Anagrams share the same sorted character sequence — use that as a map key.
2. For each string, sort its characters → key.
3. Group all strings with the same key in a `HashMap<String, List<String>>`.
4. Return all map values.

### Java 17+ Solution
```java
package com.interview.collections;

import java.util.*;

public class GroupAnagrams {

    /**
     * Groups the input strings by their anagram equivalence class.
     *
     * @param strs array of strings
     * @return list of anagram groups
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String word : strs) {
            // Sort characters to get canonical key; all anagrams share the same key
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            // Add word to its group, creating the list if absent
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(groups.values());
    }

    public static void main(String[] args) {
        String[] input = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = groupAnagrams(input);
        result.forEach(System.out::println);
    }
}
```

### Complexity Analysis
| | Complexity |
|---|---|
| **Time** | O(n · k log k) — n strings, each of max length k sorted |
| **Space** | O(n · k) — storing all characters in the map |

### Variants & Follow-ups

**1. Use character frequency array as key (O(k) instead of O(k log k)):**
```java
int[] freq = new int[26];
Arrays.fill(freq, 0);
for (char c : word.toCharArray()) freq[c - 'a']++;
String key = Arrays.toString(freq); // e.g., "[1,0,0,...,1,0]"
```

**2. Count groups, not return them:** `return groupAnagrams(strs).size();`

### Common Pitfalls
- ❌ Mutating the original string (sort a copy of `toCharArray()`).
- ❌ Using `computeIfAbsent` vs `getOrDefault` — `computeIfAbsent` is cleaner and avoids a null check.

---

## Core Java Interview Questions (Theory)

**Q: What is the difference between `ArrayList` and `LinkedList`?**
- `ArrayList`: backed by a dynamic array. O(1) random access, O(n) insertion/removal in the middle. Preferred when reads dominate.
- `LinkedList`: doubly-linked list. O(n) random access, O(1) insertion/removal at head/tail. Preferred when frequent adds/removes at ends are needed.

**Q: Why is `HashMap` not thread-safe? What are the alternatives?**
- `HashMap` is not synchronized; concurrent modifications can cause infinite loops (Java 7) or data corruption.
- Alternatives: `ConcurrentHashMap` (segment-level locking, best performance), `Collections.synchronizedMap()` (full lock), `Hashtable` (legacy, avoid).

**Q: When would you use `TreeMap` over `HashMap`?**
- When you need keys in sorted order or want efficient `floorKey`, `ceilingKey`, `subMap` range operations. `TreeMap` gives O(log n) operations vs O(1) average for `HashMap`.

**Q: What is the contract between `equals()` and `hashCode()`?**
- If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true.
- The reverse is not required (hash collisions are allowed).
- Violating this contract breaks `HashMap`, `HashSet`, etc.

**Q: Explain bounded wildcards: `? extends T` vs `? super T`.**
- `? extends T` (upper bounded): read-only producer. Use when you read from a collection: `List<? extends Number>`.
- `? super T` (lower bounded): write-capable consumer. Use when you add to a collection: `List<? super Integer>`.
- Mnemonic: **PECS** — Producer Extends, Consumer Super.
