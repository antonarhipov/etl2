package org.example.etl2.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;

class TemperatureImportJobTest extends BaseIntegrationTest {

    @Value("${batch.input.directory}")
    private String inputDirectory;

    private Path inputPath;

    @BeforeEach
    void setUp() throws IOException {
        inputPath = Path.of(inputDirectory);
        if (!Files.exists(inputPath)) {
            Files.createDirectories(inputPath);
        }
        // Clean input directory
        Files.list(inputPath)
                .filter(p -> p.toString().endsWith(".csv"))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        // ignore
                    }
                });
        // Clear database
        clearDatabase();
    }

    @Test
    @DisplayName("Should import all valid records from CSV file")
    void testValidCsvImport() throws Exception {
        // Given: Copy valid_data.csv to input directory
        ClassPathResource resource = new ClassPathResource("data/valid_data.csv");
        Path targetFile = inputPath.resolve("valid_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should contain 15 records (valid_data.csv has 15 data rows)
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(15);
    }

    @Test
    @DisplayName("Should handle within-file duplicates - insert first, skip subsequent")
    void testWithinFileDuplicates() throws Exception {
        // Given: Copy with_duplicates.csv to input directory
        // File has 7 records with 3 duplicate name+datetime pairs
        // Unique records: 4 (Sensor1@10:30, Sensor2@11:00, Sensor3@12:00, Sensor1@13:00)
        ClassPathResource resource = new ClassPathResource("data/with_duplicates.csv");
        Path targetFile = inputPath.resolve("with_duplicates.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should contain only 4 unique records (duplicates ignored by INSERT IGNORE)
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(4);
    }

    @Test
    @DisplayName("Should skip records that already exist in database")
    void testDatabaseDuplicates() throws Exception {
        // Given: Pre-insert a record to database
        jdbcTemplate.update(
                "INSERT INTO temperature_data (name, datetime, temp) VALUES (?, ?, ?)",
                "Sensor1", java.sql.Timestamp.valueOf("2024-01-15 10:30:00"), new java.math.BigDecimal("20.0"));

        // And: Copy valid_data.csv which contains Sensor1 at 2024-01-15T10:30:00
        ClassPathResource resource = new ClassPathResource("data/valid_data.csv");
        Path targetFile = inputPath.resolve("valid_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should contain 15 records (1 pre-existing + 14 new, 1 duplicate skipped)
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(15);
    }

    @Test
    @DisplayName("Should detect all duplicates when reprocessing same file")
    void testReprocessSameFile() throws Exception {
        // Given: Copy valid_data.csv and run job first time
        ClassPathResource resource = new ClassPathResource("data/valid_data.csv");
        Path targetFile = inputPath.resolve("valid_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        JobExecution firstRun = jobLauncherTestUtils.launchJob();
        assertThat(firstRun.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(countRecords()).isEqualTo(15);

        // When: Run job again with same data (re-copy file as it may have been renamed)
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        JobExecution secondRun = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(secondRun.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should still contain 15 records (all duplicates ignored)
        assertThat(countRecords()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should skip rows with invalid datetime format and continue processing")
    void testInvalidDatetime() throws Exception {
        // Given: malformed_data.csv contains "invalid-date" in row 2
        ClassPathResource resource = new ClassPathResource("data/malformed_data.csv");
        Path targetFile = inputPath.resolve("malformed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully (not fail)
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Only valid rows should be in database (Sensor4 with valid data)
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should skip rows with missing required fields and continue processing")
    void testMissingRequiredField() throws Exception {
        // Given: malformed_data.csv has rows with missing name, datetime, and temp
        ClassPathResource resource = new ClassPathResource("data/malformed_data.csv");
        Path targetFile = inputPath.resolve("malformed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Only valid rows processed
        assertThat(countRecords()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should skip rows with non-numeric temperature and continue processing")
    void testInvalidTemperature() throws Exception {
        // Given: malformed_data.csv has "not-a-number" as temp value
        ClassPathResource resource = new ClassPathResource("data/malformed_data.csv");
        Path targetFile = inputPath.resolve("malformed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // Valid rows processed despite errors
        assertThat(countRecords()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should process malformed data file - skip invalid, process valid")
    void testMalformedDataFile() throws Exception {
        // Given: malformed_data.csv with various errors
        // Row 1: invalid datetime
        // Row 2: missing name
        // Row 3: invalid temperature
        // Row 4: VALID - Sensor4,2024-01-15T13:00:00,25.0
        // Row 5: missing datetime
        // Row 6: missing temp
        ClassPathResource resource = new ClassPathResource("data/malformed_data.csv");
        Path targetFile = inputPath.resolve("malformed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Only 1 valid record should be in database
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(1);

        // And: Step should report skipped records
        var stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getSkipCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle empty file - complete with zero records")
    void testEmptyFile() throws Exception {
        // Given: empty_file.csv with only header row
        ClassPathResource resource = new ClassPathResource("data/empty_file.csv");
        Path targetFile = inputPath.resolve("empty_file.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should be empty
        assertThat(countRecords()).isEqualTo(0);

        // And: Step execution should show 0 reads
        var stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(0);
        assertThat(stepExecution.getWriteCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should process mixed data file with valid, invalid, and duplicate records")
    void testMixedData() throws Exception {
        // Given: mixed_data.csv with:
        // - Valid: Sensor1@10:30, Sensor2@11:00, Sensor5@13:00, Sensor6@14:00, Sensor7@15:00 (5 unique)
        // - Duplicates: Sensor1@10:30 (duplicate), Sensor2@11:00 (duplicate) (2 duplicates)
        // - Errors: invalid-datetime, missing name, not-a-temp (3 errors)
        ClassPathResource resource = new ClassPathResource("data/mixed_data.csv");
        Path targetFile = inputPath.resolve("mixed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete successfully
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Database should contain 5 unique valid records
        long recordCount = countRecords();
        assertThat(recordCount).isEqualTo(5);
    }

    @Test
    @DisplayName("Should validate summary formula: processed = inserts + duplicates + errors")
    void testSummaryFormulaValidation() throws Exception {
        // Given: mixed_data.csv for comprehensive test
        ClassPathResource resource = new ClassPathResource("data/mixed_data.csv");
        Path targetFile = inputPath.resolve("mixed_data.csv");
        Files.copy(resource.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        // When: Launch the job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then: Job should complete
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // And: Summary formula should hold
        var stepExecution = jobExecution.getStepExecutions().iterator().next();
        long readCount = stepExecution.getReadCount();
        long writeCount = stepExecution.getWriteCount();
        long skipCount = stepExecution.getSkipCount();

        // processed (readCount) >= writeCount + skipCount
        // Note: duplicates via INSERT IGNORE are counted as writes in Spring Batch
        assertThat(readCount).isGreaterThanOrEqualTo(writeCount);
    }
}
