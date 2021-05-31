package com.cyansecurity.vlasak.rsstopicanalyzer.controller;

import com.cyansecurity.vlasak.rsstopicanalyzer.BaseTest;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.TopicStat;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.*;

import static com.cyansecurity.vlasak.rsstopicanalyzer.controller.RssTopicAnalyzerController.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class RssTopicAnalyzerControllerIT extends BaseTest {

    private static final String FOLDER = "/integration_test/";

    @Autowired
    private MockMvc mvc;

    @Test
    public void GIVEN_threeUrls_withCommonTopics_WHEN_analyseAndFrequencyApi_THEN_bothApiOk_and_correctResultsReturned()
            throws Exception {
        final String jsonData = toJson(Arrays.asList(
                createResourceUri(FOLDER + "fragment_news_google_com.rss.xml"),
                createResourceUri(FOLDER + "fragment_the_guardian_com.rss.xml"),
                createResourceUri(FOLDER + "fragment_yahoo_com.rss.xml")));
        final RequestBuilder requestToAnalyseApi = post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);

        // request on analyse API
        final MvcResult analyseApiResult = mvc.perform(requestToAnalyseApi).andReturn();
        assertThat(ANALYSE_API + "response status should be OK",
                analyseApiResult.getResponse().getStatus(), is(HttpStatus.OK.value()));

        final long analysisId = Long.parseLong(analyseApiResult.getResponse().getContentAsString());
        final RequestBuilder requestToFrequencyApi = get(FREQUENCY_API_ENDPOINT + analysisId);

        // request on frequency API
        final MvcResult frequencyApiResult = mvc.perform(requestToFrequencyApi).andReturn();
        assertThat(FREQUENCY_API + "response status should be OK",
                frequencyApiResult.getResponse().getStatus(), is(HttpStatus.OK.value()));
        final String resultJson = frequencyApiResult.getResponse().getContentAsString();

        // convert returned json result to objects
        final List<TopicStat> hotTopics = new ObjectMapper().readValue(
                resultJson, new TypeReference<List<TopicStat>>(){});
        // expected result
        final List<TopicStat> expectedHotTopics = createExpectedResult();
        final int numberOfExpectedHotTopics = expectedHotTopics.size();

        assertThat("Wrong number of hot topics found", hotTopics.size(), is(numberOfExpectedHotTopics));
        // check each hot topic one by one
        for(int i = 0; i < numberOfExpectedHotTopics; i++) {
            final TopicStat hotTopic = hotTopics.get(i);
            final TopicStat expectedTopic = expectedHotTopics.get(i);

            assertThat("Topic is wrong", hotTopic.getTopic(), is(expectedTopic.getTopic()));
            assertThat("Frequency is wrong", hotTopic.getFrequency(), is(expectedTopic.getFrequency()));
            JSONAssert.assertEquals("References are wrong",
                    hotTopic.getReferences(), expectedTopic.getReferences(), false);
        }
    }

    private List<TopicStat> createExpectedResult() throws JsonProcessingException {
        // covid-19 -> 4 times
        final Feed feedCovid1 = new Feed("The unseen covid-19 risk for unvaccinated people - Yahoo News",
                "https://news.google.com/__i/rss/rd/articles/the-unsen-covid-19");
        final Feed feedCovid2 = new Feed("Coronavirus mailbag: Responding to your COVID-19 vaccine concerns - KSL.com",
                "https://news.google.com/__i/rss/rd/articles/coronavirus-mailbag");
        final Feed feedCovid3 = new Feed("Kumbh Mela: how a superspreader festival seeded Covid-19 across India",
                "https://www.theguardian.com/world/2021/may/30/kumbh-mela-how-a-superspreader-festival-seeded-covid-across-india");
        final Feed feedCovid4 = new Feed("Lebanon ramps up COVID-19 fight with vaccination marathon",
                "https://news.yahoo.com/lebanon-ramps-covid-19-fight-122517318.html");
        final List<Feed> covidReferences = Arrays.asList(feedCovid3, feedCovid4, feedCovid2, feedCovid1);
        // police -> 3 times
        final Feed feedPolice1 = new Feed("Partner of Lord Ashcroft son questioned over killing of Belize police officer",
                "https://www.theguardian.com/world/2021/may/30/jasmin-hartin-partner-of-lord-ashcroft-son-questioned-over-killing-of-belize-police-officer");
        final Feed feedPolice2 = new Feed("Black fear of Tulsa police lingers 100 years after massacre",
                "https://news.yahoo.com/100-years-tulsa-race-massacre-152344936.html");
        final Feed feedPolice3 = new Feed("Police: 2 dead, over 20 injured in banquet hall shooting",
                "https://news.yahoo.com/police-2-dead-over-20-095705864.html");
        final List<Feed> policeReferences = Arrays.asList(feedPolice1, feedPolice2, feedPolice3);
        // china -> 2 times
        final Feed feedChina1 = new Feed("China Censors Lady Gaga, Justin Bieber On 'Friends' Reunion Special - HuffPost",
                "https://news.google.com/__i/rss/rd/articles/china-censors");
        final Feed feedChina2 = new Feed("China and it's sci-tech development to focus more on self-reliance",
                "https://finance.yahoo.com/news/chinas-sci-tech-development-focus-115100862.html");
        final List<Feed> chinaReferences = Arrays.asList(feedChina1, feedChina2);

        // analysisId is not important here
        final long analysisId = 1;
        final TopicStat covid19 = new TopicStat("covid-19", covidReferences.size(), toJson(covidReferences), analysisId);
        final TopicStat police = new TopicStat("police", policeReferences.size(), toJson(policeReferences), analysisId);
        final TopicStat china = new TopicStat("china", chinaReferences.size(), toJson(chinaReferences), analysisId);
        return Arrays.asList(covid19, police, china);
    }
}