# Acceptance Criteria: Temperature Data Import Batch Job

## Overview
This document defines the formal acceptance criteria for the Temperature Data Import Batch Job feature using WHEN-THEN-SHALL format.

### Requirements Traceability
This acceptance criteria document traces to the requirements defined in [requirements.md](requirements.md), specifically:
- Section 6.1: File Processing
- Section 6.2: Data Model
- Section 6.3: Database Schema
- Section 6.4: Duplicate Handling
- Section 6.5: Error Handling
- Section 6.6: Summary Report

---

## 1. CSV Parsing
*Traces to: [REQ 6.1 File Processing](requirements.md#61-file-processing)*

### AC-1.1: Valid CSV File Processing
- **WHEN** a CSV file exists in the configured input directory
- **THEN** the batch job reads the file
- **SHALL** parse comma-delimited records with UTF-8 encoding and skip the header row

### AC-1.2: Header Row Recognition
- **WHEN** a CSV file contains the header row with columns `name`, `datetime`, `temp`
- **THEN** the batch job processes the file
- **SHALL** correctly map each column to the corresponding data field

### AC-1.3: Extra Columns Ignored
- **WHEN** a CSV file contains additional columns beyond `name`, `datetime`, `temp`
- **THEN** the batch job processes the file
- **SHALL** ignore extra columns and extract only the required fields

### AC-1.4: Empty CSV File
- **WHEN** a CSV file contains only a header row (no data rows)
- **THEN** the batch job processes the file
- **SHALL** complete successfully with zero records processed

### AC-1.5: Large File Processing
- **WHEN** a CSV file contains up to 100,000 rows
- **THEN** the batch job processes the file using chunk size of 1000 records
- **SHALL** complete processing without memory exhaustion

### AC-1.6: Processed File Renaming
- **WHEN** a CSV file has been successfully processed
- **THEN** the batch job renames the file
- **SHALL** add `.processed` suffix to the filename to prevent reprocessing

---

## 2. Data Transformation
*Traces to: [REQ 6.1 File Processing](requirements.md#61-file-processing), [REQ 6.2 Data Model](requirements.md#62-data-model)*

### AC-2.1: Datetime Parsing
- **WHEN** a CSV row contains a datetime value in ISO-8601 format (yyyy-MM-dd'T'HH:mm:ss)
- **THEN** the batch job parses the value
- **SHALL** convert it to a LocalDateTime object

### AC-2.2: Temperature Precision
- **WHEN** a CSV row contains a temperature value
- **THEN** the batch job processes the value
- **SHALL** store it as a decimal with 1 decimal place precision

### AC-2.3: Name Field Handling
- **WHEN** a CSV row contains a name value with special characters or unicode
- **THEN** the batch job processes the value
- **SHALL** preserve the exact string value without modification

---

## 3. Duplicate Detection
*Traces to: [REQ 6.4 Duplicate Handling](requirements.md#64-duplicate-handling)*

### AC-3.1: Database-Level Duplicate Check
- **WHEN** a CSV row has the same `name` and `datetime` combination as an existing database record
- **THEN** the batch job attempts to insert the record
- **SHALL** skip the duplicate without causing an error

### AC-3.2: Duplicate Logging
- **WHEN** a duplicate record is detected
- **THEN** the batch job logs the duplicate
- **SHALL** write full details (name, datetime, temp) to a separate duplicate log file

### AC-3.3: Within-File Duplicates
- **WHEN** a CSV file contains multiple rows with the same `name` and `datetime` combination
- **THEN** the batch job processes the file
- **SHALL** insert the first occurrence and skip subsequent duplicates

### AC-3.4: Re-processing Same File
- **WHEN** a file is processed that was already imported previously (same data exists in database)
- **THEN** the batch job processes the file
- **SHALL** detect all records as duplicates and report them in the summary

---

## 4. Database Operations
*Traces to: [REQ 6.3 Database Schema](requirements.md#63-database-schema)*

### AC-4.1: Table Creation via Flyway
- **WHEN** the application starts
- **THEN** Flyway migration runs
- **SHALL** create the `temperature_data` table with appropriate schema (id, name, datetime, temp, unique constraint on name+datetime)

### AC-4.2: Successful Record Insertion
- **WHEN** a valid, non-duplicate CSV row is processed
- **THEN** the batch job writes to the database
- **SHALL** insert a new record into the `temperature_data` table

### AC-4.3: Unique Constraint Enforcement
- **WHEN** the database table is created
- **THEN** the schema is applied
- **SHALL** include a composite unique constraint on (`name`, `datetime`) columns

### AC-4.4: Chunk-Based Transaction
- **WHEN** records are written to the database
- **THEN** the batch job commits in chunks of 1000 records
- **SHALL** maintain transactional integrity within each chunk

---

## 5. Error Handling
*Traces to: [REQ 6.5 Error Handling](requirements.md#65-error-handling)*

### AC-5.1: Malformed Row - Invalid Datetime
- **WHEN** a CSV row contains an invalid datetime format (not ISO-8601)
- **THEN** the batch job encounters the row
- **SHALL** skip the row, log the error with row details, and continue processing

### AC-5.2: Malformed Row - Missing Required Field
- **WHEN** a CSV row is missing a required field (name, datetime, or temp)
- **THEN** the batch job encounters the row
- **SHALL** skip the row, log the error with row details, and continue processing

### AC-5.3: Malformed Row - Invalid Temperature
- **WHEN** a CSV row contains a non-numeric temperature value
- **THEN** the batch job encounters the row
- **SHALL** skip the row, log the error with row details, and continue processing

### AC-5.4: Error Logging Format
- **WHEN** a malformed row is encountered
- **THEN** the batch job logs the error
- **SHALL** include the line number, raw row content, and specific error reason

### AC-5.5: Job Continuation After Errors
- **WHEN** multiple malformed rows exist in a CSV file
- **THEN** the batch job processes all rows
- **SHALL** skip all malformed rows and successfully process all valid rows

---

## 6. Summary Reporting
*Traces to: [REQ 6.6 Summary Report](requirements.md#66-summary-report)*

### AC-6.1: Console Output Summary
- **WHEN** the batch job completes
- **THEN** a summary is printed to the console
- **SHALL** display: total records processed, successful inserts, duplicates count, and error count

### AC-6.2: Processed Count Accuracy
- **WHEN** the batch job completes
- **THEN** the summary reports the processed count
- **SHALL** equal the total number of data rows read from the CSV file (excluding header)

### AC-6.3: Duplicates Count in Summary
- **WHEN** the batch job completes with duplicate records
- **THEN** the summary reports the duplicates count
- **SHALL** equal the number of records skipped due to duplicate detection

### AC-6.4: Errors Count Accuracy
- **WHEN** the batch job completes with malformed rows
- **THEN** the summary reports the errors count
- **SHALL** equal the number of rows skipped due to parsing or validation errors

### AC-6.5: Summary Formula Validation
- **WHEN** the batch job completes
- **THEN** the summary counts are verified
- **SHALL** satisfy: processed = successful_inserts + duplicates + errors

---

## Test Scenarios Matrix

| Scenario | Category | Expected Outcome |
|----------|----------|------------------|
| Valid file with 100 records | Happy Path | 100 successful inserts |
| Empty file (header only) | Edge Case | 0 processed, job completes |
| File with 5 duplicate entries | Duplicates | 5 duplicates logged, details in separate file |
| File with 3 malformed rows | Errors | 3 errors logged, valid rows processed |
| Large file (100K rows) | Scale | Completes within reasonable time |
| File with mixed valid/invalid/duplicate | Combined | Correct counts for each category |
| Re-run job on same file | Idempotency | All records detected as duplicates |
