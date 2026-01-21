package org.example.etl2.batch.processor;

public class InvalidDateTimeException extends RuntimeException {

    private final String datetimeString;

    public InvalidDateTimeException(String datetimeString, String reason) {
        super(String.format("Invalid datetime value '%s': %s", datetimeString, reason));
        this.datetimeString = datetimeString;
    }

    public String getDatetimeString() {
        return datetimeString;
    }
}
