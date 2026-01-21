# Task: task-2.1 - Create TemperatureReading Record

## Context
This Java record is the core domain model for temperature data. All batch components (reader, processor, writer) will use this record. Required by constraints.md Section 2.1.

## Prerequisites
- [ ] task-1.1 is complete
- [ ] Package `org.example.etl2.model` exists

## Instructions
1. Create file `TemperatureReading.java` in `src/main/java/org/example/etl2/model/`
2. Define as Java record with fields:
   - `String name` - location/sensor name
   - `LocalDateTime datetime` - timestamp in ISO-8601
   - `BigDecimal temp` - temperature value
3. Add necessary imports for `java.time.LocalDateTime` and `java.math.BigDecimal`

## Constraints (from spec)
- MUST: Use Java record (not class)
- MUST: Use `BigDecimal` for temperature (not double/float)
- MUST: Use `LocalDateTime` for datetime (not Date/String)
- MUST NOT: Use JPA entities or Hibernate annotations
- MUST NOT: Create mutable DTOs with setters

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/model/TemperatureReading.java` | CREATE | Domain model |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Defined as `public record TemperatureReading`
- [ ] Has field `String name`
- [ ] Has field `LocalDateTime datetime`
- [ ] Has field `BigDecimal temp`
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/model/TemperatureReading.java`
