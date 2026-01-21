package org.example.etl2.batch.reader;

import org.example.etl2.model.TemperatureReading;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
public class MultiFileReaderConfig {

    @Value("${batch.input.directory}")
    private String inputDirectory;

    private final FlatFileItemReader<TemperatureReading> temperatureItemReader;

    public MultiFileReaderConfig(FlatFileItemReader<TemperatureReading> temperatureItemReader) {
        this.temperatureItemReader = temperatureItemReader;
    }

    @Bean
    public MultiResourceItemReader<TemperatureReading> multiResourceItemReader() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("file:" + inputDirectory + "/*.csv");

        var reader = new MultiResourceItemReader<>(temperatureItemReader);
        reader.setResources(resources);
        return reader;
    }
}
