# Task: task-2.3 - Create JdbcBatchItemWriter Configuration

## Context
This writer inserts temperature records into the database using INSERT IGNORE for duplicate handling. Required by constraints.md Section 3.1 and AC-3.1.

## Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

## Instructions
1. Create file `TemperatureItemWriter.java` in `src/main/java/org/example/etl2/batch/writer/`
2. Create a `@Configuration` class
3. Define `@Bean` method returning `JdbcBatchItemWriter<TemperatureReading>`
4. Use `JdbcBatchItemWriterBuilder` to configure:
   - DataSource injection via constructor
   - SQL: `INSERT IGNORE INTO temperature_data (name, datetime, temp) VALUES (:name, :datetime, :temp)`
   - Use `BeanPropertyItemSqlParameterSourceProvider` for parameter mapping
5. Use text block for SQL statement (Java 21 feature)

## Constraints (from spec)
- MUST: Use `JdbcBatchItemWriter` (constraints.md Section 2.2)
- MUST: Use `INSERT IGNORE` for duplicate handling (constraints.md Section 3.1)
- MUST: Use parameterized queries, no raw SQL strings (constraints.md Section 3.1)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST NOT: Use JPA/Hibernate

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/writer/TemperatureItemWriter.java` | CREATE | Database writer configuration |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Uses `JdbcBatchItemWriter<TemperatureReading>`
- [ ] SQL contains `INSERT IGNORE INTO temperature_data`
- [ ] Uses parameterized values (`:name`, `:datetime`, `:temp`)
- [ ] Uses constructor injection for DataSource
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/writer/TemperatureItemWriter.java`
