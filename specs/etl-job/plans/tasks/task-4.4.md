# Task: task-4.4 - Create Custom Exceptions

## Context
Domain-specific exceptions provide meaningful error messages for validation failures. Required by constraints.md Section 4.3.

## Prerequisites
- [ ] task-2.4 is complete
- [ ] ItemProcessor exists

## Instructions
1. Create file `InvalidTemperatureException.java` in `src/main/java/org/example/etl2/batch/processor/`
   - Extend `RuntimeException`
   - Constructor accepts temperature value and reason
   - Meaningful error message
2. Create file `InvalidDateTimeException.java` in `src/main/java/org/example/etl2/batch/processor/`
   - Extend `RuntimeException`
   - Constructor accepts datetime string and reason
   - Meaningful error message
3. Update `TemperatureItemProcessor` to throw these exceptions (optional, for detailed error tracking)

## Constraints (from spec)
- SHOULD: Create custom exceptions for domain-specific errors (constraints.md Section 4.3)
- MUST: Use specific exception types, not generic Exception (constraints.md Section 4.3)
- MUST NOT: Throw RuntimeException without wrapping (constraints.md Section 4.3)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/processor/InvalidTemperatureException.java` | CREATE | Temperature validation error |
| `src/main/java/org/example/etl2/batch/processor/InvalidDateTimeException.java` | CREATE | DateTime validation error |

## Acceptance Criteria
- [ ] `InvalidTemperatureException.java` exists
- [ ] `InvalidDateTimeException.java` exists
- [ ] Both extend `RuntimeException`
- [ ] Both have meaningful constructors
- [ ] Both compile without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/processor/InvalidTemperatureException.java`
2. Delete `src/main/java/org/example/etl2/batch/processor/InvalidDateTimeException.java`
