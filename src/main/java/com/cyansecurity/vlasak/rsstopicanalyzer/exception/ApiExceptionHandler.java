package com.cyansecurity.vlasak.rsstopicanalyzer.exception;

import com.cyansecurity.vlasak.rsstopicanalyzer.helper.TopicStatJsonException;
import com.cyansecurity.vlasak.rsstopicanalyzer.xml.RssFeedParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RssFeedParserException.class)
    public ResponseEntity<?> handleRssFeedParserException(final RssFeedParserException exception, final WebRequest request) {
        logError(exception);
        return new ResponseEntity(createErrorDetails(exception, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TopicStatJsonException.class)
    public ResponseEntity<?> handleTopicStatJsonException(final TopicStatJsonException exception, final WebRequest request) {
        logError(exception);
        return new ResponseEntity(createErrorDetails(exception, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(final ApiException exception) {
        if (null == exception.getCause()) {
            logger.error(exception.getMessage());
        } else {
            logger.error(exception.getMessage(), exception.getCause());
        }
    }

    private ErrorDetails createErrorDetails(final ApiException exception, final WebRequest request) {
        return new ErrorDetails(exception.getMessage(), request.getDescription(false));
    }
}
