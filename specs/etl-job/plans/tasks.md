# Task Cards: Temperature Data Import Batch Job

This document contains detailed task cards for implementing the Temperature Data Import Batch Job.
Each task card provides step-by-step implementation guidance based on the [Execution Plan](execution.md).

## Related Documentation
- [Execution Plan](execution.md)
- [Requirements Specification](../requirements.md)
- [Acceptance Criteria](../specs/etl-job/acceptance_criteria.md)
- [Technical Constraints](../specs/etl-job/constraints.md)

---

# Phase 1: Project Foundation & Infrastructure

---

## Task: task-1.1 - Create Package Structure

### Context
This task establishes the foundational package hierarchy required for organizing all batch job components. All subsequent tasks depend on this structure being in place. The package organization follows the constraints defined in constraints.md Section 1.1.

### Prerequisites
- [ ] Project exists with base package `org.example.etl2`

### Instructions
1. Navigate to `src/main/java/org/example/etl2/`
2. Create directory `batch/` for Spring Batch job configuration
3. Create directory `batch/listener/` for job and step listeners
4. Create directory `batch/processor/` for ItemProcessor implementations
5. Create directory `batch/reader/` for ItemReader configurations
6. Create directory `batch/writer/` for ItemWriter implementations
7. Create directory `model/` for Java records and domain objects
8. Create directory `config/` for application configuration classes
9. Create `package-info.java` in each package with brief package documentation

### Constraints (from spec)
- MUST: Use base package `org.example.etl2`
- MUST: Organize code into specified sub-packages
- MUST NOT: Create deeply nested package structures beyond 4 levels
- MUST NOT: Place all batch components in a single configuration class

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/listener/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/processor/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/reader/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/batch/writer/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/model/package-info.java` | CREATE | Package documentation |
| `src/main/java/org/example/etl2/config/package-info.java` | CREATE | Package documentation |

### Acceptance Criteria
- [ ] Directory `src/main/java/org/example/etl2/batch/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/listener/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/processor/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/reader/` exists
- [ ] Directory `src/main/java/org/example/etl2/batch/writer/` exists
- [ ] Directory `src/main/java/org/example/etl2/model/` exists
- [ ] Directory `src/main/java/org/example/etl2/config/` exists
- [ ] Each package contains `package-info.java`

### Rollback
If this task fails validation:
1. Delete created directories under `src/main/java/org/example/etl2/`
2. Verify original project structure is intact

---

## Task: task-1.2 - Create Flyway Migration

### Context
This task creates the database schema for storing temperature data. The migration must be executed by Flyway before any batch job processing can occur. This is required by AC-4.1 and AC-4.3.

### Prerequisites
- [ ] Project has Flyway dependency configured

### Instructions
1. Create directory `src/main/resources/db/migration/` if it doesn't exist
2. Create file `V1__create_temperature_data_table.sql`
3. Write CREATE TABLE statement with:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `name` VARCHAR(255) NOT NULL
   - `datetime` DATETIME NOT NULL
   - `temp` DECIMAL(5,1) NOT NULL
   - UNIQUE KEY `uk_name_datetime` on (`name`, `datetime`)

### Constraints (from spec)
- MUST: Create table named `temperature_data` (constraints.md Section 6.1)
- MUST: Use Flyway migration naming pattern `V{version}__{description}.sql`
- MUST: Include composite unique constraint on (name, datetime)
- MUST NOT: Use Hibernate auto-DDL

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/db/migration/V1__create_temperature_data_table.sql` | CREATE | Database schema |

### Acceptance Criteria
- [ ] File `src/main/resources/db/migration/V1__create_temperature_data_table.sql` exists
- [ ] SQL contains `CREATE TABLE temperature_data`
- [ ] SQL contains `id BIGINT AUTO_INCREMENT PRIMARY KEY`
- [ ] SQL contains `name VARCHAR(255) NOT NULL`
- [ ] SQL contains `datetime DATETIME NOT NULL`
- [ ] SQL contains `temp DECIMAL(5,1) NOT NULL`
- [ ] SQL contains `UNIQUE KEY uk_name_datetime (name, datetime)`

### Rollback
If this task fails validation:
1. Delete `src/main/resources/db/migration/V1__create_temperature_data_table.sql`

---

## Task: task-1.3 - Configure Application Properties

### Context
This task configures essential application properties for batch processing, database connection, and Flyway. These properties control runtime behavior of the batch job.

### Prerequisites
- [ ] File `src/main/resources/application.properties` exists

### Instructions
1. Open `src/main/resources/application.properties`
2. Add batch configuration:
   - `batch.input.directory=./input` (configurable input directory)
   - `batch.chunk-size=1000` (chunk size for processing)
3. Add MySQL datasource configuration placeholders
4. Add Flyway configuration
5. Add Spring Batch configuration to allow job to run on startup

### Constraints (from spec)
- MUST: Define `batch.input.directory` property (constraints.md Section 3.4)
- MUST: Define `batch.chunk-size` with default 1000 (constraints.md Section 3.4)
- SHOULD: Provide sensible defaults for all optional properties

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/application.properties` | MODIFY | Add batch and database configuration |

### Acceptance Criteria
- [ ] Property `batch.input.directory` is defined
- [ ] Property `batch.chunk-size=1000` is defined
- [ ] MySQL datasource properties are configured
- [ ] Flyway is enabled
- [ ] Spring Batch job enabled to run on startup

### Rollback
If this task fails validation:
1. Restore original `application.properties` content
2. Remove added batch configuration properties

---

## Task: task-1.4 - Create Test Resources Directory

### Context
This task prepares the test environment for integration testing with Testcontainers. Test CSV files and test-specific configuration will be placed here.

### Prerequisites
- [ ] Project test directory exists: `src/test/`

### Instructions
1. Create directory `src/test/resources/data/`
2. Create file `src/test/resources/application-test.properties`
3. Configure test properties:
   - Set `batch.input.directory` to test data directory
   - Configure Testcontainers MySQL connection
   - Disable job auto-launch for controlled test execution

### Constraints (from spec)
- MUST: Place test CSV files in `src/test/resources/data/` (constraints.md Section 1.2)
- SHOULD: Use `application-test.properties` for test-specific configuration (constraints.md Section 1.2)
- MUST NOT: Use H2 or any in-memory database (constraints.md Section 5.1)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/resources/data/.gitkeep` | CREATE | Ensure directory exists in VCS |
| `src/test/resources/application-test.properties` | CREATE | Test-specific configuration |

### Acceptance Criteria
- [ ] Directory `src/test/resources/data/` exists
- [ ] File `src/test/resources/application-test.properties` exists
- [ ] Test properties disable job auto-launch
- [ ] Test properties configure batch input directory

### Rollback
If this task fails validation:
1. Delete `src/test/resources/data/` directory
2. Delete `src/test/resources/application-test.properties`

---

# Phase 2: Core Domain Model & Data Access

---

## Task: task-2.1 - Create TemperatureReading Record

### Context
This Java record is the core domain model for temperature data. All batch components (reader, processor, writer) will use this record. Required by constraints.md Section 2.1.

### Prerequisites
- [ ] task-1.1 is complete
- [ ] Package `org.example.etl2.model` exists

### Instructions
1. Create file `TemperatureReading.java` in `src/main/java/org/example/etl2/model/`
2. Define as Java record with fields:
   - `String name` - location/sensor name
   - `LocalDateTime datetime` - timestamp in ISO-8601
   - `BigDecimal temp` - temperature value
3. Add necessary imports for `java.time.LocalDateTime` and `java.math.BigDecimal`

### Constraints (from spec)
- MUST: Use Java record (not class)
- MUST: Use `BigDecimal` for temperature (not double/float)
- MUST: Use `LocalDateTime` for datetime (not Date/String)
- MUST NOT: Use JPA entities or Hibernate annotations
- MUST NOT: Create mutable DTOs with setters

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/model/TemperatureReading.java` | CREATE | Domain model |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Defined as `public record TemperatureReading`
- [ ] Has field `String name`
- [ ] Has field `LocalDateTime datetime`
- [ ] Has field `BigDecimal temp`
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/model/TemperatureReading.java`

---

## Task: task-2.2 - Create Custom FieldSetMapper

### Context
This mapper converts CSV fields to TemperatureReading records. It handles parsing of ISO-8601 datetime and BigDecimal temperature values. Required by constraints.md Section 3.2.

### Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

### Instructions
1. Create file `TemperatureReadingFieldSetMapper.java` in `src/main/java/org/example/etl2/batch/reader/`
2. Implement `FieldSetMapper<TemperatureReading>` interface
3. In `mapFieldSet(FieldSet fieldSet)` method:
   - Read `name` field as String: `fieldSet.readString("name")`
   - Read `datetime` field as String and parse with `DateTimeFormatter.ISO_LOCAL_DATE_TIME`
   - Read `temp` field as BigDecimal: `fieldSet.readBigDecimal("temp")`
4. Return new `TemperatureReading` record with mapped values
5. Handle parsing exceptions appropriately (let them propagate for skip handling)

### Constraints (from spec)
- MUST: Use `BeanWrapperFieldSetMapper` or custom `FieldSetMapper` (constraints.md Section 3.2)
- MUST: Parse datetime using ISO-8601 format `yyyy-MM-dd'T'HH:mm:ss` (constraints.md Section 3.3)
- MUST: Use `DateTimeFormatter.ISO_LOCAL_DATE_TIME` for parsing
- MUST NOT: Use third-party CSV libraries

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/TemperatureReadingFieldSetMapper.java` | CREATE | CSV to record mapping |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `FieldSetMapper<TemperatureReading>`
- [ ] Parses datetime using `DateTimeFormatter.ISO_LOCAL_DATE_TIME`
- [ ] Reads temperature as `BigDecimal`
- [ ] Returns `TemperatureReading` record
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/TemperatureReadingFieldSetMapper.java`

---

## Task: task-2.3 - Create JdbcBatchItemWriter Configuration

### Context
This writer inserts temperature records into the database using INSERT IGNORE for duplicate handling. Required by constraints.md Section 3.1 and AC-3.1.

### Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

### Instructions
1. Create file `TemperatureItemWriter.java` in `src/main/java/org/example/etl2/batch/writer/`
2. Create a `@Configuration` class
3. Define `@Bean` method returning `JdbcBatchItemWriter<TemperatureReading>`
4. Use `JdbcBatchItemWriterBuilder` to configure:
   - DataSource injection via constructor
   - SQL: `INSERT IGNORE INTO temperature_data (name, datetime, temp) VALUES (:name, :datetime, :temp)`
   - Use `BeanPropertyItemSqlParameterSourceProvider` for parameter mapping
5. Use text block for SQL statement (Java 21 feature)

### Constraints (from spec)
- MUST: Use `JdbcBatchItemWriter` (constraints.md Section 2.2)
- MUST: Use `INSERT IGNORE` for duplicate handling (constraints.md Section 3.1)
- MUST: Use parameterized queries, no raw SQL strings (constraints.md Section 3.1)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST NOT: Use JPA/Hibernate

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/writer/TemperatureItemWriter.java` | CREATE | Database writer configuration |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Uses `JdbcBatchItemWriter<TemperatureReading>`
- [ ] SQL contains `INSERT IGNORE INTO temperature_data`
- [ ] Uses parameterized values (`:name`, `:datetime`, `:temp`)
- [ ] Uses constructor injection for DataSource
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/writer/TemperatureItemWriter.java`

---

## Task: task-2.4 - Create ItemProcessor for Validation

### Context
This processor validates temperature records before writing to database. It returns null for invalid records (which Spring Batch will skip). Required by constraints.md Section 2.2.

### Prerequisites
- [ ] task-2.1 is complete
- [ ] `TemperatureReading` record exists

### Instructions
1. Create file `TemperatureItemProcessor.java` in `src/main/java/org/example/etl2/batch/processor/`
2. Implement `ItemProcessor<TemperatureReading, TemperatureReading>`
3. In `process(TemperatureReading item)` method, validate:
   - `name` is not null or blank
   - `datetime` is not null
   - `temp` is not null
4. Return `item` if valid, return `null` if invalid (Spring Batch skips null returns)
5. Add logging for validation failures using SLF4J

### Constraints (from spec)
- MUST: Implement `ItemProcessor` for validation (constraints.md Section 2.2)
- MUST: Return null for invalid records (Spring Batch convention)
- SHOULD: Use `@Slf4j` or constructor-injected Logger (constraints.md Section 4.3)
- MUST NOT: Use `System.out.println` for logging

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/processor/TemperatureItemProcessor.java` | CREATE | Data validation |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `ItemProcessor<TemperatureReading, TemperatureReading>`
- [ ] Validates name is not null/blank
- [ ] Validates datetime is not null
- [ ] Validates temp is not null
- [ ] Returns null for invalid records
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/processor/TemperatureItemProcessor.java`

---

# Phase 3: Batch Job Configuration

---

## Task: task-3.1 - Create FlatFileItemReader Configuration

### Context
This reader parses individual CSV files. It will be wrapped by MultiResourceItemReader to process multiple files. Required by constraints.md Section 3.2 and AC-1.1, AC-1.2, AC-1.3.

### Prerequisites
- [ ] task-2.2 is complete
- [ ] `TemperatureReadingFieldSetMapper` exists

### Instructions
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

### Constraints (from spec)
- MUST: Use `FlatFileItemReader` with `DefaultLineMapper` (constraints.md Section 3.2)
- MUST: Use `DelimitedLineTokenizer` with comma delimiter (constraints.md Section 3.2)
- MUST: Skip header row (constraints.md Section 3.2)
- MUST: Support UTF-8 encoding (constraints.md Section 3.2)
- MUST NOT: Load entire file into memory (constraints.md Section 3.2)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/TemperatureItemReaderConfig.java` | CREATE | CSV reader configuration |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Bean annotated with `@StepScope`
- [ ] Reader skips 1 line (header)
- [ ] Reader uses UTF-8 encoding
- [ ] Reader uses comma delimiter
- [ ] Reader uses custom FieldSetMapper
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/TemperatureItemReaderConfig.java`

---

## Task: task-3.2 - Create MultiResourceItemReader Configuration

### Context
This reader discovers and processes all CSV files in the configured input directory. It delegates to FlatFileItemReader for actual parsing. Required by constraints.md Section 3.2.

### Prerequisites
- [ ] task-3.1 is complete
- [ ] `FlatFileItemReader` bean exists

### Instructions
1. Create file `MultiFileReaderConfig.java` in `src/main/java/org/example/etl2/batch/reader/`
2. Create a `@Configuration` class
3. Inject `batch.input.directory` property using `@Value`
4. Define `@Bean` method returning `MultiResourceItemReader<TemperatureReading>`
5. Use `ResourcePatternResolver` to find all `*.csv` files in input directory
6. Configure:
   - Set resources from discovered CSV files
   - Set delegate to the `FlatFileItemReader` bean
7. Handle case when no files found (empty resource array)

### Constraints (from spec)
- SHOULD: Use `MultiResourceItemReader` for processing multiple files (constraints.md Section 3.2)
- MUST: Process all `.csv` files in configured directory
- MUST: Use configurable input directory path (constraints.md Section 3.4)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/reader/MultiFileReaderConfig.java` | CREATE | Multi-file reader configuration |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration`
- [ ] Injects `batch.input.directory` property
- [ ] Uses `ResourcePatternResolver` to find CSV files
- [ ] Configures `MultiResourceItemReader` with delegate
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/reader/MultiFileReaderConfig.java`

---

## Task: task-3.3 - Create Batch Job Configuration Class

### Context
This is the main batch configuration class that wires together all components into a job. Required by constraints.md Section 2.2 and AC-1.5, AC-4.4.

### Prerequisites
- [ ] task-3.2 is complete
- [ ] task-2.3 is complete
- [ ] task-2.4 is complete
- [ ] All reader, processor, and writer beans exist

### Instructions
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

### Constraints (from spec)
- MUST: Define exactly one Job named `temperatureImportJob` (constraints.md Section 2.2)
- MUST: Define exactly one Step named `importStep` (constraints.md Section 2.2)
- MUST: Use chunk size of 1000 (constraints.md Section 2.2)
- MUST: Annotate with `@Configuration` and `@EnableBatchProcessing` (constraints.md Section 2.4)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST NOT: Use tasklet-based steps (constraints.md Section 2.2)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | CREATE | Main batch job configuration |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Class annotated with `@Configuration` and `@EnableBatchProcessing`
- [ ] Job bean named `temperatureImportJob`
- [ ] Step bean named `importStep`
- [ ] Chunk size is 1000 (from property)
- [ ] Uses constructor injection
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java`

---

## Task: task-3.4 - Configure Skip Policy

### Context
This adds fault tolerance to skip malformed records without failing the entire job. Required by constraints.md Section 2.2 and AC-5.5.

### Prerequisites
- [ ] task-3.3 is complete
- [ ] `TemperatureImportJobConfig` exists with step definition

### Instructions
1. Open `TemperatureImportJobConfig.java`
2. Modify `importStep()` to add fault tolerance:
   - Add `.faultTolerant()`
   - Add `.skip(FlatFileParseException.class)` for CSV parsing errors
   - Add `.skip(Exception.class)` for general validation errors
   - Add `.skipLimit(Integer.MAX_VALUE)` for unlimited skips
3. Ensure skip policy allows job to continue on malformed rows

### Constraints (from spec)
- SHOULD: Configure fault tolerance with skip policy (constraints.md Section 2.2)
- MUST: Skip malformed rows and continue processing (requirements.md Section 6.5)
- MUST: Log exceptions with full context (constraints.md Section 4.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | MODIFY | Add skip policy |

### Acceptance Criteria
- [ ] Step configuration includes `.faultTolerant()`
- [ ] Skips `FlatFileParseException`
- [ ] Skip limit is unlimited (or very high)
- [ ] Job continues after encountering malformed rows
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Revert changes to `TemperatureImportJobConfig.java`
2. Remove fault tolerance configuration

---

## Task: task-3.5 - Configure Job Auto-Start

### Context
The job must run automatically when the application starts. Required by requirements.md Section 6.7.

### Prerequisites
- [ ] task-3.3 is complete
- [ ] Job bean is defined

### Instructions
1. Open `src/main/resources/application.properties`
2. Ensure Spring Batch job runs on startup:
   - `spring.batch.job.enabled=true` (default, but explicit)
3. Optionally configure job parameters if needed
4. Verify no configuration prevents auto-start

### Constraints (from spec)
- MUST: Job triggers automatically on application startup (requirements.md Section 6.7)
- MUST: Single job instance at a time (requirements.md Section 6.7)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/resources/application.properties` | MODIFY | Enable job auto-start |

### Acceptance Criteria
- [ ] Property `spring.batch.job.enabled=true` is set (or not disabled)
- [ ] Job runs when application starts
- [ ] No configuration prevents job execution

### Rollback
If this task fails validation:
1. Revert changes to `application.properties`

---

# Phase 4: Listeners, Logging & Error Handling

---

## Task: task-4.1 - Create JobCompletionListener

### Context
This listener reports job summary statistics after completion. Required by constraints.md Section 2.3 and AC-6.1 through AC-6.5.

### Prerequisites
- [ ] task-3.3 is complete
- [ ] Job configuration exists

### Instructions
1. Create file `JobCompletionListener.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Implement `JobExecutionListener` interface
3. Use `@Component` annotation for Spring management
4. Implement `afterJob(JobExecution jobExecution)` method:
   - Get step execution from job execution
   - Extract metrics: read count, write count, skip count
   - Calculate: processed = read count, inserts = write count, duplicates (tracked separately), errors = skip count
   - Log summary using SLF4J at INFO level
5. Format summary as per AC-6.1:
   ```
   Job Summary:
   - Total records processed: X
   - Successful inserts: Y
   - Duplicates ignored: Z
   - Errors: W
   ```
6. Validate formula: processed = inserts + duplicates + errors (AC-6.5)

### Constraints (from spec)
- MUST: Implement `JobExecutionListener` (constraints.md Section 2.3)
- SHOULD: Use `@AfterJob` annotation (constraints.md Section 2.3)
- MUST: Log summary with all four metrics (constraints.md Section 7.1)
- MUST NOT: Use `System.out.println` (constraints.md Section 4.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/JobCompletionListener.java` | CREATE | Job summary reporting |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `JobExecutionListener`
- [ ] Logs total records processed
- [ ] Logs successful inserts
- [ ] Logs duplicates count
- [ ] Logs errors count
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/JobCompletionListener.java`

---

## Task: task-4.2 - Create SkipItemListener

### Context
This listener tracks and logs skipped records with full details. Required by constraints.md Section 2.3 and AC-5.4.

### Prerequisites
- [ ] task-3.4 is complete
- [ ] Skip policy is configured

### Instructions
1. Create file `SkipItemListener.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Implement `SkipListener<TemperatureReading, TemperatureReading>` interface
3. Use `@Component` annotation
4. Implement `onSkipInRead(Throwable t)`:
   - Log line number (from exception if `FlatFileParseException`)
   - Log raw content (from exception)
   - Log error reason (exception message)
5. Implement `onSkipInProcess(TemperatureReading item, Throwable t)`:
   - Log item details
   - Log error reason
6. Implement `onSkipInWrite(TemperatureReading item, Throwable t)`:
   - Log item details
   - Log error reason
7. Format as per AC-5.4: line number, raw content, error reason

### Constraints (from spec)
- MUST: Implement `SkipListener` (constraints.md Section 2.3)
- MUST: Log line number, raw content, and error reason (constraints.md Section 7.1)
- MUST: Use specific exception types (constraints.md Section 4.3)
- MUST NOT: Swallow exceptions silently (constraints.md Section 4.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/SkipItemListener.java` | CREATE | Skip tracking and logging |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Implements `SkipListener<TemperatureReading, TemperatureReading>`
- [ ] Logs line number for read errors
- [ ] Logs raw content for read errors
- [ ] Logs error reason for all skips
- [ ] Uses SLF4J logging
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/SkipItemListener.java`

---

## Task: task-4.3 - Create DuplicateLogWriter

### Context
This component writes duplicate record details to a separate log file. Required by constraints.md Section 7.2 and AC-3.2.

### Prerequisites
- [ ] task-4.1 is complete
- [ ] JobCompletionListener exists

### Instructions
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

### Constraints (from spec)
- MUST: Write to `logs/` directory (constraints.md Section 7.2)
- MUST: Use filename pattern `duplicates-{timestamp}.log` (constraints.md Section 7.2)
- MUST: Use line format `{timestamp}|{name}|{datetime}|{temp}` (constraints.md Section 7.2)
- SHOULD: Include header line (constraints.md Section 7.2)
- SHOULD: Flush writes (constraints.md Section 7.2)
- MUST NOT: Overwrite existing files (constraints.md Section 7.2)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/DuplicateLogWriter.java` | CREATE | Duplicate record logging |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Creates log file in `logs/` directory
- [ ] Filename matches pattern `duplicates-{timestamp}.log`
- [ ] Line format is `{timestamp}|{name}|{datetime}|{temp}`
- [ ] Includes header line with job ID
- [ ] Creates new file for each job execution
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/DuplicateLogWriter.java`

---

## Task: task-4.4 - Create Custom Exceptions

### Context
Domain-specific exceptions provide meaningful error messages for validation failures. Required by constraints.md Section 4.3.

### Prerequisites
- [ ] task-2.4 is complete
- [ ] ItemProcessor exists

### Instructions
1. Create file `InvalidTemperatureException.java` in `src/main/java/org/example/etl2/batch/processor/`
   - Extend `RuntimeException`
   - Constructor accepts temperature value and reason
   - Meaningful error message
2. Create file `InvalidDateTimeException.java` in `src/main/java/org/example/etl2/batch/processor/`
   - Extend `RuntimeException`
   - Constructor accepts datetime string and reason
   - Meaningful error message
3. Update `TemperatureItemProcessor` to throw these exceptions (optional, for detailed error tracking)

### Constraints (from spec)
- SHOULD: Create custom exceptions for domain-specific errors (constraints.md Section 4.3)
- MUST: Use specific exception types, not generic Exception (constraints.md Section 4.3)
- MUST NOT: Throw RuntimeException without wrapping (constraints.md Section 4.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/processor/InvalidTemperatureException.java` | CREATE | Temperature validation error |
| `src/main/java/org/example/etl2/batch/processor/InvalidDateTimeException.java` | CREATE | DateTime validation error |

### Acceptance Criteria
- [ ] `InvalidTemperatureException.java` exists
- [ ] `InvalidDateTimeException.java` exists
- [ ] Both extend `RuntimeException`
- [ ] Both have meaningful constructors
- [ ] Both compile without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/processor/InvalidTemperatureException.java`
2. Delete `src/main/java/org/example/etl2/batch/processor/InvalidDateTimeException.java`

---

## Task: task-4.5 - Create File Renaming Component

### Context
Processed CSV files are renamed to prevent reprocessing. Required by AC-1.6.

### Prerequisites
- [ ] task-4.1 is complete
- [ ] JobCompletionListener exists

### Instructions
1. Create file `ProcessedFileRenamer.java` in `src/main/java/org/example/etl2/batch/listener/`
2. Use `@Component` annotation
3. Implement method to rename processed files:
   - Accept list of Resource objects (processed files)
   - For each file, rename by adding `.processed` suffix
   - Example: `data.csv` → `data.csv.processed`
4. Handle errors gracefully (log but don't fail job)
5. Call from JobCompletionListener after successful job completion

### Constraints (from spec)
- MUST: Add `.processed` suffix to processed files (AC-1.6)
- MUST: Rename only after successful processing
- SHOULD: Log renaming operations

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/listener/ProcessedFileRenamer.java` | CREATE | File renaming after processing |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Renames files by adding `.processed` suffix
- [ ] Handles file renaming errors gracefully
- [ ] Logs renaming operations
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/main/java/org/example/etl2/batch/listener/ProcessedFileRenamer.java`

---

## Task: task-4.6 - Register Listeners with Job

### Context
All listeners must be registered with the job configuration to be invoked during execution.

### Prerequisites
- [ ] task-4.1 is complete
- [ ] task-4.2 is complete
- [ ] task-4.3 is complete
- [ ] task-4.5 is complete
- [ ] All listener components exist

### Instructions
1. Open `TemperatureImportJobConfig.java`
2. Inject listeners via constructor:
   - `JobCompletionListener jobCompletionListener`
   - `SkipItemListener skipItemListener`
   - `DuplicateLogWriter duplicateLogWriter`
   - `ProcessedFileRenamer processedFileRenamer`
3. Register `JobCompletionListener` with job:
   - `.listener(jobCompletionListener)`
4. Register `SkipItemListener` with step:
   - `.listener(skipItemListener)`
5. Wire `DuplicateLogWriter` and `ProcessedFileRenamer` through `JobCompletionListener`

### Constraints (from spec)
- MUST: Use constructor injection (constraints.md Section 2.4)
- MUST: Register listeners with appropriate job/step
- MUST NOT: Use field injection with @Autowired (constraints.md Section 2.4)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/org/example/etl2/batch/TemperatureImportJobConfig.java` | MODIFY | Register all listeners |

### Acceptance Criteria
- [ ] All listeners injected via constructor
- [ ] `JobCompletionListener` registered with job
- [ ] `SkipItemListener` registered with step
- [ ] All listeners invoked during job execution
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Revert changes to `TemperatureImportJobConfig.java`
2. Remove listener registrations

---

# Phase 5: Integration Testing

---

## Task: task-5.1 - Create Test CSV Files

### Context
Test data files are needed for all test scenarios. These files cover happy path, edge cases, and error conditions. Required by constraints.md Section 5.4.

### Prerequisites
- [ ] task-1.4 is complete
- [ ] Directory `src/test/resources/data/` exists

### Instructions
1. Create `valid_data.csv` with 10-20 valid records:
   ```csv
   name,datetime,temp
   Sensor1,2024-01-15T10:30:00,23.5
   Sensor2,2024-01-15T11:00:00,24.1
   ...
   ```
2. Create `empty_file.csv` with header only:
   ```csv
   name,datetime,temp
   ```
3. Create `with_duplicates.csv` with duplicate name+datetime pairs:
   ```csv
   name,datetime,temp
   Sensor1,2024-01-15T10:30:00,23.5
   Sensor1,2024-01-15T10:30:00,24.0
   Sensor2,2024-01-15T11:00:00,22.1
   ```
4. Create `malformed_data.csv` with invalid rows:
   ```csv
   name,datetime,temp
   Sensor1,invalid-date,23.5
   ,2024-01-15T10:30:00,23.5
   Sensor3,2024-01-15T12:00:00,not-a-number
   Sensor4,2024-01-15T13:00:00,25.0
   ```
5. Create `mixed_data.csv` combining valid, invalid, and duplicate records

### Constraints (from spec)
- MUST: Place files in `src/test/resources/data/` (constraints.md Section 5.4)
- MUST: Include files for each test scenario (constraints.md Section 5.4)
- SHOULD: Use realistic data patterns (constraints.md Section 5.4)
- SHOULD: Keep files under 100 records (constraints.md Section 5.4)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/resources/data/valid_data.csv` | CREATE | Happy path test data |
| `src/test/resources/data/empty_file.csv` | CREATE | Empty file test |
| `src/test/resources/data/with_duplicates.csv` | CREATE | Duplicate handling test |
| `src/test/resources/data/malformed_data.csv` | CREATE | Error handling test |
| `src/test/resources/data/mixed_data.csv` | CREATE | Combined scenario test |

### Acceptance Criteria
- [ ] All 5 CSV files exist in `src/test/resources/data/`
- [ ] `valid_data.csv` has header and valid data rows
- [ ] `empty_file.csv` has only header row
- [ ] `with_duplicates.csv` contains duplicate name+datetime pairs
- [ ] `malformed_data.csv` contains rows with invalid datetime, missing fields, invalid temp
- [ ] `mixed_data.csv` contains combination of all scenarios

### Rollback
If this task fails validation:
1. Delete created CSV files from `src/test/resources/data/`

---

## Task: task-5.2 - Configure Testcontainers Base Test

### Context
Base test configuration provides MySQL Testcontainer setup for all integration tests. Required by constraints.md Section 5.1.

### Prerequisites
- [ ] task-5.1 is complete
- [ ] Test CSV files exist

### Instructions
1. Create file `BaseIntegrationTest.java` in `src/test/java/org/example/etl2/batch/`
2. Annotate with:
   - `@SpringBootTest`
   - `@SpringBatchTest`
   - `@Testcontainers`
   - `@ActiveProfiles("test")`
3. Define MySQL container:
   ```java
   @Container
   static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
       .withDatabaseName("testdb")
       .withUsername("test")
       .withPassword("test");
   ```
4. Configure dynamic properties:
   ```java
   @DynamicPropertySource
   static void configureProperties(DynamicPropertyRegistry registry) {
       registry.add("spring.datasource.url", mysql::getJdbcUrl);
       registry.add("spring.datasource.username", mysql::getUsername);
       registry.add("spring.datasource.password", mysql::getPassword);
   }
   ```
5. Inject `JobLauncherTestUtils` for job execution
6. Inject `JdbcTemplate` for database verification

### Constraints (from spec)
- MUST: Use Testcontainers with MySQL (constraints.md Section 5.1)
- MUST: Use `@SpringBatchTest` annotation (constraints.md Section 5.1)
- MUST: Use `JobLauncherTestUtils` (constraints.md Section 5.1)
- MUST NOT: Use H2 or in-memory database (constraints.md Section 5.1)
- MUST NOT: Mock database interactions (constraints.md Section 5.1)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/BaseIntegrationTest.java` | CREATE | Base test configuration |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Uses `@SpringBootTest` and `@SpringBatchTest`
- [ ] Uses `@Testcontainers` annotation
- [ ] Defines `MySQLContainer` with version 8.0
- [ ] Configures dynamic datasource properties
- [ ] Injects `JobLauncherTestUtils`
- [ ] Compiles without errors

### Rollback
If this task fails validation:
1. Delete `src/test/java/org/example/etl2/batch/BaseIntegrationTest.java`

---

## Task: task-5.3 - Test Happy Path

### Context
Happy path test verifies valid CSV data is imported correctly. Required by constraints.md Section 5.3 and AC Happy Path scenario.

### Prerequisites
- [ ] task-5.2 is complete
- [ ] task-4.6 is complete
- [ ] Base test and all batch components are ready

### Instructions
1. Create file `TemperatureImportJobTest.java` in `src/test/java/org/example/etl2/batch/`
2. Extend `BaseIntegrationTest`
3. Create test method `testValidCsvImport()`:
   - Copy `valid_data.csv` to test input directory
   - Launch job using `jobLauncherTestUtils.launchJob()`
   - Assert job status is `BatchStatus.COMPLETED`
   - Query database for record count
   - Assert record count matches expected (number of data rows in CSV)
4. Use `@DisplayName` for descriptive test name
5. Use AssertJ for assertions

### Constraints (from spec)
- MUST: Test happy path (constraints.md Section 5.3)
- MUST: Verify job completion status is COMPLETED (constraints.md Section 5.5)
- MUST: Verify database record count (constraints.md Section 5.5)
- MUST: Use AssertJ for assertions (constraints.md Section 5.5)
- MUST NOT: Use `assertTrue(condition)` for specific assertions (constraints.md Section 5.5)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | CREATE | Integration tests |

### Acceptance Criteria
- [ ] File exists at specified path
- [ ] Extends `BaseIntegrationTest`
- [ ] Test method `testValidCsvImport()` exists
- [ ] Job completes with status COMPLETED
- [ ] Database contains expected number of records
- [ ] Uses AssertJ assertions
- [ ] Test passes

### Rollback
If this task fails validation:
1. Delete `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java`

---

## Task: task-5.4 - Test Duplicate Handling

### Context
Tests verify duplicate detection and handling per AC-3.1, AC-3.3, AC-3.4.

### Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

### Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testWithinFileDuplicates()`:
   - Use `with_duplicates.csv`
   - Assert job completes successfully
   - Assert first occurrence inserted, duplicates skipped
   - Assert duplicate count in summary
3. Add test method `testDatabaseDuplicates()`:
   - Pre-insert records to database
   - Run job with CSV containing same name+datetime
   - Assert records detected as duplicates
4. Add test method `testReprocessSameFile()`:
   - Run job with `valid_data.csv`
   - Run job again with same file
   - Assert all records detected as duplicates on second run

### Constraints (from spec)
- MUST: Test within-file duplicates (constraints.md Section 5.3)
- SHOULD: Test re-processing same file (constraints.md Section 5.3)
- MUST: Verify duplicates are skipped and counted (AC-3.1, AC-3.3, AC-3.4)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add duplicate tests |

### Acceptance Criteria
- [ ] Test `testWithinFileDuplicates()` exists and passes
- [ ] Test `testDatabaseDuplicates()` exists and passes
- [ ] Test `testReprocessSameFile()` exists and passes
- [ ] Duplicates are correctly detected and skipped
- [ ] Duplicate count is accurate in summary

### Rollback
If this task fails validation:
1. Remove duplicate test methods from `TemperatureImportJobTest.java`

---

## Task: task-5.5 - Test Error Handling

### Context
Tests verify malformed data is handled correctly per AC-5.1, AC-5.2, AC-5.3.

### Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

### Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testInvalidDatetime()`:
   - Use CSV with invalid datetime format
   - Assert row is skipped
   - Assert job continues and completes
3. Add test method `testMissingRequiredField()`:
   - Use CSV with missing name/datetime/temp
   - Assert row is skipped
   - Assert job continues and completes
4. Add test method `testInvalidTemperature()`:
   - Use CSV with non-numeric temperature
   - Assert row is skipped
   - Assert job continues and completes
5. Add test method `testMalformedDataFile()`:
   - Use `malformed_data.csv`
   - Assert job completes
   - Assert valid rows processed, invalid rows skipped
   - Assert error count matches expected

### Constraints (from spec)
- MUST: Test malformed data scenarios (constraints.md Section 5.3)
- MUST: Verify malformed rows skipped, job continues (AC-5.5)
- MUST: Verify error count accuracy

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add error handling tests |

### Acceptance Criteria
- [ ] Test `testInvalidDatetime()` exists and passes
- [ ] Test `testMissingRequiredField()` exists and passes
- [ ] Test `testInvalidTemperature()` exists and passes
- [ ] Test `testMalformedDataFile()` exists and passes
- [ ] Malformed rows are skipped
- [ ] Job completes despite errors
- [ ] Error count is accurate

### Rollback
If this task fails validation:
1. Remove error handling test methods from `TemperatureImportJobTest.java`

---

## Task: task-5.6 - Test Edge Cases

### Context
Tests cover edge cases: empty file, mixed data, summary formula validation. Required by AC-1.4 and AC-6.5.

### Prerequisites
- [ ] task-5.3 is complete
- [ ] `TemperatureImportJobTest.java` exists

### Instructions
1. Open `TemperatureImportJobTest.java`
2. Add test method `testEmptyFile()`:
   - Use `empty_file.csv`
   - Assert job completes with status COMPLETED
   - Assert 0 records processed, 0 inserts, 0 duplicates, 0 errors
3. Add test method `testMixedData()`:
   - Use `mixed_data.csv`
   - Assert job completes
   - Assert correct counts for each category (valid, duplicate, error)
4. Add test method `testSummaryFormulaValidation()`:
   - Use any test data
   - Extract summary counts
   - Assert: processed = inserts + duplicates + errors (AC-6.5)

### Constraints (from spec)
- MUST: Test empty file (constraints.md Section 5.3)
- MUST: Verify summary formula (AC-6.5)
- SHOULD: Test mixed valid/invalid/duplicate data (constraints.md Section 5.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/TemperatureImportJobTest.java` | MODIFY | Add edge case tests |

### Acceptance Criteria
- [ ] Test `testEmptyFile()` exists and passes
- [ ] Test `testMixedData()` exists and passes
- [ ] Test `testSummaryFormulaValidation()` exists and passes
- [ ] Empty file job completes with zero counts
- [ ] Summary formula validated: processed = inserts + duplicates + errors

### Rollback
If this task fails validation:
1. Remove edge case test methods from `TemperatureImportJobTest.java`

---

## Task: task-5.7 - Verify All Tests Pass

### Context
Final verification ensures all tests pass and implementation is complete.

### Prerequisites
- [ ] task-5.3 is complete
- [ ] task-5.4 is complete
- [ ] task-5.5 is complete
- [ ] task-5.6 is complete
- [ ] All test methods are implemented

### Instructions
1. Run complete test suite:
   ```bash
   ./mvnw test
   ```
2. Verify all tests pass
3. Check for any failures or errors
4. If failures exist, investigate and fix:
   - Check test data
   - Check assertions
   - Check implementation code
5. Re-run tests until all pass
6. Generate test report (optional)

### Constraints (from spec)
- MUST: All tests pass (constraints.md Section 5.5)
- MUST NOT: Ignore failing tests (constraints.md Section 5.5)
- SHOULD: Achieve at least 80% code coverage (constraints.md Section 5.3)

### Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| N/A | N/A | Test execution only |

### Acceptance Criteria
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] No test failures or errors
- [ ] Tests cover all acceptance criteria scenarios
- [ ] Summary formula verified in tests

### Rollback
If this task fails validation:
1. Identify failing tests
2. Analyze test failures
3. Fix implementation or test code as needed
4. Re-run verification

---

# Checkpoint Summary

After completing all phases, verify:

1. **Phase 1 Checkpoint:**
   - Package structure matches constraints.md Section 1.1
   - Flyway migration creates correct table schema
   - Application properties configured correctly

2. **Phase 2 Checkpoint:**
   - TemperatureReading uses Java record syntax
   - No JPA/Hibernate annotations
   - FieldSetMapper handles ISO-8601 datetime
   - Writer uses INSERT IGNORE

3. **Phase 3 Checkpoint:**
   - Job named `temperatureImportJob`
   - Step named `importStep`
   - Chunk size is 1000
   - Skip policy configured

4. **Phase 4 Checkpoint:**
   - Job summary logs all metrics
   - Skipped records logged with full details
   - Duplicate log file created correctly
   - Processed files renamed

5. **Phase 5 Checkpoint:**
   - All test scenarios from acceptance_criteria.md covered
   - Testcontainers with MySQL used
   - All tests pass
   - Summary formula validated
