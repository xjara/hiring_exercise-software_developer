package com.cyansecurity.vlasak.rsstopicanalyzer.helper;

import com.cyansecurity.vlasak.rsstopicanalyzer.exception.ApiException;

import javax.annotation.Nonnull;

public class TopicStatJsonException extends ApiException {

    public TopicStatJsonException(@Nonnull String message, @Nonnull Throwable exception) {
        super(message, exception);
    }
}
