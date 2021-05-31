package com.cyansecurity.vlasak.rsstopicanalyzer.service;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import model.TopicStat;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TopicStatService {

    long saveAll(Map<String, Set<Feed>> matchingFeeds);

    List<TopicStat> find3TopicsWithHighestFreq(long analysisId);
}
