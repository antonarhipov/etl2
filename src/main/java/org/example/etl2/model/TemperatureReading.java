package org.example.etl2.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TemperatureReading(
        String name,
        LocalDateTime datetime,
        BigDecimal temp
) {
}
