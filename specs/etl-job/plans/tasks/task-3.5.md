# Task: task-3.5 - Configure Job Auto-Start

## Context
The job must run automatically when the application starts. Required by requirements.md Section 6.7.

## Prerequisites
- [ ] task-3.3 is complete
- [ ] Job bean is defined

## Instructions
1. Open `src/main/resources/application.properties`
2. Ensure Spring Batch job runs on startup:
   - `spring.batch.job.enabled=true` (default, but explicit)
3. Optionally configure job parameters if needed
4. Verify no configuration prevents auto-start

## Constraints (from spec)
- MUST: Job triggers automatically on application startup (requirements.md Section 6.7)
- MUST: Single job instance at a time (requirements.md Section 6.7)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/application.properties` | MODIFY | Enable job auto-start |

## Acceptance Criteria
- [ ] Property `spring.batch.job.enabled=true` is set (or not disabled)
- [ ] Job runs when application starts
- [ ] No configuration prevents job execution

## Rollback
If this task fails validation:
1. Revert changes to `application.properties`
