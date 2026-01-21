# Task: task-4.6 - Register Listeners with Job

## Context
All listeners must be registered with the job configuration to be invoked during execution.

## Prerequisites
- [ ] task-4.1 is complete
- [ ] task-4.2 is complete
- [ ] task-4.3 is complete
- [ ] task-4.5 is complete
- [ ] All listener components exist

## Instructions
1. Open `TemperatureImportJobConfig.java`
2. Inject listeners via constructor:
   - `JobCompletionListener jobCompletionListener`
   - `SkipItemListener skipItemListener`
   - `DuplicateLogWriter duplicateLogWriter`
   - `ProcessedFileRenamer processedFileRenamer`
3. Register `JobCompletionListener` with job:
   - `.listener(jobCompletionListener)`
4. Register `SkipItemListener` with step:
   - `.listener(skipItemListener)`
5. Wire `DuplicateLogWriter` and `ProcessedFileRenamer` through `JobCompletionListener`

## Constraints (from spec)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST: Register listeners with appropriate job/step
- MUST NOT: Use field injection with @Autowired (constraints.md Section 2.4)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | MODIFY | Register all listeners |

## Acceptance Criteria
- [ ] All listeners injected via constructor
- [ ] `JobCompletionListener` registered with job
- [ ] `SkipItemListener` registered with step
- [ ] All listeners invoked during job execution
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Revert changes to `TemperatureImportJobConfig.java`
2. Remove listener registrations
