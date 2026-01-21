# Task: task-4.3 - Create DuplicateLogWriter

## Context
This component writes duplicate record details to a separate log file. Required by constraints.md Section 7.2 and AC-3.2.

## Prerequisites
- [ ] task-4.1 is complete
- [ ] JobCompletionListener exists

## Instructions
1. Create file `DuplicateLogWriter.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Use `@Component` annotation
3. Create method to initialize log file:
   - Directory: `logs/`
   - Filename pattern: `duplicates-{timestamp}.log` where timestamp is `yyyyMMdd-HHmmss`
   - Create directory if not exists
4. Create method to write duplicate record:
   - Line format: `{timestamp}|{name}|{datetime}|{temp}`
   - Timestamp is ISO-8601 when duplicate was detected
5. Add header line: `# Duplicate records detected during job execution {jobId}`
6. Ensure writes are flushed for durability
7. Close file on job completion

## Constraints (from spec)
- MUST: Write to `logs/` directory (constraints.md Section 7.2)
- MUST: Use filename pattern `duplicates-{timestamp}.log` (constraints.md Section 7.2)
- MUST: Use line format `{timestamp}|{name}|{datetime}|{temp}` (constraints.md Section 7.2)
- SHOULD: Include header line (constraints.md Section 7.2)
- SHOULD: Flush writes (constraints.md Section 7.2)
- MUST NOT: Overwrite existing files (constraints.md Section 7.2)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/DuplicateLogWriter.java` | CREATE | Duplicate record logging |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Creates log file in `logs/` directory
- [ ] Filename matches pattern `duplicates-{timestamp}.log`
- [ ] Line format is `{timestamp}|{name}|{datetime}|{temp}`
- [ ] Includes header line with job ID
- [ ] Creates new file for each job execution
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/DuplicateLogWriter.java`
