# Task: task-3.4 - Configure Skip Policy

## Context
This adds fault tolerance to skip malformed records without failing the entire job. Required by constraints.md Section 2.2 and AC-5.5.

## Prerequisites
- [ ] task-3.3 is complete
- [ ] `TemperatureImportJobConfig` exists with step definition

## Instructions
1. Open `TemperatureImportJobConfig.java`
2. Modify `importStep()` to add fault tolerance:
   - Add `.faultTolerant()`
   - Add `.skip(FlatFileParseException.class)` for CSV parsing errors
   - Add `.skip(Exception.class)` for general validation errors
   - Add `.skipLimit(Integer.MAX_VALUE)` for unlimited skips
3. Ensure skip policy allows job to continue on malformed rows

## Constraints (from spec)
- SHOULD: Configure fault tolerance with skip policy (constraints.md Section 2.2)
- MUST: Skip malformed rows and continue processing (requirements.md Section 6.5)
- MUST: Log exceptions with full context (constraints.md Section 4.3)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | MODIFY | Add skip policy |

## Acceptance Criteria
- [ ] Step configuration includes `.faultTolerant()`
- [ ] Skips `FlatFileParseException`
- [ ] Skip limit is unlimited (or very high)
- [ ] Job continues after encountering malformed rows
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Revert changes to `TemperatureImportJobConfig.java`
2. Remove fault tolerance configuration
