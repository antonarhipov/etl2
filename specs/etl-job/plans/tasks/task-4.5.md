# Task: task-4.5 - Create File Renaming Component

## Context
Processed CSV files are renamed to prevent reprocessing. Required by AC-1.6.

## Prerequisites
- [ ] task-4.1 is complete
- [ ] JobCompletionListener exists

## Instructions
1. Create file `ProcessedFileRenamer.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Use `@Component` annotation
3. Implement method to rename processed files:
   - Accept list of Resource objects (processed files)
   - For each file, rename by adding `.processed` suffix
   - Example: `data.csv` → `data.csv.processed`
4. Handle errors gracefully (log but don't fail job)
5. Call from JobCompletionListener after successful job completion

## Constraints (from spec)
- MUST: Add `.processed` suffix to processed files (AC-1.6)
- MUST: Rename only after successful processing
- SHOULD: Log renaming operations

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/ProcessedFileRenamer.java` | CREATE | File renaming after processing |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Renames files by adding `.processed` suffix
- [ ] Handles file renaming errors gracefully
- [ ] Logs renaming operations
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/ProcessedFileRenamer.java`
