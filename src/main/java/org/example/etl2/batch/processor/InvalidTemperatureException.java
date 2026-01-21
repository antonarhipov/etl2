package org.example.etl2.batch.processor;

import java.math.BigDecimal;

public class InvalidTemperatureException extends RuntimeException {

    private final BigDecimal temperature;

    public InvalidTemperatureException(BigDecimal temperature, String reason) {
        super(String.format("Invalid temperature value '%s': %s", temperature, reason));
        this.temperature = temperature;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }
}
