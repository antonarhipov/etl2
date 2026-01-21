# Task: task-2.4 - Create ItemProcessor for Validation

## Context
This processor validates temperature records before writing to database. It returns null for invalid records (which Spring Batch will skip). Required by constraints.md Section 2.2.

## Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

## Instructions
1. Create file `TemperatureItemProcessor.java` in `src/main/java/org/example/etl2/batch/processor/`
2. Implement `ItemProcessor<TemperatureReading, TemperatureReading>`
3. In `process(TemperatureReading item)` method, validate:
   - `name` is not null or blank
   - `datetime` is not null
   - `temp` is not null
4. Return `item` if valid, return `null` if invalid (Spring Batch skips null returns)
5. Add logging for validation failures using SLF4J

## Constraints (from spec)
- MUST: Implement `ItemProcessor` for validation (constraints.md Section 2.2)
- MUST: Return null for invalid records (Spring Batch convention)
- SHOULD: Use `@Slf4j` or constructor-injected Logger (constraints.md Section 4.3)
- MUST NOT: Use `System.out.println` for logging

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/processor/TemperatureItemProcessor.java` | CREATE | Data validation |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `ItemProcessor<TemperatureReading, TemperatureReading>`
- [ ] Validates name is not null/blank
- [ ] Validates datetime is not null
- [ ] Validates temp is not null
- [ ] Returns null for invalid records
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/processor/TemperatureItemProcessor.java`
