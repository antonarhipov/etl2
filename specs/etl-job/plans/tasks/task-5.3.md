# Task: task-5.3 - Test Happy Path

## Context
Happy path test verifies valid CSV data is imported correctly. Required by constraints.md Section 5.3 and AC Happy Path scenario.

## Prerequisites
- [ ] task-5.2 is complete
- [ ] task-4.6 is complete
- [ ] Base test and all batch components are ready

## Instructions
1. Create file `TemperatureImportJobTest.java` in `src/test/java/org/example/etl2/batch/`
2. Extend `BaseIntegrationTest`
3. Create test method `testValidCsvImport()`:
   - Copy `valid_data.csv` to test input directory
   - Launch job using `jobLauncherTestUtils.launchJob()`
   - Assert job status is `BatchStatus.COMPLETED`
   - Query database for record count
   - Assert record count matches expected (number of data rows in CSV)
4. Use `@DisplayName` for descriptive test name
5. Use AssertJ for assertions

## Constraints (from spec)
- MUST: Test happy path (constraints.md Section 5.3)
- MUST: Verify job completion status is COMPLETED (constraints.md Section 5.5)
- MUST: Verify database record count (constraints.md Section 5.5)
- MUST: Use AssertJ for assertions (constraints.md Section 5.5)
- MUST NOT: Use `assertTrue(condition)` for specific assertions (constraints.md Section 5.5)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | CREATE | Integration tests |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Extends `BaseIntegrationTest`
- [ ] Test method `testValidCsvImport()` exists
- [ ] Job completes with status COMPLETED
- [ ] Database contains expected number of records
- [ ] Uses AssertJ assertions
- [ ] Test passes

## Rollback
If this task fails validation:
1. Delete `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java`
