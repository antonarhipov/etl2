package org.example.etl2.batch.processor;

import org.example.etl2.model.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TemperatureItemProcessor implements ItemProcessor<TemperatureReading, TemperatureReading> {

    private static final Logger log = LoggerFactory.getLogger(TemperatureItemProcessor.class);

    @Override
    public TemperatureReading process(TemperatureReading item) throws Exception {
        if (item.name() == null || item.name().isBlank()) {
            log.warn("Skipping record with null or blank name: {}", item);
            return null;
        }

        if (item.datetime() == null) {
            log.warn("Skipping record with null datetime: {}", item);
            return null;
        }

        if (item.temp() == null) {
            log.warn("Skipping record with null temperature: {}", item);
            return null;
        }

        return item;
    }
}
