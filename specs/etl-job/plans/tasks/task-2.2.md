# Task: task-2.2 - Create Custom FieldSetMapper

## Context
This mapper converts CSV fields to TemperatureReading records. It handles parsing of ISO-8601 datetime and BigDecimal temperature values. Required by constraints.md Section 3.2.

## Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

## Instructions
1. Create file `TemperatureReadingFieldSetMapper.java` in `src/main/java/org/example/etl2/batch/reader/`
2. Implement `FieldSetMapper<TemperatureReading>` interface
3. In `mapFieldSet(FieldSet fieldSet)` method:
   - Read `name` field as String: `fieldSet.readString("name")`
   - Read `datetime` field as String and parse with `DateTimeFormatter.ISO_LOCAL_DATE_TIME`
   - Read `temp` field as BigDecimal: `fieldSet.readBigDecimal("temp")`
4. Return new `TemperatureReading` record with mapped values
5. Handle parsing exceptions appropriately (let them propagate for skip handling)

## Constraints (from spec)
- MUST: Use `BeanWrapperFieldSetMapper` or custom `FieldSetMapper` (constraints.md Section 3.2)
- MUST: Parse datetime using ISO-8601 format `yyyy-MM-dd'T'HH:mm:ss` (constraints.md Section 3.3)
- MUST: Use `DateTimeFormatter.ISO_LOCAL_DATE_TIME` for parsing
- MUST NOT: Use third-party CSV libraries

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/TemperatureReadingFieldSetMapper.java` | CREATE | CSV to record mapping |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `FieldSetMapper<TemperatureReading>`
- [ ] Parses datetime using `DateTimeFormatter.ISO_LOCAL_DATE_TIME`
- [ ] Reads temperature as `BigDecimal`
- [ ] Returns `TemperatureReading` record
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/TemperatureReadingFieldSetMapper.java`
