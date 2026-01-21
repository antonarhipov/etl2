# Task: task-5.5 - Test Error Handling

## Context
Tests verify malformed data is handled correctly per AC-5.1, AC-5.2, AC-5.3.

## Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

## Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testInvalidDatetime()`:
   - Use CSV with invalid datetime format
   - Assert row is skipped
   - Assert job continues and completes
3. Add test method `testMissingRequiredField()`:
   - Use CSV with missing name/datetime/temp
   - Assert row is skipped
   - Assert job continues and completes
4. Add test method `testInvalidTemperature()`:
   - Use CSV with non-numeric temperature
   - Assert row is skipped
   - Assert job continues and completes
5. Add test method `testMalformedDataFile()`:
   - Use `malformed_data.csv`
   - Assert job completes
   - Assert valid rows processed, invalid rows skipped
   - Assert error count matches expected

## Constraints (from spec)
- MUST: Test malformed data scenarios (constraints.md Section 5.3)
- MUST: Verify malformed rows skipped, job continues (AC-5.5)
- MUST: Verify error count accuracy

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add error handling tests |

## Acceptance Criteria
- [ ] Test `testInvalidDatetime()` exists and passes
- [ ] Test `testMissingRequiredField()` exists and passes
- [ ] Test `testInvalidTemperature()` exists and passes
- [ ] Test `testMalformedDataFile()` exists and passes
- [ ] Malformed rows are skipped
- [ ] Job completes despite errors
- [ ] Error count is accurate

## Rollback
If this task fails validation:
1. Remove error handling test methods from `TemperatureImportJobTest.java`
