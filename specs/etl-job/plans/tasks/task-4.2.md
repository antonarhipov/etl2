# Task: task-4.2 - Create SkipItemListener

## Context
This listener tracks and logs skipped records with full details. Required by constraints.md Section 2.3 and AC-5.4.

## Prerequisites
- [ ] task-3.4 is complete
- [ ] Skip policy is configured

## Instructions
1. Create file `SkipItemListener.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Implement `SkipListener<TemperatureReading, TemperatureReading>` interface
3. Use `@Component` annotation
4. Implement `onSkipInRead(Throwable t)`:
   - Log line number (from exception if `FlatFileParseException`)
   - Log raw content (from exception)
   - Log error reason (exception message)
5. Implement `onSkipInProcess(TemperatureReading item, Throwable t)`:
   - Log item details
   - Log error reason
6. Implement `onSkipInWrite(TemperatureReading item, Throwable t)`:
   - Log item details
   - Log error reason
7. Format as per AC-5.4: line number, raw content, error reason

## Constraints (from spec)
- MUST: Implement `SkipListener` (constraints.md Section 2.3)
- MUST: Log line number, raw content, and error reason (constraints.md Section 7.1)
- MUST: Use specific exception types (constraints.md Section 4.3)
- MUST NOT: Swallow exceptions silently (constraints.md Section 4.3)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/SkipItemListener.java` | CREATE | Skip tracking and logging |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `SkipListener<TemperatureReading, TemperatureReading>`
- [ ] Logs line number for read errors
- [ ] Logs raw content for read errors
- [ ] Logs error reason for all skips
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/SkipItemListener.java`
