# Linked Lists

## Key Concepts
- Singly vs doubly linked lists
- Fast & slow pointer (Floyd's cycle detection)
- Dummy head node pattern
- In-place reversal
- Merge sorted lists

---

## Problem 1: Reverse a Linked List

### Problem Statement
Reverse a singly linked list and return the new head.

### Examples
```
Input:  1 → 2 → 3 → 4 → 5
Output: 5 → 4 → 3 → 2 → 1
```

### Java 17+ Solution
```java
package com.interview.algorithms;

public class ReverseLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Iterative reversal — O(n) time, O(1) space.
     */
    public static ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode nextTemp = current.next; // save next
            current.next = prev;             // reverse pointer
            prev = current;                  // advance prev
            current = nextTemp;              // advance current
        }

        return prev; // new head
    }

    /**
     * Recursive reversal — O(n) time, O(n) space (call stack).
     */
    public static ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode newHead = reverseRecursive(head.next);
        head.next.next = head; // reverse the link
        head.next = null;      // break old link
        return newHead;
    }

    // Helper: build list from array
    public static ListNode buildList(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int v : vals) { curr.next = new ListNode(v); curr = curr.next; }
        return dummy.next;
    }

    // Helper: print list
    public static String listToString(ListNode head) {
        StringBuilder sb = new StringBuilder();
        for (ListNode n = head; n != null; n = n.next) {
            if (sb.length() > 0) sb.append(" → ");
            sb.append(n.val);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ListNode list = buildList(1, 2, 3, 4, 5);
        System.out.println(listToString(reverse(list))); // 5 → 4 → 3 → 2 → 1
    }
}
```

### Complexity Analysis
| Approach | Time | Space |
|---|---|---|
| Iterative | O(n) | O(1) |
| Recursive | O(n) | O(n) |

**Prefer iterative** in interviews — it demonstrates space awareness and avoids stack overflow for large lists.

---

## Problem 2: Detect Cycle in Linked List

### Problem Statement
Given the head of a linked list, determine if it contains a cycle. A cycle exists if some node can be reached by continuously following `next` pointers.

### Step-by-Step Explanation (Floyd's Algorithm)
1. Use two pointers: `slow` (moves 1 step) and `fast` (moves 2 steps).
2. If there is a cycle, `fast` will eventually lap `slow` and they will meet.
3. If `fast` reaches `null`, there is no cycle.

### Java 17+ Solution
```java
package com.interview.algorithms;

public class DetectCycle {

    public static boolean hasCycle(ReverseLinkedList.ListNode head) {
        ReverseLinkedList.ListNode slow = head;
        ReverseLinkedList.ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;       // 1 step
            fast = fast.next.next;  // 2 steps
            if (slow == fast) return true; // cycle detected
        }

        return false; // fast reached end → no cycle
    }
}
```

### Variant: Find the Start of the Cycle
After the meeting point is found:
1. Move one pointer to `head`, keep the other at the meeting point.
2. Advance both one step at a time — they meet at the cycle start.

```java
public static ReverseLinkedList.ListNode cycleStart(ReverseLinkedList.ListNode head) {
    ReverseLinkedList.ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            // Found meeting point; reset one pointer to head
            slow = head;
            while (slow != fast) {
                slow = slow.next;
                fast = fast.next;
            }
            return slow; // cycle start
        }
    }
    return null;
}
```

---

## Problem 3: Merge Two Sorted Lists

### Problem Statement
Merge two sorted linked lists and return the merged list (also sorted).

### Java 17+ Solution
```java
package com.interview.algorithms;

public class MergeSortedLists {

    public static ReverseLinkedList.ListNode mergeTwoLists(
            ReverseLinkedList.ListNode l1,
            ReverseLinkedList.ListNode l2) {

        // Dummy node avoids special-casing the head
        ReverseLinkedList.ListNode dummy = new ReverseLinkedList.ListNode(0);
        ReverseLinkedList.ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes (at most one list has remaining elements)
        current.next = (l1 != null) ? l1 : l2;

        return dummy.next;
    }
}
```

### Complexity Analysis
| | Complexity |
|--|--|
| **Time** | O(n + m) |
| **Space** | O(1) — in-place linking |

### Common Pitfalls
- ❌ Not using a dummy head — leads to verbose null-checks for the first node.
- ❌ Forgetting to attach the remaining list after the loop.
