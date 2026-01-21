package org.example.etl2.batch.listener;

import org.example.etl2.model.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@Component
public class SkipItemListener implements SkipListener<TemperatureReading, TemperatureReading> {

    private static final Logger log = LoggerFactory.getLogger(SkipItemListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException parseException) {
            log.error("Skipped record in read - Line number: {}, Raw content: [{}], Error: {}",
                    parseException.getLineNumber(),
                    parseException.getInput(),
                    parseException.getMessage());
        } else {
            log.error("Skipped record in read - Error: {}", t.getMessage());
        }
    }

    @Override
    public void onSkipInProcess(TemperatureReading item, Throwable t) {
        log.error("Skipped record in process - Item: {}, Error: {}", item, t.getMessage());
    }

    @Override
    public void onSkipInWrite(TemperatureReading item, Throwable t) {
        log.error("Skipped record in write - Item: {}, Error: {}", item, t.getMessage());
    }
}
