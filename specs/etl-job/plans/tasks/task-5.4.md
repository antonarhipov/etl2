# Task: task-5.4 - Test Duplicate Handling

## Context
Tests verify duplicate detection and handling per AC-3.1, AC-3.3, AC-3.4.

## Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

## Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testWithinFileDuplicates()`:
   - Use `with_duplicates.csv`
   - Assert job completes successfully
   - Assert first occurrence inserted, duplicates skipped
   - Assert duplicate count in summary
3. Add test method `testDatabaseDuplicates()`:
   - Pre-insert records to database
   - Run job with CSV containing same name+datetime
   - Assert records detected as duplicates
4. Add test method `testReprocessSameFile()`:
   - Run job with `valid_data.csv`
   - Run job again with same file
   - Assert all records detected as duplicates on second run

## Constraints (from spec)
- MUST: Test within-file duplicates (constraints.md Section 5.3)
- SHOULD: Test re-processing same file (constraints.md Section 5.3)
- MUST: Verify duplicates are skipped and counted (AC-3.1, AC-3.3, AC-3.4)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add duplicate tests |

## Acceptance Criteria
- [ ] Test `testWithinFileDuplicates()` exists and passes
- [ ] Test `testDatabaseDuplicates()` exists and passes
- [ ] Test `testReprocessSameFile()` exists and passes
- [ ] Duplicates are correctly detected and skipped
- [ ] Duplicate count is accurate in summary

## Rollback
If this task fails validation:
1. Remove duplicate test methods from `TemperatureImportJobTest.java`
