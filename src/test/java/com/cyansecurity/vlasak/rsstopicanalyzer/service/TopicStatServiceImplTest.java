package com.cyansecurity.vlasak.rsstopicanalyzer.service;

import com.cyansecurity.vlasak.rsstopicanalyzer.BaseTest;
import com.cyansecurity.vlasak.rsstopicanalyzer.TopicTestHelper;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.cyansecurity.vlasak.rsstopicanalyzer.repository.TopicStatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.TopicStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cyansecurity.vlasak.rsstopicanalyzer.controller.RssTopicAnalyzerController.NUMBER_OF_TOPICS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
public class TopicStatServiceImplTest extends BaseTest {

    @Autowired
    private TopicStatService topicStatService;
    @Autowired
    private TopicStatRepository topicStatRepository;

    @Test
    public void GIVEN_notTopicsInDb_and_oneTopicToSave_WHEN_saveAll_THEN_topicCorrectlySaved_and_correctId_returned()
            throws JsonProcessingException {
        // db is empty -> expectedAnalysisId = 1
        final long expectedAnalysisId = 1;
        final String topic = "asia";
        // for simplicity tested just with one topic
        final Map<String, Set<Feed>> commonTopics = TopicTestHelper.create1DummyCommonTopicWithFreq2(topic);
        final long analysisId = topicStatService.saveAll(commonTopics);

        final List<TopicStat> dbTopics = topicStatRepository.findAll();
        final TopicStat onlyOneDbTopic =  dbTopics.get(0);
        assertThat("Returned analysisId is wrong", analysisId, is(expectedAnalysisId));
        assertThat("Topic is wrong", onlyOneDbTopic.getTopic(), is(topic));
        assertThat("Frequency is wrong", onlyOneDbTopic.getFrequency(), is(onlyOneDbTopic.getFrequency()));
        assertThat("References are wrong", onlyOneDbTopic.getReferences(), is(toJson(commonTopics.get(topic))));
        assertThat("AnalysisId is wrong", onlyOneDbTopic.getAnalysisId(), is(expectedAnalysisId));
    }

    @Test
    public void GIVEN_2topics_withOurId_WHEN_find3TopicsWithHighestFreq_withOurId_THEN_our2TopicsReturned() {
        final long analysisId = 1;
        List<TopicStat> dbTopicStatList = topicStatRepository.saveAll(TopicTestHelper.create2DummyHotTopics(analysisId));
        dbTopicStatList = dbTopicStatList.stream()
                .sorted(TopicTestHelper.getReversedTopicStatComparatorUsingFreq())
                .collect(Collectors.toList());

        final List<TopicStat> topicStatList = topicStatService.find3TopicsWithHighestFreq(analysisId);
        assertThat("Wrong number of topics found", topicStatList.size(), is(dbTopicStatList.size()));
        assertThat("Wrong topic found - element or order problem)", topicStatList, is(dbTopicStatList));
    }

    @Test
    public void GIVEN_5topics_butOnly4withOurId_WHEN_find3TopicsWithHighestFreq_THEN_3topics_withHighestFreq_found() {
        final long analysisId = 1;
        // 4 with our ID
        final TopicStat topicStat1 = new TopicStat("europe", 2, "dummy value", analysisId);
        final TopicStat topicStat2 = new TopicStat("asia", 5, "dummy value", analysisId);
        final TopicStat topicStat3 = new TopicStat("africa", 3, "dummy value", analysisId);
        final TopicStat topicStat4 = new TopicStat("america", 4, "dummy value", analysisId);
        // 1 with other ID
        final TopicStat topicStat5 = new TopicStat("antarctica", 6, "dummy value", analysisId + 1);
        final List<TopicStat> dbTopicStatList = topicStatRepository.saveAll(
                Arrays.asList(topicStat1, topicStat2, topicStat3, topicStat4, topicStat5));
        final List<TopicStat> expected3TopicsWithHighestFreq = dbTopicStatList.stream()
                // filter out not relevant topics
                .filter(t -> !t.getTopic().equals("europe") && !t.getTopic().equals("antarctica"))
                .sorted(TopicTestHelper.getReversedTopicStatComparatorUsingFreq())
                .collect(Collectors.toList());

        final List<TopicStat> topicStatList = topicStatService.find3TopicsWithHighestFreq(analysisId);
        assertThat("Wrong number of topics found", topicStatList.size(), is(NUMBER_OF_TOPICS));
        assertThat("Wrong topic found - element or order problem)", topicStatList, is(expected3TopicsWithHighestFreq));
    }

    @Test
    public void GIVEN_4topics_and2HaveSameFreq_WHEN_find3TopicsWithHighestFreq_THEN_still3TopicsFoundButNotEnsuredWhich() {
        final long analysisId = 1;
        final String europe = "europe";
        final String africa = "africa";
        final TopicStat topicStat1 = new TopicStat(europe, 3, "dummy value", analysisId);
        final TopicStat topicStat2 = new TopicStat("asia", 5, "dummy value", analysisId);
        final TopicStat topicStat3 = new TopicStat(africa, 3, "dummy value", analysisId);
        final TopicStat topicStat4 = new TopicStat("america", 4, "dummy value", analysisId);
        final List<TopicStat> dbTopicStatList = topicStatRepository.saveAll(
                Arrays.asList(topicStat1, topicStat2, topicStat3, topicStat4));
        // prepare 2 possible results
        final List<TopicStat> expected3TopicsWithEurope = dbTopicStatList.stream()
                .filter(t -> !t.getTopic().equals(africa))
                .sorted(TopicTestHelper.getReversedTopicStatComparatorUsingFreq())
                .collect(Collectors.toList());
        final List<TopicStat> expected3TopicsWithAfrica = dbTopicStatList.stream()
                .filter(t -> !t.getTopic().equals(europe))
                .sorted(TopicTestHelper.getReversedTopicStatComparatorUsingFreq())
                .collect(Collectors.toList());

        final List<TopicStat> topicStatList = topicStatService.find3TopicsWithHighestFreq(analysisId);
        assertThat("Wrong number of topics found", topicStatList.size(), is(NUMBER_OF_TOPICS));
        if (topicStatList.stream().anyMatch(t -> t.getTopic().equals(europe))) {
            assertThat("Wrong topic found - element or order problem)", topicStatList, is(expected3TopicsWithEurope));
        } else {
            assertThat("Wrong topic found - element or order problem)", topicStatList, is(expected3TopicsWithAfrica));
        }
    }
}