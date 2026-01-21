# Task: task-1.4 - Create Test Resources Directory

## Context
This task prepares the test environment for integration testing with Testcontainers. Test CSV files and test-specific configuration will be placed here.

## Prerequisites
- [ ] Project test directory exists: `src/test/`

## Instructions
1. Create directory `src/test/resources/data/`
2. Create file `src/test/resources/application-test.properties`
3. Configure test properties:
   - Set `batch.input.directory` to test data directory
   - Configure Testcontainers MySQL connection
   - Disable job auto-launch for controlled test execution

## Constraints (from spec)
- MUST: Place test CSV files in `src/test/resources/data/` (constraints.md Section 1.2)
- SHOULD: Use `application-test.properties` for test-specific configuration (constraints.md Section 1.2)
- MUST NOT: Use H2 or any in-memory database (constraints.md Section 5.1)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/resources/data/.gitkeep` | CREATE | Ensure directory exists in VCS |
| `src/test/resources/application-test.properties` | CREATE | Test-specific configuration |

## Acceptance Criteria
- [ ] Directory `src/test/resources/data/` exists
- [ ] File `src/test/resources/application-test.properties` exists
- [ ] Test properties disable job auto-launch
- [ ] Test properties configure batch input directory

## Rollback
If this task fails validation:
1. Delete `src/test/resources/data/` directory
2. Delete `src/test/resources/application-test.properties`
