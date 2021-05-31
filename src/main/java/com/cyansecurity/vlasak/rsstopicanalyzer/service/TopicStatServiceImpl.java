package com.cyansecurity.vlasak.rsstopicanalyzer.service;

import com.cyansecurity.vlasak.rsstopicanalyzer.helper.TopicStatHelper;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.cyansecurity.vlasak.rsstopicanalyzer.repository.TopicStatRepository;
import model.TopicStat;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cyansecurity.vlasak.rsstopicanalyzer.controller.RssTopicAnalyzerController.NUMBER_OF_TOPICS;

@Service
public class TopicStatServiceImpl implements TopicStatService {

    @Resource
    private TopicStatRepository topicStatRepository;

    @Transactional
    @Override
    public long saveAll(final @Nonnull Map<String, Set<Feed>> matchingFeeds) {
        final long analysisId = topicStatRepository.findNextAnalysisId();
        topicStatRepository.saveAll(TopicStatHelper.createTopicStatList(matchingFeeds, analysisId));
        return analysisId;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TopicStat> find3TopicsWithHighestFreq(final long analysisId) {
        return topicStatRepository.findTopicsWithHighestFreq(analysisId, PageRequest.of(0, NUMBER_OF_TOPICS));
    }
}
