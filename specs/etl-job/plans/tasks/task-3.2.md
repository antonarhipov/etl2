# Task: task-3.2 - Create MultiResourceItemReader Configuration

## Context
This reader discovers and processes all CSV files in the configured input directory. It delegates to FlatFileItemReader for actual parsing. Required by constraints.md Section 3.2.

## Prerequisites
- [ ] task-3.1 is complete
- [ ] `FlatFileItemReader` bean exists

## Instructions
1. Create file `MultiFileReaderConfig.java` in `src/main/java/org/example/etl2/batch/reader/`
2. Create a `@Configuration` class
3. Inject `batch.input.directory` property using `@Value`
4. Define `@Bean` method returning `MultiResourceItemReader<TemperatureReading>`
5. Use `ResourcePatternResolver` to find all `*.csv` files in input directory
6. Configure:
   - Set resources from discovered CSV files
   - Set delegate to the `FlatFileItemReader` bean
7. Handle case when no files found (empty resource array)

## Constraints (from spec)
- SHOULD: Use `MultiResourceItemReader` for processing multiple files (constraints.md Section 3.2)
- MUST: Process all `.csv` files in configured directory
- MUST: Use configurable input directory path (constraints.md Section 3.4)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/MultiFileReaderConfig.java` | CREATE | Multi-file reader configuration |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Injects `batch.input.directory` property
- [ ] Uses `ResourcePatternResolver` to find CSV files
- [ ] Configures `MultiResourceItemReader` with delegate
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/MultiFileReaderConfig.java`
