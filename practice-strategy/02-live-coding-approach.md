# Live Coding Interview Approach

## The Problem-Solving Framework (5 Steps)

### Step 1: UNDERSTAND (2–3 min)
Don't start coding immediately. Spend time understanding the problem.

**Questions to ask:**
- "Can the input be null or empty? How should I handle that?"
- "Are there duplicates in the input?"
- "What is the expected input size / scale?"
- "Should I optimize for time or space, or is there a specific constraint?"
- "Can I modify the input array in place, or should I keep it immutable?"

**Restate the problem:**
> *"So if I understand correctly, I need to find the two numbers in the array that sum to the target and return their indices. There's guaranteed to be exactly one solution. Is that right?"*

---

### Step 2: PLAN (3–5 min)
Discuss your approach BEFORE writing code.

**Think out loud:**
> *"My first instinct is a brute force: for every pair (i, j), check if they sum to target. That's O(n²). Can we do better?"*

> *"A better approach: use a HashMap. As I iterate, I store each element. For each number, I check if `target - num` is already in the map. That gives O(n) time with O(n) space."*

**Draw on paper/whiteboard:**
- Example walkthrough with a small input
- The data structure you'll use

**Ask if unsure:**
> *"Before I start, would you prefer I implement the O(n²) brute force first and then optimize, or should I go straight to the optimal solution?"*

---

### Step 3: CODE (10–15 min)
Write clean, readable code. Think aloud as you code.

**Good habits:**
- Start with the method signature and return type
- Add a brief comment for the algorithm idea
- Use meaningful variable names (`complement`, `maxLength` vs `x`, `y`)
- Handle edge cases as you encounter them, not as an afterthought
- Don't silently fix mistakes — say "oh wait, I should handle the case where..."

**What to say while coding:**
> *"I'll use a HashMap here... OK, I'm iterating through the array... for each element, I first check if the complement exists in the map — this handles the case where we've already seen the matching number... then I add the current element."*

---

### Step 4: TEST (3–5 min)
Walk through your code with test cases.

1. **Happy path**: the example from the problem
2. **Edge cases**: empty input, single element, all same elements, negative numbers
3. **Trace through**: `nums = [2, 7, 11, 15], target = 9`
   - i=0: complement=7, not in map, add {2:0}
   - i=1: complement=2, found at index 0, return [0,1] ✓

**Say what you're doing:**
> *"Let me trace through this example... at index 0, the complement is 7, the map is empty so I add 2 → 0... at index 1, the complement is 2, and 2 is in the map at index 0, so I return [0, 1]. That matches the expected output."*

---

### Step 5: REFLECT (2 min)
Discuss complexity and improvements.

> *"The time complexity is O(n) because we do a single pass through the array and each HashMap operation is O(1) average. The space complexity is O(n) for the map.*

> *One potential issue: if there are duplicate values in the array, we'd overwrite the earlier index in the map. But the problem guarantees exactly one solution, so this case won't arise here.*

> *If we needed O(1) space, we could sort the array and use two pointers, but that's O(n log n) time and would destroy the original indices — so not applicable here."*

---

## Common Mistakes to Avoid

| Mistake | Better Approach |
|---------|----------------|
| Jumping straight into code | Spend 2-3 min understanding |
| Coding silently | Narrate your thought process |
| Writing the perfect solution first | Write a correct solution first, then optimize |
| Ignoring edge cases | Mention them even if you don't code them all |
| Getting stuck silently | Say "I'm thinking through this" or ask for a hint |
| Forgetting to test | Always trace through at least one example |

---

## If You Get Stuck

1. **Restate the problem** — sometimes clarity comes from rephrasing.
2. **Try a simpler version** — "What if n was just 1? 2? 3?"
3. **Think about related problems** — "This looks like a variant of two sum..."
4. **Ask for a hint** — it's better than sitting in silence:
   > *"I'm not immediately seeing the optimal approach. Could you give me a direction hint — should I be thinking about sorting, or some kind of hash structure?"*

---

## Pacing Guide (45-min Coding Session)

| Time | Activity |
|------|----------|
| 0–3 min | Read problem, ask clarifying questions |
| 3–7 min | Discuss approach, draw example |
| 7–22 min | Write code (think aloud) |
| 22–30 min | Test with examples, debug |
| 30–35 min | Analyze complexity |
| 35–45 min | Discuss follow-ups / optimizations |
