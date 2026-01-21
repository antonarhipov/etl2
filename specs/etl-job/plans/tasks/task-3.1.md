# Task: task-3.1 - Create FlatFileItemReader Configuration

## Context
This reader parses individual CSV files. It will be wrapped by MultiResourceItemReader to process multiple files. Required by constraints.md Section 3.2 and AC-1.1, AC-1.2, AC-1.3.

## Prerequisites
- [ ] task-2.2 is complete
- [ ] `TemperatureReadingFieldSetMapper` exists

## Instructions
1. Create file `TemperatureItemReaderConfig.java` in `src/main/java/org/example/etl2/batch/reader/`
2. Create a `@Configuration` class
3. Define `@Bean` method with `@StepScope` returning `FlatFileItemReader<TemperatureReading>`
4. Use `FlatFileItemReaderBuilder` to configure:
   - `name("temperatureItemReader")`
   - `linesToSkip(1)` - skip header row
   - `encoding("UTF-8")`
   - `delimited().delimiter(",")` - comma delimiter
   - `names("name", "datetime", "temp")` - column names
   - `fieldSetMapper(new TemperatureReadingFieldSetMapper())`
5. Do NOT set resource here (will be set by MultiResourceItemReader)

## Constraints (from spec)
- MUST: Use `FlatFileItemReader` with `DefaultLineMapper` (constraints.md Section 3.2)
- MUST: Use `DelimitedLineTokenizer` with comma delimiter (constraints.md Section 3.2)
- MUST: Skip header row (constraints.md Section 3.2)
- MUST: Support UTF-8 encoding (constraints.md Section 3.2)
- MUST NOT: Load entire file into memory (constraints.md Section 3.2)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/TemperatureItemReaderConfig.java` | CREATE | CSV reader configuration |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Bean annotated with `@StepScope`
- [ ] Reader skips 1 line (header)
- [ ] Reader uses UTF-8 encoding
- [ ] Reader uses comma delimiter
- [ ] Reader uses custom FieldSetMapper
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/TemperatureItemReaderConfig.java`
