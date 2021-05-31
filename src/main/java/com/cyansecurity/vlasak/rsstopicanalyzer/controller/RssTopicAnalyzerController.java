package com.cyansecurity.vlasak.rsstopicanalyzer.controller;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.cyansecurity.vlasak.rsstopicanalyzer.exception.ErrorDetails;
import com.cyansecurity.vlasak.rsstopicanalyzer.processing.RssFeedProcessor;
import com.cyansecurity.vlasak.rsstopicanalyzer.processing.Tokenizer;
import com.cyansecurity.vlasak.rsstopicanalyzer.service.TopicStatService;
import model.TopicStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class RssTopicAnalyzerController {

    private static final Logger logger = LoggerFactory.getLogger(RssTopicAnalyzerController.class);

    public static final int MIN_PARAM_NUMBER = 2;
    public static final int NUMBER_OF_TOPICS = 3;

    public static final String ANALYSE_API_ENDPOINT = "/analyse/new";
    public static final String FREQUENCY_API_ENDPOINT = "/frequency/";

    public static final String ANALYSE_API = "Analyse API: ";
    public static final String FREQUENCY_API = "Frequency API: ";

    @Autowired
    private RssFeedProcessor rssFeedProcessor;
    @Autowired
    private TopicStatService topicStatService;

    private Tokenizer tokenizer;

    public RssTopicAnalyzerController() {
        try {
            tokenizer = new Tokenizer();
        } catch (IOException e) {
            logger.error("RssTopicAnalyzer API could not start - problem with a config file for the Tokenizer", e);
            System.exit(1);
        }
    }

    @PostMapping(ANALYSE_API_ENDPOINT)
    public ResponseEntity findCommonTopicsAndPersistThem(final @RequestBody String[] urls, final WebRequest webRequest) {
        if (urls.length < MIN_PARAM_NUMBER) {
            final String msg = String.format(ANALYSE_API + "at least %s RSS URLs are expected.", MIN_PARAM_NUMBER);
            logger.error(msg);
            return new ResponseEntity(new ErrorDetails(msg, webRequest.getDescription(false)),
                    HttpStatus.NOT_ACCEPTABLE);
        }
        final Map<String, Set<Feed>> matchingFeeds = rssFeedProcessor.performAnalysisAndGiveCommonTopics(
                Arrays.asList(urls), tokenizer);
        if (matchingFeeds.isEmpty()) {
            logger.info(ANALYSE_API + "no common topics were found for URLs: {}", urls);
            return ResponseEntity.noContent().build();
        } else {
            logger.info(ANALYSE_API + "{} common topics were found for URLs: {}", matchingFeeds.size(), urls);
            return ResponseEntity.ok(topicStatService.saveAll(matchingFeeds));
        }
    }

    @GetMapping(FREQUENCY_API_ENDPOINT + "{id}")
    public ResponseEntity getMostReferencedTopics(final @PathVariable(name="id") long id) {
        final List<TopicStat> topics = topicStatService.find3TopicsWithHighestFreq(id);
        if (topics.isEmpty()) {
            logger.info(FREQUENCY_API + "for analysis with id = {} were not found any hot topics - " +
                    "analyse API had not found any or it had not been started at all", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.info(FREQUENCY_API + "for analysis with id = {} were found {} hot topics", id, topics.size());
            return ResponseEntity.ok(topics);
        }
    }
}
