package org.example.etl2.batch;

import org.example.etl2.batch.listener.JobCompletionListener;
import org.example.etl2.batch.listener.SkipItemListener;
import org.example.etl2.batch.processor.TemperatureItemProcessor;
import org.example.etl2.model.TemperatureReading;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class TemperatureImportJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MultiResourceItemReader<TemperatureReading> multiResourceItemReader;
    private final TemperatureItemProcessor temperatureItemProcessor;
    private final JdbcBatchItemWriter<TemperatureReading> temperatureWriter;
    private final JobCompletionListener jobCompletionListener;
    private final SkipItemListener skipItemListener;

    @Value("${batch.chunk-size:1000}")
    private int chunkSize;

    public TemperatureImportJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            MultiResourceItemReader<TemperatureReading> multiResourceItemReader,
            TemperatureItemProcessor temperatureItemProcessor,
            JdbcBatchItemWriter<TemperatureReading> temperatureWriter,
            JobCompletionListener jobCompletionListener,
            SkipItemListener skipItemListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.multiResourceItemReader = multiResourceItemReader;
        this.temperatureItemProcessor = temperatureItemProcessor;
        this.temperatureWriter = temperatureWriter;
        this.jobCompletionListener = jobCompletionListener;
        this.skipItemListener = skipItemListener;
    }

    @Bean
    public Step importStep() {
        return new StepBuilder("importStep", jobRepository)
                .<TemperatureReading, TemperatureReading>chunk(chunkSize, transactionManager)
                .reader(multiResourceItemReader)
                .processor(temperatureItemProcessor)
                .writer(temperatureWriter)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(skipItemListener)
                .build();
    }

    @Bean
    public Job temperatureImportJob(Step importStep) {
        return new JobBuilder("temperatureImportJob", jobRepository)
                .listener(jobCompletionListener)
                .start(importStep)
                .build();
    }
}
