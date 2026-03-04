package com.interview.collections;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates common Collections API patterns used in interviews.
 *
 * Topics:
 * - FrequencyMap with merge()
 * - Group anagrams with computeIfAbsent()
 * - Stream collectors: groupingBy, maxBy, averagingDouble
 * - LinkedHashMap for insertion-order iteration
 * - TreeMap for sorted key iteration
 */
public class CollectionsDemo {

    // ────────────────────────────────────────────────────────────
    // Frequency Map
    // ────────────────────────────────────────────────────────────

    public static Map<Character, Integer> charFrequency(String s) {
        Map<Character, Integer> freq = new LinkedHashMap<>();
        for (char c : s.toCharArray()) {
            freq.merge(c, 1, Integer::sum); // merge: if absent put 1, else add 1
        }
        return freq;
    }

    public static char firstNonRepeating(String s) {
        Map<Character, Integer> freq = charFrequency(s);
        return freq.entrySet().stream()
            .filter(e -> e.getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse('\0');
    }

    // ────────────────────────────────────────────────────────────
    // Anagram Grouping
    // ────────────────────────────────────────────────────────────

    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : strs) {
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            groups.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }

    // ────────────────────────────────────────────────────────────
    // Stream Collectors
    // ────────────────────────────────────────────────────────────

    record Employee(String name, String department, double salary) {}

    public static void streamDemo() {
        List<Employee> employees = List.of(
            new Employee("Alice",  "Engineering", 90_000),
            new Employee("Bob",    "Engineering", 85_000),
            new Employee("Carol",  "Marketing",   70_000),
            new Employee("Dave",   "Marketing",   75_000),
            new Employee("Eve",    "Engineering", 95_000),
            new Employee("Frank",  "HR",          60_000)
        );

        // Average salary per department
        Map<String, Double> avgByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.averagingDouble(Employee::salary)
            ));
        System.out.println("\nAverage salary by department:");
        new TreeMap<>(avgByDept).forEach((dept, avg) ->
            System.out.printf("  %-15s %.0f%n", dept, avg));

        // Top earner per department
        Map<String, Optional<Employee>> topEarner = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.maxBy(Comparator.comparingDouble(Employee::salary))
            ));
        System.out.println("\nTop earner per department:");
        new TreeMap<>(topEarner).forEach((dept, emp) ->
            emp.ifPresent(e -> System.out.printf("  %-15s %s (%.0f)%n",
                dept, e.name(), e.salary())));

        // Employees above company average
        double companyAvg = employees.stream()
            .mapToDouble(Employee::salary).average().orElse(0);
        List<String> aboveAvg = employees.stream()
            .filter(e -> e.salary() > companyAvg)
            .map(Employee::name).sorted()
            .collect(Collectors.toList());
        System.out.printf("\nCompany average: %.0f%n", companyAvg);
        System.out.println("Above average: " + aboveAvg);
    }

    // ────────────────────────────────────────────────────────────
    // Sorted Map Operations
    // ────────────────────────────────────────────────────────────

    public static void treeMapDemo() {
        TreeMap<String, Integer> scores = new TreeMap<>();
        scores.put("Charlie", 85);
        scores.put("Alice", 92);
        scores.put("Bob", 78);
        scores.put("Dave", 95);

        System.out.println("\nTreeMap (sorted): " + scores);
        System.out.println("First: " + scores.firstKey());
        System.out.println("Last: " + scores.lastKey());
        System.out.println("Range [Bob, Dave]: " + scores.subMap("Bob", true, "Dave", true));
    }

    // ────────────────────────────────────────────────────────────
    // Demo
    // ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== First Non-Repeating Character ===");
        System.out.println("leetcode → '" + firstNonRepeating("leetcode") + "'"); // 'l'
        System.out.println("aabb     → '" + firstNonRepeating("aabb") + "'");     // '\0'

        System.out.println("\n=== Group Anagrams ===");
        String[] words = {"eat", "tea", "tan", "ate", "nat", "bat"};
        groupAnagrams(words).forEach(System.out::println);

        System.out.println("\n=== Stream Collectors Demo ===");
        streamDemo();

        System.out.println("\n=== TreeMap Demo ===");
        treeMapDemo();
    }
}
