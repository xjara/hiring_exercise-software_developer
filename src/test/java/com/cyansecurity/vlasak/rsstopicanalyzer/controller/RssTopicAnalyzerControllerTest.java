package com.cyansecurity.vlasak.rsstopicanalyzer.controller;

import com.cyansecurity.vlasak.rsstopicanalyzer.BaseTest;
import com.cyansecurity.vlasak.rsstopicanalyzer.TopicTestHelper;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.cyansecurity.vlasak.rsstopicanalyzer.helper.TopicStatJsonException;
import com.cyansecurity.vlasak.rsstopicanalyzer.processing.RssFeedProcessor;
import com.cyansecurity.vlasak.rsstopicanalyzer.processing.Tokenizer;
import com.cyansecurity.vlasak.rsstopicanalyzer.service.TopicStatService;
import com.cyansecurity.vlasak.rsstopicanalyzer.xml.RssFeedParserException;
import model.TopicStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.*;

import static com.cyansecurity.vlasak.rsstopicanalyzer.controller.RssTopicAnalyzerController.ANALYSE_API_ENDPOINT;
import static com.cyansecurity.vlasak.rsstopicanalyzer.controller.RssTopicAnalyzerController.FREQUENCY_API_ENDPOINT;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RssTopicAnalyzerController.class)
public class RssTopicAnalyzerControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TopicStatService topicStatService;
    @MockBean
    private RssFeedProcessor rssFeedProcessor;

    @Test
    public void GIVEN_oneUrl_WHEN_analyseApi_THEN_notAccepted_and_answerInJson() throws Exception {
        final List<String> urls = Arrays.asList("correct_rss_with_two_items.xml");
        final String jsonData = toJson(urls);
        final RequestBuilder request = MockMvcRequestBuilders.post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);

        mvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
        // todo: check more content of the answer
        verify(rssFeedProcessor, never()).performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class));
        verify(topicStatService, never()).saveAll(anyMap());
    }

    @Test
    public void GIVEN_twoUrls_WHEN_analyseApi_andRssFeedParserExceptionThrown_THEN_internalServerError_and_answerInJson()
            throws Exception {
        final String jsonData = toJson(Arrays.asList(
                createResourceUri("malformed_rss_instead_of_item_is_entry_tag.xml"),
                createResourceUri("correct_rss_with_two_items.xml")));
        final RequestBuilder request = MockMvcRequestBuilders.post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);
        when(rssFeedProcessor.performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class)))
                .thenThrow(new RssFeedParserException("Incorrect RSS format ..."));

        mvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
        // todo: check more content of the answer
        verify(topicStatService, never()).saveAll(anyMap());
    }

    @Test
    public void GIVEN_twoUrls_WHEN_analyseApi_andTopicStatJsonExceptionThrown_THEN_internalServerError_and_answerInJson()
            throws Exception {
        final String jsonData = toJson(Arrays.asList(
                createResourceUri("correct_rss_with_one_item.xml"),
                createResourceUri("correct_rss_with_two_items.xml")));
        final RequestBuilder request = MockMvcRequestBuilders.post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);
        final HashMap<String, Set<Feed>> commonTopics = TopicTestHelper.create1DummyCommonTopicWithFreq2("Asia");
        when(rssFeedProcessor.performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class)))
                .thenReturn(commonTopics);
        when(topicStatService.saveAll(commonTopics)).thenThrow(
                new TopicStatJsonException("Error ...", new IOException("because JsonProcessingException not allowed")));

        mvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
        // todo: check more content of the answer
        verify(topicStatService, times(1)).saveAll(commonTopics);
    }

    @Test
    public void GIVEN_twoUrls_butNoCommonTopics_WHEN_analyseApi_THEN_noContent()
            throws Exception {
        final List<String> urls = Arrays.asList(
                createResourceUri("correct_rss_with_one_item.xml"),
                createResourceUri("correct_rss_with_two_items.xml"));
        final String jsonData = toJson(urls);
        final RequestBuilder request = MockMvcRequestBuilders.post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);
        when(rssFeedProcessor.performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class)))
                .thenReturn(new HashMap<>());

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(rssFeedProcessor, times(1))
                .performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class));
        verify(topicStatService, never()).saveAll(anyMap());
    }

    @Test
    public void GIVEN_twoUrls_withCommonTopics_WHEN_analyseApi_THEN_jsonAnswerOk_andContains_id()
            throws Exception {
        final String jsonData = toJson(Arrays.asList(
                createResourceUri("correct_rss_with_one_item.xml"),
                createResourceUri("correct_rss_with_two_items.xml")));
        final RequestBuilder request = MockMvcRequestBuilders.post(ANALYSE_API_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonData);
        final long analysisId = 1;
        final HashMap<String, Set<Feed>> commonTopics = TopicTestHelper.create1DummyCommonTopicWithFreq2("Asia");
        when(rssFeedProcessor.performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class)))
                .thenReturn(commonTopics);
        when(topicStatService.saveAll(commonTopics)).thenReturn(analysisId);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(analysisId + ""))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
        verify(rssFeedProcessor, times(1))
                .performAnalysisAndGiveCommonTopics(anyList(), any(Tokenizer.class));
        verify(topicStatService, times(1)).saveAll(commonTopics);
    }

    @Test
    public void GIVEN_noCommonTopics_withOurIdStoredInDatabase_WHEN_frequencyApi_withOurId_THEN_noContent()
            throws Exception {
        final long analysisId = 1;
        final RequestBuilder request = MockMvcRequestBuilders.get(FREQUENCY_API_ENDPOINT + analysisId);
        when(topicStatService.find3TopicsWithHighestFreq(analysisId)).thenReturn(Collections.emptyList());

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(topicStatService, times(1)).find3TopicsWithHighestFreq(analysisId);
    }

    @Test
    public void GIVEN_commonTopics_withOurId_exist_WHEN_frequencyApi_withOurId_THEN_jsonAnswerOk_withFoundHotTopics()
            throws Exception {
        final long analysisId = 2;
        final List<TopicStat> foundHotTopics = TopicTestHelper.create2DummyHotTopics(analysisId);
        final RequestBuilder request = MockMvcRequestBuilders.get(FREQUENCY_API_ENDPOINT + analysisId);
        when(topicStatService.find3TopicsWithHighestFreq(analysisId))
                .thenReturn(foundHotTopics);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(foundHotTopics)))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
        verify(topicStatService, times(1)).find3TopicsWithHighestFreq(analysisId);
    }
}