package com.cyansecurity.vlasak.rsstopicanalyzer.exception;

import javax.annotation.Nonnull;

public abstract class ApiException extends RuntimeException {

    public ApiException(@Nonnull String message) {
        super(message);
    }

    public ApiException(@Nonnull String message, @Nonnull Throwable exception) {
        super(message, exception);
    }
}
