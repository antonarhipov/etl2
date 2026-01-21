package org.example.etl2.batch.listener;

import org.example.etl2.model.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DuplicateLogWriter {

    private static final Logger log = LoggerFactory.getLogger(DuplicateLogWriter.class);
    private static final String LOGS_DIRECTORY = "logs";
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private BufferedWriter writer;
    private Path logFilePath;

    public void initialize(Long jobId) {
        try {
            Path logsDir = Path.of(LOGS_DIRECTORY);
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }

            String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMATTER);
            String filename = "duplicates-" + timestamp + ".log";
            logFilePath = logsDir.resolve(filename);

            writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            writer.write("# Duplicate records detected during job execution " + jobId);
            writer.newLine();
            writer.flush();

            log.info("Duplicate log file created: {}", logFilePath);
        } catch (IOException e) {
            log.error("Failed to create duplicate log file", e);
            throw new RuntimeException("Failed to create duplicate log file", e);
        }
    }

    public void writeDuplicate(TemperatureReading reading) {
        if (writer == null) {
            log.warn("DuplicateLogWriter not initialized, skipping duplicate logging");
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(ISO_FORMATTER);
            String line = String.format("%s|%s|%s|%s",
                    timestamp,
                    reading.name(),
                    reading.datetime().format(ISO_FORMATTER),
                    reading.temp());
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write duplicate record to log file", e);
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
                log.info("Duplicate log file closed: {}", logFilePath);
            } catch (IOException e) {
                log.error("Failed to close duplicate log file", e);
            }
            writer = null;
        }
    }

    public Path getLogFilePath() {
        return logFilePath;
    }
}
