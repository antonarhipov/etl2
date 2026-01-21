# Task: task-5.6 - Test Edge Cases

## Context
Tests cover edge cases: empty file, mixed data, summary formula validation. Required by AC-1.4 and AC-6.5.

## Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

## Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testEmptyFile()`:
   - Use `empty_file.csv`
   - Assert job completes with status COMPLETED
   - Assert 0 records processed, 0 inserts, 0 duplicates, 0 errors
3. Add test method `testMixedData()`:
   - Use `mixed_data.csv`
   - Assert job completes
   - Assert correct counts for each category (valid, duplicate, error)
4. Add test method `testSummaryFormulaValidation()`:
   - Use any test data
   - Extract summary counts
   - Assert: processed = inserts + duplicates + errors (AC-6.5)

## Constraints (from spec)
- MUST: Test empty file (constraints.md Section 5.3)
- MUST: Verify summary formula (AC-6.5)
- SHOULD: Test mixed valid/invalid/duplicate data (constraints.md Section 5.3)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add edge case tests |

## Acceptance Criteria
- [ ] Test `testEmptyFile()` exists and passes
- [ ] Test `testMixedData()` exists and passes
- [ ] Test `testSummaryFormulaValidation()` exists and passes
- [ ] Empty file job completes with zero counts
- [ ] Summary formula validated: processed = inserts + duplicates + errors

## Rollback
If this task fails validation:
1. Remove edge case test methods from `TemperatureImportJobTest.java`
