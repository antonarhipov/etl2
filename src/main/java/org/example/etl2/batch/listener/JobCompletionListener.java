package org.example.etl2.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class JobCompletionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    private final AtomicLong duplicatesCount = new AtomicLong(0);

    public void incrementDuplicates() {
        duplicatesCount.incrementAndGet();
    }

    public long getDuplicatesCount() {
        return duplicatesCount.get();
    }

    public void resetDuplicatesCount() {
        duplicatesCount.set(0);
    }

    @Override
    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        long totalRead = 0;
        long totalWritten = 0;
        long totalSkipped = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            totalRead += stepExecution.getReadCount();
            totalWritten += stepExecution.getWriteCount();
            totalSkipped += stepExecution.getSkipCount();
        }

        long duplicates = duplicatesCount.get();
        long errors = totalSkipped;
        
        log.info("Job Summary:");
        log.info("- Total records processed: {}", totalRead);
        log.info("- Successful inserts: {}", totalWritten);
        log.info("- Duplicates ignored: {}", duplicates);
        log.info("- Errors: {}", errors);

        // Reset for next job run
        resetDuplicatesCount();
    }
}
