package com.cyansecurity.vlasak.rsstopicanalyzer.helper;

import com.cyansecurity.vlasak.rsstopicanalyzer.BaseTest;
import com.cyansecurity.vlasak.rsstopicanalyzer.TopicTestHelper;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.TopicStat;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TopicStatHelperTest extends BaseTest {

    @Test
    public void GIVEN_matchingFeeds_and_id_WHEN_createTopicStatList_THEN_listOfTopicStat_correctlyCreated() throws JsonProcessingException {
        final String topic = "Asia";
        final long analysisId = 1;
        final String expectedReferencesInJson = toJson(TopicTestHelper.getReferencesFor1DummyCommonTopic(topic));
        // to keep it simple - just one topic will be tested
        final Map<String, Set<Feed>> topics = TopicTestHelper.create1DummyCommonTopicWithFreq2(topic);

        final List<TopicStat> topicStatList = TopicStatHelper.createTopicStatList(topics, analysisId);
        final TopicStat justOneTopic = topicStatList.get(0);
        assertThat("List of TopicStat has incorret size", topics.size(), is(topicStatList.size()));
        assertThat("Topic name is incorrect", justOneTopic.getTopic(), is(topic.toLowerCase()));
        assertThat("Frequency is incorrect", justOneTopic.getFrequency(), is(2));
        assertThat("References in JSON are incorrect", justOneTopic.getReferences(), is(expectedReferencesInJson));
        assertThat("AnalysisId is incorrect", justOneTopic.getAnalysisId(), is(analysisId));
    }
}