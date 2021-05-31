package com.cyansecurity.vlasak.rsstopicanalyzer.xml;

import com.cyansecurity.vlasak.rsstopicanalyzer.exception.ApiException;

import javax.annotation.Nonnull;

public class RssFeedParserException extends ApiException {

    public RssFeedParserException(@Nonnull String message) {
        super(message);
    }

    public RssFeedParserException(@Nonnull String message, @Nonnull Throwable exception) {
        super(message, exception);
    }
}
