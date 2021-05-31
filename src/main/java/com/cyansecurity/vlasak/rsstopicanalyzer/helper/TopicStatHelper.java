package com.cyansecurity.vlasak.rsstopicanalyzer.helper;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.TopicStat;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TopicStatHelper {

    public static List<TopicStat> createTopicStatList(@Nonnull final Map<String, Set<Feed>> matchingFeeds,
                                                      final long analysisId) {
        final List<TopicStat> topicStats = new ArrayList<>();
        for(final String feed : matchingFeeds.keySet()) {
            final Set<Feed> topicFeeds = matchingFeeds.get(feed);
            try {
                topicStats.add(new TopicStat(feed, topicFeeds.size(),
                        new ObjectMapper().writeValueAsString(topicFeeds), analysisId));
            } catch (JsonProcessingException e) {
                throw new TopicStatJsonException("Problem during serializing set of topics into JSON", e);
            }
        }
        return topicStats;
    }
}
