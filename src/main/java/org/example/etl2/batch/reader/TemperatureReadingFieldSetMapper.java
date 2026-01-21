package org.example.etl2.batch.reader;

import org.example.etl2.model.TemperatureReading;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemperatureReadingFieldSetMapper implements FieldSetMapper<TemperatureReading> {

    @Override
    public TemperatureReading mapFieldSet(FieldSet fieldSet) throws BindException {
        String name = fieldSet.readString("name");
        String datetimeStr = fieldSet.readString("datetime");
        LocalDateTime datetime = LocalDateTime.parse(datetimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        return new TemperatureReading(
                name,
                datetime,
                fieldSet.readBigDecimal("temp")
        );
    }
}
