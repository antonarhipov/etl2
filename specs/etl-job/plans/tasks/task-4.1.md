# Task: task-4.1 - Create JobCompletionListener

## Context
This listener reports job summary statistics after completion. Required by constraints.md Section 2.3 and AC-6.1 through AC-6.5.

## Prerequisites
- [ ] task-3.3 is complete
- [ ] Job configuration exists

## Instructions
1. Create file `JobCompletionListener.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Implement `JobExecutionListener` interface
3. Use `@Component` annotation for Spring management
4. Implement `afterJob(JobExecution jobExecution)` method:
   - Get step execution from job execution
   - Extract metrics: read count, write count, skip count
   - Calculate: processed = read count, inserts = write count, duplicates (tracked separately), errors = skip count
   - Log summary using SLF4J at INFO level
5. Format summary as per AC-6.1:
   ```
   Job Summary:
   - Total records processed: X
   - Successful inserts: Y
   - Duplicates ignored: Z
   - Errors: W
   ```
6. Validate formula: processed = inserts + duplicates + errors (AC-6.5)

## Constraints (from spec)
- MUST: Implement `JobExecutionListener` (constraints.md Section 2.3)
- SHOULD: Use `@AfterJob` annotation (constraints.md Section 2.3)
- MUST: Log summary with all four metrics (constraints.md Section 7.1)
- MUST NOT: Use `System.out.println` (constraints.md Section 4.3)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/JobCompletionListener.java` | CREATE | Job summary reporting |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `JobExecutionListener`
- [ ] Logs total records processed
- [ ] Logs successful inserts
- [ ] Logs duplicates count
- [ ] Logs errors count
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/JobCompletionListener.java`
