package com.cyansecurity.vlasak.rsstopicanalyzer.repository;

import model.TopicStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.is;

@DataJpaTest
public class TopicStatRepositoryTest {

    @Autowired
    private TopicStatRepository topicStatRepository;

    @Test
    public void GIVEN_notTopicStatExists_WHEN_findNextAnalysisId_THEN_value_1_found() {
        assertThat("Wrong analysis ID found", topicStatRepository.findNextAnalysisId(), is(1L));
    }

    @Test
    public void GIVEN_2topicStats_withAnalysisId_3_and_4_exists_WHEN_findNextAnalysisId_THEN_value_5_found() {
        long higherAnalysisId = 4;
        topicStatRepository.save(new TopicStat("europe", 2, "dummy value", 4));
        topicStatRepository.save(new TopicStat("vaccination", 3, "dummy value", 3));

        assertThat("Wrong analysis ID found",
                topicStatRepository.findNextAnalysisId(), is(higherAnalysisId + 1));
    }

    @Test
    public void GIVEN_topicStats_forDifferentAnalyses_WHEN_findTopicsWithHighestFreq_THEN_correctTopicStatsFound_withRightOrder() {
        topicStatRepository.save(new TopicStat("europe", 4, "dummy value", 1));
        topicStatRepository.save(new TopicStat("vaccination", 5, "dummy value", 2));
        final long ourAnalysisId = 3;
        final int numberOfTopics = 2;
        final TopicStat topic1 = new TopicStat("covid-19", 5, "dummy value", ourAnalysisId);
        final TopicStat topic2 = new TopicStat("president", 4, "dummy value", ourAnalysisId);
        topicStatRepository.save(topic1);
        topicStatRepository.save(topic2);

        final List<TopicStat> foundTopics = topicStatRepository.findTopicsWithHighestFreq(ourAnalysisId,
                PageRequest.of(0, numberOfTopics));
        assertThat("Incorrect number of topics were found", foundTopics.size(), is(numberOfTopics));
        assertThat("Incorrect topics were found", foundTopics, containsInRelativeOrder(topic1, topic2));
    }
}