# Task: task-1.2 - Create Flyway Migration

## Context
This task creates the database schema for storing temperature data. The migration must be executed by Flyway before any batch job processing can occur. This is required by AC-4.1 and AC-4.3.

## Prerequisites
- [ ] Project has Flyway dependency configured

## Instructions
1. Create directory `src/main/resources/db/migration/` if it doesn't exist
2. Create file `V1__create_temperature_data_table.sql`
3. Write CREATE TABLE statement with:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `name` VARCHAR(255) NOT NULL
   - `datetime` DATETIME NOT NULL
   - `temp` DECIMAL(5,1) NOT NULL
   - UNIQUE KEY `uk_name_datetime` on (`name`, `datetime`)

## Constraints (from spec)
- MUST: Create table named `temperature_data` (constraints.md Section 6.1)
- MUST: Use Flyway migration naming pattern `V{version}__{description}.sql`
- MUST: Include composite unique constraint on (name, datetime)
- MUST NOT: Use Hibernate auto-DDL

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/db/migration/V1__create_temperature_data_table.sql` | CREATE | Database schema |

## Acceptance Criteria
- [ ] File `src/main/resources/db/migration/V1__create_temperature_data_table.sql` exists
- [ ] SQL contains `CREATE TABLE temperature_data`
- [ ] SQL contains `id BIGINT AUTO_INCREMENT PRIMARY KEY`
- [ ] SQL contains `name VARCHAR(255) NOT NULL`
- [ ] SQL contains `datetime DATETIME NOT NULL`
- [ ] SQL contains `temp DECIMAL(5,1) NOT NULL`
- [ ] SQL contains `UNIQUE KEY uk_name_datetime (name, datetime)`

## Rollback
If this task fails validation:
1. Delete `src/main/resources/db/migration/V1__create_temperature_data_table.sql`
