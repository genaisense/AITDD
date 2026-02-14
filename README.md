TDD AI Framework - AI-assisted Test-Driven Development (AITDD )

Specification First, Test First, and Quality First.**

## **Problem**

Vibe coding is too risky for corporate development:

- **Not implemented as expected** — Frequent rework due to ambiguous specifications
- **Works, but seems hard to maintain** — Accumulation of technical debt due to lack of testing
- **Fine for simple demos, but risky for production** — Lack of quality assurance processes

## **Solution**

Invert the vibe code approach (where AI just writes code) by having the AI write tests first, creating a feedback loop that leads to more reliable and self-correcting systems.

## How it works

**Architecture:**

```
Claude (AI model) → Claude Code (middleware) → TDD Agent framework → Individual Applications
```

**TDD Framework consists of:**

- **Agent 0:** Propose initial specification updates
- **Agent 1:** Propose software architecture
- **Agent 2:** Generate tests
- **Agent 3:** Generate implementation for tests (app features)
- **Run and check if test passes**

AITDD combines traditional Test-Driven Development (TDD) with AI technology to enhance development efficiency.

### **Basic Development Cycle:**

Requirements Expansion → Design → Task Breakdown → TDD Implementation  
(Requirements Analysis → Write Test Case → Red → Green → Refactor → Verify)

### **Features of the Extended TDD Cycle:**

- **Red (Write Test):** Define the "expected behavior" as a test first
- **Green (Implementation):** AI generates the minimal code needed to pass the test
- **Refactor (Improvement):** AI optimizes the code for readability and efficiency
- **Verify (Validation):** AI confirms that the implementation meets the requirements

### **Differences from Conventional AI-Driven Development:**

TDD Framework ensures quality not just by generating code with AI, but also through:

- **Automatic Specification Generation and Management:** Clarifies requirements and ensures consistency with implementation
- **Automated Code Review:** Objective quality evaluation by AI


### Self-Correction Flow

1. **Tests are generated first** - AI creates unittest tests from feature description
2. **Code is generated** - AI writes an implementation to pass those tests
3. **Tests are executed** - the code + tests run on Judge0 (sandboxed Python)
4. **If tests fail, the agent automatically:**
    - 4.1. Captures the full error output (which tests failed, tracebacks, assertion errors)
    - 4.2. Sends the failing code + error output + tests back to the AI with a fix-code prompt
    - 4.3. The AI analyzes what went wrong and generates a corrected implementation
    - 4.4. Repeats up to 5 times until all tests pass or max retries are reached

### Difficult task examples:

1. **Easy:** Check that customer discounts are applied only on Fridays between 2:00 PM and 4:00 PM.
	1. **With specification:** Check that customer discounts are applied only on Fridays between 2:00 PM and 4:00 PM - function `isDiscountActive()` should return `true` only between 2:00 PM and 4:00 PM on Fridays.
	2. A GUI application where clicking a button starts a background thread that fetches data from a network, updates an internal cache, and then updates a UI label.
	3. A function that generates a daily report, but only if run between 11:55 PM and midnight.
