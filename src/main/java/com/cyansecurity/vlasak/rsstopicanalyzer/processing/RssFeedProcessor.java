package com.cyansecurity.vlasak.rsstopicanalyzer.processing;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import com.cyansecurity.vlasak.rsstopicanalyzer.xml.RssFeedParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;

@Component
public class RssFeedProcessor {

    private List<String> urls;
    private Tokenizer tokenizer;

    @Autowired
    private RssFeedParser rssFeedParser;

    // for token representation of all rss channels - key: token and value: references
    private List<Map<String, Set<Feed>>> tokensOfAllRssChannels;
    // for storing matching feeds (only unique are relevant) - common topics
    private HashMap<String, Set<Feed>> matchedTokensMap;

    public RssFeedProcessor() {
    }

    public HashMap<String, Set<Feed>> performAnalysisAndGiveCommonTopics(@Nonnull final List urls,
                                                                         @Nonnull final Tokenizer tokenizer) {
        this.urls = urls;
        this.tokenizer = tokenizer;
        return loadAndConvertToTokenRepresentation().findMatchingFeeds();
    }

    private RssFeedProcessor loadAndConvertToTokenRepresentation() {
        tokensOfAllRssChannels = new ArrayList<>(urls.size());
        for(final String url : urls) {
            final List<Feed> feedsFromOneRssChannel = rssFeedParser.parse(url);
            final Map<String, Set<Feed>> tokensOfOneRssChannel = new HashMap<>();
            for (final Feed feed : feedsFromOneRssChannel) {
                final Set<String> feedTokenizedTitle = Collections.unmodifiableSet(
                        tokenizer.tokenizeAndConvertToLower(feed.getTitle())
                                .removeFillers()
                                .getTokens());
                for(final String token : feedTokenizedTitle) {
                    if (tokensOfOneRssChannel.containsKey(token)) {
                        tokensOfOneRssChannel.get(token).add(feed);
                    } else {
                        final Set<Feed> tokenRelatedFeeds = new HashSet<>();
                        tokenRelatedFeeds.add(feed);
                        tokensOfOneRssChannel.put(token, tokenRelatedFeeds);
                    }
                }
            }
            tokensOfAllRssChannels.add(tokensOfOneRssChannel);
        }
        return this;
    }

    private HashMap<String, Set<Feed>> findMatchingFeeds() {
        matchedTokensMap = new HashMap<>();
        final int lastUrlIndex = urls.size() - 1;
        final int penultimateUrlIndex = lastUrlIndex - 1;
        // do intersections of tokens between all rss channels (each with each)
        for (int i = 0; i <= penultimateUrlIndex; i++) {
            for(int j = i + 1; j <= lastUrlIndex; j++) {
                // token intersection of "i" and "j" rss channel
                final Set<String> intersectedTitleTokens = new HashSet<>(tokensOfAllRssChannels.get(i).keySet());
                if (intersectedTitleTokens.retainAll(tokensOfAllRssChannels.get(j).keySet())) {
                    // there is intersection -> store all intersected tokens of both rss channels
                    for (final String token : intersectedTitleTokens) {
                        storeReferencesOfBothChannels(token, i, j);
                    }
                }
            }
        }
        return matchedTokensMap;
    }

    private void storeReferencesOfBothChannels(final String token, final int iChannel, final int jChannel) {
        final Set<Feed> matchedFeeds;
        if (matchedTokensMap.containsKey(token)) {
            matchedFeeds = matchedTokensMap.get(token);
        } else {
            matchedFeeds = new HashSet<>();
            matchedTokensMap.put(token, matchedFeeds);
        }
        matchedFeeds.addAll(tokensOfAllRssChannels.get(iChannel).get(token));
        matchedFeeds.addAll(tokensOfAllRssChannels.get(jChannel).get(token));
    }
}