# Task: task-3.3 - Create Batch Job Configuration Class

## Context
This is the main batch configuration class that wires together all components into a job. Required by constraints.md Section 2.2 and AC-1.5, AC-4.4.

## Prerequisites
- [ ] task-3.2 is complete
- [ ] task-2.3 is complete
- [ ] task-2.4 is complete
- [ ] All reader, processor, and writer beans exist

## Instructions
1. Create file `TemperatureImportJobConfig.java` in `src/main/java/org/example/etl2/batch/`
2. Annotate with `@Configuration` and `@EnableBatchProcessing`
3. Inject via constructor:
   - `JobRepository jobRepository`
   - `PlatformTransactionManager transactionManager`
   - `MultiResourceItemReader<TemperatureReading>` reader
   - `TemperatureItemProcessor` processor
   - `JdbcBatchItemWriter<TemperatureReading>` writer
4. Inject `batch.chunk-size` property (default 1000)
5. Define `@Bean` for `Step importStep()`:
   - Name: `"importStep"`
   - Chunk size from property
   - Reader, processor, writer
6. Define `@Bean` for `Job temperatureImportJob()`:
   - Name: `"temperatureImportJob"`
   - Start with `importStep`

## Constraints (from spec)
- MUST: Define exactly one Job named `temperatureImportJob` (constraints.md Section 2.2)
- MUST: Define exactly one Step named `importStep` (constraints.md Section 2.2)
- MUST: Use chunk size of 1000 (constraints.md Section 2.2)
- MUST: Annotate with `@Configuration` and `@EnableBatchProcessing` (constraints.md Section 2.4)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST NOT: Use tasklet-based steps (constraints.md Section 2.2)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | CREATE | Main batch job configuration |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration` and `@EnableBatchProcessing`
- [ ] Job bean named `temperatureImportJob`
- [ ] Step bean named `importStep`
- [ ] Chunk size is 1000 (from property)
- [ ] Uses constructor injection
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java`
