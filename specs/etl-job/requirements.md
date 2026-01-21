# Requirements Analysis: Temperature Data Import Batch Job

## Feature Request Summary
Implement a Spring Batch job with JDBC to import temperature data from CSV files into a MySQL database using Java 21 features, Java records, and Testcontainers for integration testing.

### Related Documents
- [Acceptance Criteria](acceptance_criteria.md) - Formal acceptance criteria with WHEN-THEN-SHALL format
- [Technical Constraints](constraints.md) - Implementation constraints and guidelines for the coding agent

---

## 1. AMBIGUITIES

### 1.1 CSV File Location and Discovery
- **Unclear:** "import temperature data from csv files" - Does the job process a single file, multiple files, or all files in a directory?
- **Why it matters:** This affects the job architecture - whether we need a multi-file reader, file discovery logic, or parameterized job execution.

### 1.2 Summary Output Destination
- **Unclear:** "Print the summary, how many" - Should the summary be printed to console/logs, stored in database, or both?
- **Why it matters:** Determines where to implement the reporting logic (JobExecutionListener, dedicated table, or logging framework).

### 1.3 Duplicate Handling Scope
- **Unclear:** Are duplicates detected within a single file, across multiple files in one job run, or against existing database records?
- **Why it matters:** Significantly impacts the implementation - in-memory deduplication vs. database constraint handling.

### 1.4 "datetime" Column Format
- **Unclear:** What is the expected format of the datetime column in the CSV (ISO-8601, Unix timestamp, custom format)?
- **Why it matters:** Requires proper parsing configuration in the ItemReader.

---

## 2. MISSING INFORMATION

### 2.1 Database Schema
- **Missing:** Target table name and column definitions (data types, constraints, nullable fields)
- **Needed for:** Flyway migration scripts and entity mapping

### 2.2 CSV File Structure
- **Missing:** 
  - Expected CSV delimiter (comma, semicolon, tab)
  - Header row presence
  - Character encoding
  - Sample data or column order
- **Needed for:** FlatFileItemReader configuration

### 2.3 Temperature Data Type and Unit
- **Missing:**
  - Data type for temperature (integer, decimal, precision)
  - Temperature unit (Celsius, Fahrenheit, Kelvin)
- **Needed for:** Database column type and potential validation

### 2.4 Error Handling Strategy
- **Missing:** 
  - What happens on malformed rows (skip, fail job, log)?
  - Chunk size for batch processing
  - Skip limit for bad records
- **Needed for:** Step configuration and fault tolerance setup

### 2.5 Job Triggering Mechanism
- **Missing:** How will the job be triggered (on startup, scheduled, REST endpoint, command line)?
- **Needed for:** Application configuration and entry point design

### 2.6 File Processing Tracking
- **Missing:** Should processed files be moved, renamed, or tracked to prevent reprocessing?
- **Needed for:** Operational reliability and idempotency

---

## 3. IMPLICIT ASSUMPTIONS

### 3.1 Technology Stack
- **Assumed:** Spring Boot application (based on HELP.md references)
- **Assumed:** Maven build tool (pom.xml present)
- **Assumed:** Flyway for database migrations (mentioned in HELP.md)

### 3.2 Data Model
- **Assumed:** Each CSV row represents one temperature reading
- **Assumed:** "name" refers to a location/sensor name (string type)
- **Assumed:** "temp" is a numeric temperature value

### 3.3 Uniqueness Constraint
- **Assumed:** The unique constraint (name + datetime) should be enforced at database level
- **Assumed:** "Duplicate entries should be ignored" means INSERT should not fail, just skip

### 3.4 Testing Requirements
- **Assumed:** Integration tests should cover the full job execution flow
- **Assumed:** MySQL Testcontainer should match production MySQL version

### 3.5 Job Behavior
- **Assumed:** Job should be restartable in case of failure
- **Assumed:** Job runs to completion (not streaming/continuous)

---

## 4. EDGE CASES

### 4.1 Data Quality
- Empty CSV file (header only or completely empty)
- CSV with missing required columns (name, datetime, or temp)
- Null or empty values in required fields
- Temperature values outside reasonable range (e.g., -500°C or 10000°C)
- Special characters or unicode in "name" field
- Invalid datetime formats or future dates

### 4.2 Duplicate Scenarios
- Exact duplicate rows within the same CSV file
- Same name+datetime with different temperature values (data conflict)
- Re-running the job with the same file (idempotency)

### 4.3 File Handling
- Very large CSV files (memory considerations)
- CSV file locked by another process
- File disappears during processing
- File with BOM (Byte Order Mark)

### 4.4 Database Scenarios
- Database connection lost mid-processing
- Transaction timeout on large batches
- Concurrent job executions with overlapping data

### 4.5 Job Execution
- Job already running when triggered again
- Partial failure and restart scenarios

---

## 5. CLARIFYING QUESTIONS (ANSWERED)

| # | Question | Answer |
|---|----------|--------|
| Q1 | CSV File Source Configuration | A configurable directory path where all CSV files are processed. Processed files should be renamed to avoid reprocessing. |
| Q2 | Duplicate Detection Scope | Check against all records already in the database |
| Q3 | Datetime Format Specification | ISO-8601 (e.g., `2024-01-15T14:30:00`) |
| Q4 | Summary Content Requirements | Total records processed, successful inserts, duplicates ignored |
| Q5 | Error Handling for Malformed Data | Skip the row and continue (log the error) |
| Q6 | Database Table Name | `temperature_data` |
| Q7 | Temperature Data Precision | Decimal with 1 decimal place (e.g., 23.5) |
| Q8 | Job Trigger Mechanism | Automatically on application startup |

---

## 6. CONSOLIDATED SPECIFICATION

Based on the answers provided, here is the consolidated specification for implementation:

### 6.1 File Processing
- **Input:** Configurable directory path (e.g., `csv.input.directory` property)
- **File Discovery:** Process all `.csv` files in the configured directory
- **Post-Processing:** Rename processed files (e.g., add `.processed` suffix) to prevent reprocessing
- **CSV Format:**
  - Standard comma-delimited CSV
  - Header row expected with columns: `name`, `datetime`, `temp` (other columns ignored)
  - UTF-8 encoding

### 6.2 Data Model
```java
// Java Record for CSV data
public record TemperatureReading(
    String name,           // Location/sensor name
    LocalDateTime datetime, // ISO-8601 format
    BigDecimal temp        // Temperature with 1 decimal precision
) {}
```

### 6.3 Database Schema
- **Table Name:** `temperature_data`
- **Columns:**
  | Column | Type | Constraints |
  |--------|------|-------------|
  | id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
  | name | VARCHAR(255) | NOT NULL |
  | datetime | DATETIME | NOT NULL |
  | temp | DECIMAL(5,1) | NOT NULL |
- **Unique Constraint:** `(name, datetime)` - composite unique index

### 6.4 Duplicate Handling
- Check against existing database records using the unique constraint `(name, datetime)`
- Use `INSERT IGNORE` or equivalent to skip duplicates silently
- Track and report the count of duplicates ignored
- Write duplicate record details (name, datetime, temp) to a separate duplicate log file

### 6.5 Error Handling
- **Malformed Rows:** Skip and log the error (continue processing)
- **Skip Limit:** Configurable (default: unlimited skips)
- **Chunk Size:** Configurable (default: 1000 records per transaction)

### 6.6 Summary Report
At job completion, log the following metrics:
- Total records read from CSV files
- Successful inserts to database
- Duplicates ignored (detected by database constraint)
- Rows skipped due to errors (malformed data)

### 6.7 Job Configuration
- **Trigger:** Automatically on application startup
- **Restartability:** Job should be restartable
- **Concurrency:** Single job instance at a time

### 6.8 Testing Requirements
- Use Testcontainers with MySQL (no H2)
- Test scenarios:
  - Happy path: valid CSV data imported successfully
  - Duplicates: same name+datetime pairs are ignored
  - Malformed data: invalid rows are skipped, job continues
  - Empty file: job completes successfully with zero inserts
  - Multiple files: all CSV files in directory are processed

---

## Technical Notes

### Java 21 Features to Consider
- Records for data transfer (as specified)
- Pattern matching for switch (if applicable)
- Virtual threads (if beneficial for I/O operations)
- Sequenced collections (if needed)

### Spring Batch Components Required
- `FlatFileItemReader<T>` - CSV reading
- `ItemProcessor<I, O>` - Validation and transformation
- `JdbcBatchItemWriter<T>` - Database writing
- `JobExecutionListener` - Summary reporting
- `SkipListener` - Duplicate/error tracking

### Testing Approach
- Testcontainers with MySQL for integration tests
- Test scenarios: happy path, duplicates, malformed data, empty file
- Verify job completion status and database state
