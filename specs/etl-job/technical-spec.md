# Technical Constraints: Temperature Data Import Batch Job

This document defines technical constraints for implementing the Temperature Data Import Batch Job.
The AI coding agent MUST validate its implementation against each constraint.

## Related Documentation
- [Requirements Specification](requirements.md)
- [Acceptance Criteria](acceptance_criteria.md)

---

## 1. Project Structure

### 1.1 Package Organization

**MUST:**
- Use base package: `org.example.etl2`
- Organize code into the following sub-packages:
  - `org.example.etl2.batch` - Spring Batch job configuration
  - `org.example.etl2.batch.listener` - Job and step listeners
  - `org.example.etl2.batch.processor` - ItemProcessor implementations
  - `org.example.etl2.batch.reader` - ItemReader configurations
  - `org.example.etl2.batch.writer` - ItemWriter implementations
  - `org.example.etl2.model` - Java records and domain objects
  - `org.example.etl2.config` - Application configuration classes

**SHOULD:**
- Keep each class focused on a single responsibility
- Limit classes to 200 lines of code (excluding imports and comments)

**MUST NOT:**
- Place all batch components in a single configuration class
- Create deeply nested package structures beyond 4 levels

### 1.2 Resource Organization

**MUST:**
- Place Flyway migrations in `src/main/resources/db/migration/`
- Name migration files following pattern: `V{version}__{description}.sql` (e.g., `V1__create_temperature_data_table.sql`)
- Place test CSV files in `src/test/resources/data/`
- Place application configuration in `src/main/resources/application.properties`

**SHOULD:**
- Use `application-test.properties` for test-specific configuration

---

## 2. Component Design

### 2.1 Data Model

**MUST:**
- Use Java record for CSV data representation:
  ```java
  public record TemperatureReading(
      String name,
      LocalDateTime datetime,
      BigDecimal temp
  ) {}
  ```
- Use `BigDecimal` for temperature values (not `double` or `float`)
- Use `LocalDateTime` for datetime values (not `Date` or `String`)

**MUST NOT:**
- Use JPA entities or Hibernate annotations
- Create mutable DTOs with setters

### 2.2 Spring Batch Job Structure

**MUST:**
- Define exactly one Job named `temperatureImportJob`
- Define exactly one Step named `importStep`
- Use chunk-oriented processing with chunk size of 1000 (as per [AC-1.5](acceptance_criteria.md#ac-15-large-file-processing), [AC-4.4](acceptance_criteria.md#ac-44-chunk-based-transaction))
- Implement `FlatFileItemReader` for CSV reading
- Implement `JdbcBatchItemWriter` for database writing
- Implement `ItemProcessor` for validation and transformation

**SHOULD:**
- Use `StepBuilderFactory` and `JobBuilderFactory` (or their Spring Batch 5.x equivalents)
- Configure fault tolerance with skip policy for malformed records

**MUST NOT:**
- Use tasklet-based steps for data processing
- Implement custom threading or parallel processing without explicit requirement

### 2.3 Listeners

**MUST:**
- Implement `JobExecutionListener` for summary reporting (traces to [AC-6.1](acceptance_criteria.md#ac-61-console-output-summary))
- Implement `SkipListener` for tracking skipped records (traces to [AC-3.2](acceptance_criteria.md#ac-32-duplicate-logging), [AC-5.4](acceptance_criteria.md#ac-54-error-logging-format))

**SHOULD:**
- Use `@BeforeJob` and `@AfterJob` annotations for listener methods
- Implement `ItemReadListener` for tracking read errors

### 2.4 Configuration Classes

**MUST:**
- Annotate batch configuration class with `@Configuration` and `@EnableBatchProcessing`
- Use `@Bean` methods to define all batch components
- Inject dependencies via constructor injection

**MUST NOT:**
- Use field injection with `@Autowired`
- Define beans in the main application class

---

## 3. Technology Decisions

### 3.1 Database Access

**MUST:**
- Use Spring JDBC (`JdbcTemplate`, `NamedParameterJdbcTemplate`) for database operations
- Use Flyway for database schema management
- Use MySQL-specific syntax: `INSERT IGNORE` for duplicate handling (traces to [AC-3.1](acceptance_criteria.md#ac-31-database-level-duplicate-check))

**MUST NOT:**
- Use JPA, Hibernate, or Spring Data JPA
- Use H2 or any in-memory database (even for tests)
- Execute raw SQL strings without parameterization

### 3.2 CSV Processing

**MUST:**
- Use Spring Batch's `FlatFileItemReader` with `DefaultLineMapper`
- Use `DelimitedLineTokenizer` with comma delimiter
- Use `BeanWrapperFieldSetMapper` or custom `FieldSetMapper` for record mapping
- Configure reader to skip header row (traces to [AC-1.1](acceptance_criteria.md#ac-11-valid-csv-file-processing))
- Support UTF-8 encoding (traces to [AC-1.1](acceptance_criteria.md#ac-11-valid-csv-file-processing))

**SHOULD:**
- Use `MultiResourceItemReader` if processing multiple files

**MUST NOT:**
- Use third-party CSV libraries (OpenCSV, Apache Commons CSV) for primary parsing
- Load entire file into memory

### 3.3 Date/Time Handling

**MUST:**
- Parse datetime using ISO-8601 format: `yyyy-MM-dd'T'HH:mm:ss` (traces to [AC-2.1](acceptance_criteria.md#ac-21-datetime-parsing))
- Use `java.time.LocalDateTime` for datetime representation
- Use `java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME` for parsing

**MUST NOT:**
- Use `java.util.Date` or `java.sql.Timestamp`
- Use string representation for datetime in domain objects

### 3.4 Configuration Properties

**MUST:**
- Define configurable input directory path: `batch.input.directory`
- Define configurable chunk size: `batch.chunk-size` (default: 1000)
- Use `@ConfigurationProperties` or `@Value` for property binding

**SHOULD:**
- Use `@ConfigurationProperties` with a dedicated properties class
- Provide sensible defaults for all optional properties

---

## 4. Code Style

### 4.1 Naming Conventions

**MUST:**
- Use `PascalCase` for class names: `TemperatureReading`, `TemperatureImportJobConfig`
- Use `camelCase` for method and variable names: `processTemperature`, `inputDirectory`
- Use `SCREAMING_SNAKE_CASE` for constants: `CHUNK_SIZE`, `DATE_TIME_PATTERN`
- Prefix configuration classes with component name: `TemperatureImportJobConfig`, `BatchConfig`
- Suffix listeners with `Listener`: `JobCompletionListener`, `SkipItemListener`

**SHOULD:**
- Use descriptive names that indicate purpose
- Keep method names under 30 characters

### 4.2 Java 21 Features

**MUST:**
- Use Java records for immutable data classes
- Use `var` for local variables when type is obvious from context

**SHOULD:**
- Use pattern matching for `instanceof` checks
- Use text blocks for multi-line SQL statements
- Consider virtual threads for I/O-bound operations (if applicable)

**MUST NOT:**
- Use deprecated APIs
- Use raw types (e.g., `List` instead of `List<TemperatureReading>`)

### 4.3 Error Handling

**MUST:**
- Use specific exception types, not generic `Exception`
- Log exceptions with full context (line number, raw content, error reason) (traces to [AC-5.4](acceptance_criteria.md#ac-54-error-logging-format))
- Never swallow exceptions silently

**SHOULD:**
- Create custom exceptions for domain-specific errors: `InvalidTemperatureException`, `InvalidDateTimeException`
- Use `@Slf4j` or constructor-injected `Logger`

**MUST NOT:**
- Use `System.out.println` for logging
- Throw `RuntimeException` without wrapping in domain exception
- Catch `Throwable` or `Error`

### 4.4 Documentation

**SHOULD:**
- Add Javadoc to public classes and methods
- Document configuration properties in `application.properties` with comments

**MUST NOT:**
- Leave TODO comments in final implementation
- Include commented-out code

---

## 5. Testing Strategy

### 5.1 Test Framework

**MUST:**
- Use JUnit 5 for all tests
- Use Testcontainers with MySQL for integration tests (traces to [requirements.md Section 6.8](requirements.md#68-testing-requirements))
- Use `@SpringBatchTest` annotation for batch job tests
- Use `JobLauncherTestUtils` for job execution in tests

**MUST NOT:**
- Use H2 or any in-memory database
- Mock database interactions in integration tests
- Use `@DirtiesContext` unless absolutely necessary

### 5.2 Test Organization

**MUST:**
- Place tests in `src/test/java/org/example/etl2/`
- Mirror main source package structure in test packages
- Name test classes with `Test` suffix: `TemperatureImportJobTest`

**SHOULD:**
- Separate unit tests from integration tests using naming convention or directories
- Use `@DisplayName` for descriptive test names

### 5.3 Test Coverage

**MUST:**
- Test happy path: valid CSV data imported successfully (traces to [Test Scenarios Matrix](acceptance_criteria.md#test-scenarios-matrix))
- Test duplicate handling: same name+datetime pairs are ignored (traces to [AC-3.1](acceptance_criteria.md#ac-31-database-level-duplicate-check))
- Test malformed data: invalid rows are skipped, job continues (traces to [AC-5.1](acceptance_criteria.md#ac-51-malformed-row---invalid-datetime), [AC-5.2](acceptance_criteria.md#ac-52-malformed-row---missing-required-field), [AC-5.3](acceptance_criteria.md#ac-53-malformed-row---invalid-temperature))
- Test empty file: job completes successfully with zero inserts (traces to [AC-1.4](acceptance_criteria.md#ac-14-empty-csv-file))
- Test summary report accuracy (traces to [AC-6.5](acceptance_criteria.md#ac-65-summary-formula-validation))

**SHOULD:**
- Test large file processing with at least 1000 records
- Test within-file duplicate detection (traces to [AC-3.3](acceptance_criteria.md#ac-33-within-file-duplicates))
- Test re-processing same file (idempotency) (traces to [AC-3.4](acceptance_criteria.md#ac-34-re-processing-same-file))
- Achieve at least 80% code coverage

### 5.4 Test Data

**MUST:**
- Create test CSV files in `src/test/resources/data/`
- Include test files for each test scenario:
  - `valid_data.csv` - Standard valid records
  - `empty_file.csv` - Header only
  - `with_duplicates.csv` - Contains duplicate name+datetime combinations
  - `malformed_data.csv` - Contains invalid rows
  - `mixed_data.csv` - Combination of valid, invalid, and duplicate records

**SHOULD:**
- Use realistic data patterns in test files
- Keep test files small (under 100 records) for fast test execution

### 5.5 Test Assertions

**MUST:**
- Verify job completion status is `COMPLETED`
- Verify database record count matches expected successful inserts
- Verify summary report counts (processed, inserted, duplicates, errors)
- Use AssertJ for fluent assertions

**MUST NOT:**
- Use `assertTrue(condition)` when more specific assertions exist
- Ignore assertion failures by catching exceptions

---

## 6. Database Schema

### 6.1 Table Definition

**MUST:**
- Create table named `temperature_data` (traces to [requirements.md Section 6.3](requirements.md#63-database-schema))
- Define schema via Flyway migration:
  ```sql
  CREATE TABLE temperature_data (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      datetime DATETIME NOT NULL,
      temp DECIMAL(5,1) NOT NULL,
      UNIQUE KEY uk_name_datetime (name, datetime)
  );
  ```

**MUST NOT:**
- Use Hibernate auto-DDL (`spring.jpa.hibernate.ddl-auto`)
- Create schema programmatically in application code

### 6.2 Spring Batch Metadata

**MUST:**
- Allow Spring Batch to create its metadata tables automatically
- Use separate schema or table prefix if needed to distinguish from application tables

---

## 7. Logging and Monitoring

### 7.1 Logging Requirements

**MUST:**
- Log job start and completion with job parameters
- Log summary statistics after job completion (traces to [AC-6.1](acceptance_criteria.md#ac-61-console-output-summary)):
  - Total records processed
  - Successful inserts
  - Duplicates count
  - Errors count
- Log each skipped record with details (traces to [AC-5.4](acceptance_criteria.md#ac-54-error-logging-format)):
  - Line number
  - Raw content
  - Error reason
- Write duplicate details to separate log file (traces to [AC-3.2](acceptance_criteria.md#ac-32-duplicate-logging))

### 7.2 Duplicate Log File Specification

**MUST:**
- Write duplicate records to a separate log file
- **File Location:** `logs/` directory relative to application working directory
- **File Name Pattern:** `duplicates-{timestamp}.log` where `{timestamp}` is job start time in format `yyyyMMdd-HHmmss`
- **File Format:** Plain text, one line per duplicate record
- **Line Format:** `{timestamp}|{name}|{datetime}|{temp}` where:
  - `{timestamp}` - ISO-8601 timestamp when duplicate was detected
  - `{name}` - the name field value from CSV
  - `{datetime}` - the datetime field value from CSV (ISO-8601 format)
  - `{temp}` - the temperature field value from CSV
- Create a new log file for each job execution

**SHOULD:**
- Include a header line in the duplicate log file: `# Duplicate records detected during job execution {jobId}`
- Flush writes to ensure data is persisted even if job fails

**MUST NOT:**
- Overwrite existing duplicate log files from previous runs

---

### 7.3 General Logging Configuration

**SHOULD:**
- Use SLF4J with Logback
- Configure appropriate log levels (INFO for summary, DEBUG for details)

**MUST NOT:**
- Log sensitive data
- Use excessive logging that impacts performance

---

## 8. Build and Dependencies

### 8.1 Maven Configuration

**MUST:**
- Use existing dependencies from `pom.xml`
- Do not add new dependencies without explicit requirement

**MUST NOT:**
- Add Lombok or other annotation processors
- Add JPA/Hibernate dependencies
- Change Java version from 21

---

## Validation Checklist

Use this checklist to validate the implementation:

- [ ] Package structure follows defined organization
- [ ] Java records used for data model
- [ ] Spring Batch job configured with correct name and chunk size
- [ ] FlatFileItemReader configured for CSV parsing
- [ ] JdbcBatchItemWriter used with INSERT IGNORE
- [ ] Flyway migration creates temperature_data table
- [ ] JobExecutionListener reports summary statistics
- [ ] SkipListener logs skipped records with full details
- [ ] Duplicate details written to separate log file
- [ ] All required test scenarios covered
- [ ] Testcontainers with MySQL used for integration tests
- [ ] No H2 or in-memory database usage
- [ ] No JPA/Hibernate usage
- [ ] All tests pass
