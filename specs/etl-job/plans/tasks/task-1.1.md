# Task: task-1.1 - Create Package Structure

## Context
This task establishes the foundational package hierarchy required for organizing all batch job components. All subsequent tasks depend on this structure being in place. The package organization follows the constraints defined in constraints.md Section 1.1.

## Prerequisites
- [ ] Project exists with base package `org.example.etl2`

## Instructions
1. Navigate to `src/main/java/org/example/etl2/`
2. Create directory `batch/` for Spring Batch job configuration
3. Create directory `batch/listener/` for job and step listeners
4. Create directory `batch/processor/` for ItemProcessor implementations
5. Create directory `batch/reader/` for ItemReader configurations
6. Create directory `batch/writer/` for ItemWriter implementations
7. Create directory `model/` for Java records and domain objects
8. Create directory `config/` for application configuration classes
9. Create `package-info.java` in each package with brief package documentation

## Constraints (from spec)
- MUST: Use base package `org.example.etl2`
- MUST: Organize code into specified sub-packages
- MUST NOT: Create deeply nested package structures beyond 4 levels
- MUST NOT: Place all batch components in a single configuration class

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/listener/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/processor/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/reader/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/writer/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/model/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/config/package-info.java` | CREATE | Package documentation |

## Acceptance Criteria
- [ ] Directory `src/main/java/org/example/etl2/batch/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/listener/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/processor/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/reader/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/writer/` exists
- [ ] Directory `src/main/java/org/example/etl2/model/` exists
- [ ] Directory `src/main/java/org/example/etl2/config/` exists
- [ ] Each package contains `package-info.java`

## Rollback
If this task fails validation:
1. Delete created directories under `src/main/java/org/example/etl2/`
2. Verify original project structure is intact
