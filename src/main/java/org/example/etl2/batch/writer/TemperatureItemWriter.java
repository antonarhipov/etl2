package org.example.etl2.batch.writer;

import org.example.etl2.model.TemperatureReading;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TemperatureItemWriter {

    private final DataSource dataSource;

    public TemperatureItemWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcBatchItemWriter<TemperatureReading> temperatureWriter() {
        return new JdbcBatchItemWriterBuilder<TemperatureReading>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO temperature_data (name, datetime, temp)
                        VALUES (:name, :datetime, :temp)
                        """)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
