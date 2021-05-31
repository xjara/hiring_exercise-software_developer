package com.cyansecurity.vlasak.rsstopicanalyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public abstract class BaseTest {

    protected String toJson(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    protected String createResourceUri(final String fileName) throws IOException {
        return new ClassPathResource(fileName).getURI().toString();
    }
}
