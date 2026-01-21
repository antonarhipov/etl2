# Temperature Data Import ETL Application

A Spring Batch application that imports temperature data from CSV files into a MySQL database.

## Overview

This application reads temperature readings from CSV files in a configurable input directory, validates and processes the data, and stores it in a MySQL database. The job runs automatically on application startup.

### Features

- **CSV Import**: Processes all `.csv` files from a configurable input directory
- **Data Validation**: Validates datetime format (ISO-8601) and temperature values
- **Duplicate Handling**: Ignores duplicate records based on `name + datetime` composite key
- **Error Handling**: Skips malformed rows and continues processing
- **Summary Reporting**: Logs statistics including records processed, inserted, duplicates, and errors
- **File Tracking**: Renames processed files to prevent reprocessing

## Application Workflow

The ETL application follows a Spring Batch chunk-oriented processing pattern. Here's how data flows through the system:

### Workflow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           APPLICATION STARTUP                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  1. INITIALIZATION                                                          │
│     • Spring Boot starts application                                        │
│     • Flyway runs database migrations (creates temperature_data table)      │
│     • Spring Batch job (temperatureImportJob) is triggered automatically    │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  2. FILE DISCOVERY                                                          │
│     • Scan configured input directory (batch.input.directory)               │
│     • Find all *.csv files                                                  │
│     • Queue files for processing                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  3. CHUNK-BASED PROCESSING (for each file)                                  │
│     ┌─────────────────────────────────────────────────────────────────┐     │
│     │  READ (FlatFileItemReader)                                      │     │
│     │  • Parse CSV line by line                                       │     │
│     │  • Skip header row                                              │     │
│     │  • Map to TemperatureReading record                             │     │
│     │  • Collect chunk of 1000 records                                │     │
│     └─────────────────────────────────────────────────────────────────┘     │
│                                    │                                        │
│                                    ▼                                        │
│     ┌─────────────────────────────────────────────────────────────────┐     │
│     │  PROCESS (ItemProcessor)                                        │     │
│     │  • Validate datetime format (ISO-8601)                          │     │
│     │  • Validate temperature value                                   │     │
│     │  • Skip malformed records → log error details                   │     │
│     └─────────────────────────────────────────────────────────────────┘     │
│                                    │                                        │
│                                    ▼                                        │
│     ┌─────────────────────────────────────────────────────────────────┐     │
│     │  WRITE (JdbcBatchItemWriter)                                    │     │
│     │  • INSERT IGNORE into temperature_data table                    │     │
│     │  • Duplicates (name+datetime) silently ignored                  │     │
│     │  • Duplicate details → logs/duplicates-{timestamp}.log          │     │
│     │  • Commit transaction                                           │     │
│     └─────────────────────────────────────────────────────────────────┘     │
│                                    │                                        │
│                          (repeat until EOF)                                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  4. POST-PROCESSING                                                         │
│     • Rename processed file: data.csv → data.csv.processed                  │
│     • Move to next file (if any)                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  5. JOB COMPLETION                                                          │
│     • JobExecutionListener generates summary report                         │
│     • Log statistics to console:                                            │
│       - Total records processed                                             │
│       - Successful inserts                                                  │
│       - Duplicates ignored                                                  │
│       - Errors (skipped rows)                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Processing Details

#### Read Phase
- Uses `FlatFileItemReader` with `MultiResourceItemReader` for multiple files
- Parses CSV with comma delimiter and UTF-8 encoding
- Maps columns: `name`, `datetime`, `temp` to `TemperatureReading` record

#### Process Phase
- Validates `datetime` field against ISO-8601 format (`yyyy-MM-dd'T'HH:mm:ss`)
- Validates `temp` field is a valid decimal number
- Invalid records are skipped and logged with line number and error details

#### Write Phase
- Uses `JdbcBatchItemWriter` with MySQL `INSERT IGNORE` statement
- Database unique constraint `(name, datetime)` handles duplicates
- Transactions commit every 1000 records (configurable via `batch.chunk-size`)

### Error Handling

| Error Type | Behavior |
|------------|----------|
| Malformed CSV row | Skip row, log error, continue processing |
| Invalid datetime format | Skip row, log error, continue processing |
| Invalid temperature value | Skip row, log error, continue processing |
| Duplicate record | Silently ignore (INSERT IGNORE), log to duplicate file |
| Database connection error | Job fails, can be restarted |

### File Lifecycle

```
input/
├── data.csv              → Being processed
├── data.csv.processed    → Already processed (renamed)
└── new_data.csv          → Waiting to be processed
```

## Requirements

- Java 21
- Maven 3.9+
- MySQL 8.0+ (or use Docker Compose)

## Project Structure

```
src/
├── main/
│   ├── java/org/example/etl2/
│   │   ├── batch/                    # Spring Batch job configuration
│   │   │   ├── listener/             # Job and step listeners
│   │   │   ├── processor/            # Item processors
│   │   │   ├── reader/               # Item reader configurations
│   │   │   └── writer/               # Item writers
│   │   ├── config/                   # Application configuration
│   │   └── model/                    # Domain objects (Java records)
│   └── resources/
│       ├── application.properties    # Application configuration
│       └── db/migration/             # Flyway migration scripts
└── test/
    ├── java/                         # Test classes
    └── resources/data/               # Test CSV files
```

## Configuration

Configure the application in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `batch.input.directory` | Directory path for input CSV files | `./input` |
| `batch.chunk-size` | Number of records per transaction | `1000` |
| `spring.datasource.url` | MySQL database URL | `jdbc:mysql://localhost:3306/etl_db` |
| `spring.datasource.username` | Database username | `root` |
| `spring.datasource.password` | Database password | `root` |

## CSV File Format

Input CSV files must have:
- **Header row** with columns: `name`, `datetime`, `temp`
- **Comma-separated** values
- **UTF-8** encoding

Example:
```csv
name,datetime,temp
SensorA,2024-01-15T14:30:00,23.5
SensorB,2024-01-15T14:30:00,21.0
```

### Column Specifications

| Column | Type | Format | Description |
|--------|------|--------|-------------|
| `name` | String | - | Location or sensor name |
| `datetime` | DateTime | ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`) | Reading timestamp |
| `temp` | Decimal | 1 decimal place (e.g., `23.5`) | Temperature value |

## Database Schema

The application creates a `temperature_data` table via Flyway migration:

```sql
CREATE TABLE temperature_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    datetime DATETIME NOT NULL,
    temp DECIMAL(5,1) NOT NULL,
    UNIQUE KEY uk_name_datetime (name, datetime)
);
```

## Running the Application

### Using Docker Compose (Recommended)

1. Start MySQL:
   ```bash
   docker compose up -d
   ```

2. Create the input directory and add CSV files:
   ```bash
   mkdir -p input
   cp your-data.csv input/
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Using External MySQL

1. Create a database named `etl_db`
2. Update `application.properties` with your database credentials
3. Create the input directory and add CSV files
4. Run:
   ```bash
   ./mvnw spring-boot:run
   ```

## Output

### Console Summary

After job completion, a summary is logged:
```
Job completed: temperatureImportJob
  Total records processed: 100
  Successful inserts: 95
  Duplicates ignored: 3
  Errors (skipped): 2
```

### Duplicate Log File

Duplicate records are written to `logs/duplicates-{timestamp}.log`:
```
# Duplicate records detected during job execution {jobId}
2024-01-15T14:30:00|SensorA|2024-01-15T14:30:00|23.5
```

### Processed Files

Successfully processed CSV files are renamed with a `.processed` suffix to prevent reprocessing.

## Running Tests

Tests use Testcontainers with MySQL. Ensure Docker is running.

```bash
# Run all tests
TESTCONTAINERS_RYUK_DISABLED=true ./mvnw test

# Run specific test class
TESTCONTAINERS_RYUK_DISABLED=true ./mvnw test -Dtest=TemperatureImportJobTest
```

## Technology Stack

- **Java 21** with Records
- **Spring Boot 4.0.1**
- **Spring Batch** for ETL processing
- **Spring JDBC** for database access
- **Flyway** for database migrations
- **MySQL** for data storage
- **Testcontainers** for integration testing
- **JUnit 5** for testing

## License

This project is provided as-is for demonstration purposes.
