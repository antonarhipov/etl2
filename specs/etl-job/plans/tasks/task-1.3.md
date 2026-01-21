# Task: task-1.3 - Configure Application Properties

## Context
This task configures essential application properties for batch processing, database connection, and Flyway. These properties control runtime behavior of the batch job.

## Prerequisites
- [ ] File `src/main/resources/application.properties` exists

## Instructions
1. Open `src/main/resources/application.properties`
2. Add batch configuration:
   - `batch.input.directory=./input` (configurable input directory)
   - `batch.chunk-size=1000` (chunk size for processing)
3. Add MySQL datasource configuration placeholders
4. Add Flyway configuration
5. Add Spring Batch configuration to allow job to run on startup

## Constraints (from spec)
- MUST: Define `batch.input.directory` property (constraints.md Section 3.4)
- MUST: Define `batch.chunk-size` with default 1000 (constraints.md Section 3.4)
- SHOULD: Provide sensible defaults for all optional properties

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/application.properties` | MODIFY | Add batch and database configuration |

## Acceptance Criteria
- [ ] Property `batch.input.directory` is defined
- [ ] Property `batch.chunk-size=1000` is defined
- [ ] MySQL datasource properties are configured
- [ ] Flyway is enabled
- [ ] Spring Batch job enabled to run on startup

## Rollback
If this task fails validation:
1. Restore original `application.properties` content
2. Remove added batch configuration properties
