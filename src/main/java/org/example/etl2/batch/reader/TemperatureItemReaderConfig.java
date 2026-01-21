package org.example.etl2.batch.reader;

import org.example.etl2.model.TemperatureReading;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemperatureItemReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<TemperatureReading> temperatureItemReader() {
        return new FlatFileItemReaderBuilder<TemperatureReading>()
                .name("temperatureItemReader")
                .linesToSkip(1)
                .encoding("UTF-8")
                .delimited()
                .delimiter(",")
                .names("name", "datetime", "temp")
                .fieldSetMapper(new TemperatureReadingFieldSetMapper())
                .build();
    }
}
