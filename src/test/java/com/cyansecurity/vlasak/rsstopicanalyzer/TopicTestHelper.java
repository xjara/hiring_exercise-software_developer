package com.cyansecurity.vlasak.rsstopicanalyzer;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import model.TopicStat;

import javax.annotation.Nonnull;
import java.util.*;

public class TopicTestHelper {

    public static LinkedHashSet<Feed> getReferencesFor1DummyCommonTopic(final String topic) {
        final Feed feed1 = new Feed(topic + " is the biggest ...", "http://www.cnn.com");
        final Feed feed2 = new Feed(topic + " has the most inhabitants ...", "http://www.news.com");
        final LinkedHashSet<Feed> references = new LinkedHashSet<>();
        references.add(feed1);
        references.add(feed2);
        return references;
    }

    public static HashMap<String, Set<Feed>> create1DummyCommonTopicWithFreq2(@Nonnull final String topic) {
        final HashMap<String, Set<Feed>> topics = new HashMap<>();
        topics.put(topic.toLowerCase(), getReferencesFor1DummyCommonTopic(topic));
        return topics;
    }

    public static List<TopicStat> create2DummyHotTopics(final long analysisId) {
        final TopicStat topicStat1 = new TopicStat("europe", 3, "dummy value", analysisId);
        final TopicStat topicStat2 = new TopicStat("asia", 2, "dummy value", analysisId);
        return Arrays.asList(topicStat1, topicStat2);
    }

    public static Comparator<TopicStat> getReversedTopicStatComparatorUsingFreq() {
        return Comparator.comparing(TopicStat::getFrequency).reversed();
    }
}
