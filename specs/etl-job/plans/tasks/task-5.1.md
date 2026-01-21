# Task: task-5.1 - Create Test CSV Files

## Context
Test data files are needed for all test scenarios. These files cover happy path, edge cases, and error conditions. Required by constraints.md Section 5.4.

## Prerequisites
- [ ] task-1.4 is complete
- [ ] Directory `src/test/resources/data/` exists

## Instructions
1. Create `valid_data.csv` with 10-20 valid records:
   ```csv
   name,datetime,temp
   Sensor1,2024-01-15T10:30:00,23.5
   Sensor2,2024-01-15T11:00:00,24.1
   ...
   ```
2. Create `empty_file.csv` with header only:
   ```csv
   name,datetime,temp
   ```
3. Create `with_duplicates.csv` with duplicate name+datetime pairs:
   ```csv
   name,datetime,temp
   Sensor1,2024-01-15T10:30:00,23.5
   Sensor1,2024-01-15T10:30:00,24.0
   Sensor2,2024-01-15T11:00:00,22.1
   ```
4. Create `malformed_data.csv` with invalid rows:
   ```csv
   name,datetime,temp
   Sensor1,invalid-date,23.5
   ,2024-01-15T10:30:00,23.5
   Sensor3,2024-01-15T12:00:00,not-a-number
   Sensor4,2024-01-15T13:00:00,25.0
   ```
5. Create `mixed_data.csv` combining valid, invalid, and duplicate records

## Constraints (from spec)
- MUST: Place files in `src/test/resources/data/` (constraints.md Section 5.4)
- MUST: Include files for each test scenario (constraints.md Section 5.4)
- SHOULD: Use realistic data patterns (constraints.md Section 5.4)
- SHOULD: Keep files under 100 records (constraints.md Section 5.4)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/resources/data/valid_data.csv` | CREATE | Happy path test data |
| `src/test/resources/data/empty_file.csv` | CREATE | Empty file test |
| `src/test/resources/data/with_duplicates.csv` | CREATE | Duplicate handling test |
| `src/test/resources/data/malformed_data.csv` | CREATE | Error handling test |
| `src/test/resources/data/mixed_data.csv` | CREATE | Combined scenario test |

## Acceptance Criteria
- [ ] All 5 CSV files exist in `src/test/resources/data/`
- [ ] `valid_data.csv` has header and valid data rows
- [ ] `empty_file.csv` has only header row
- [ ] `with_duplicates.csv` contains duplicate name+datetime pairs
- [ ] `malformed_data.csv` contains rows with invalid datetime, missing fields, invalid temp
- [ ] `mixed_data.csv` contains combination of all scenarios

## Rollback
If this task fails validation:
1. Delete created CSV files from `src/test/resources/data/`
