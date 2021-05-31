package com.cyansecurity.vlasak.rsstopicanalyzer.exception;

import javax.annotation.Nonnull;
import java.util.Date;

public class ErrorDetails {

    private final Date timestamp;
    private final String message;
    private final String details;

    public ErrorDetails(final @Nonnull String message, final @Nonnull String details) {
        this.timestamp = new Date();
        this.message = message;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
