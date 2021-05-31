package com.cyansecurity.vlasak.rsstopicanalyzer.xml;

import com.cyansecurity.vlasak.rsstopicanalyzer.BaseTest;
import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RssFeedParserTest extends BaseTest {

    private RssFeedParser rssFeedParser;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        rssFeedParser = new RssFeedParser();
    }

    @Test
    public void GIVEN_correctRssWithOneItem_WHEN_parsed_THEN_dataFound() throws IOException {
        final List<Feed> result = rssFeedParser.parse(createResourceUri("correct_rss_with_one_item.xml"));
        final List<Feed> expected = Arrays.asList(
                new Feed("RSS Tutorial", "https://www.w3schools.com/xml/xml_rss.asp"));
        assertThat("Title and link should be found in xml.", result, is(expected));
    }

    @Test
    public void GIVEN_correctRssWithTwoItems_WHEN_parsed_THEN_dataFound() throws IOException {
        final List<Feed> result = rssFeedParser.parse(createResourceUri("correct_rss_with_two_items.xml"));
        final List<Feed> expected = Arrays.asList(
                new Feed("RSS Tutorial", "https://www.w3schools.com/xml/xml_rss.asp"),
                new Feed("XML Tutorial", "https://www.w3schools.com/xml"));
        assertThat("Title and link should be found in xml.", result, is(expected));
    }

    @Test(expectedExceptions = RssFeedParserException.class)
    public void GIVEN_malformedRss_noItemTag_WHEN_parsed_THEN_exception() throws IOException {
        rssFeedParser.parse(createResourceUri("malformed_rss_instead_of_item_is_entry_tag.xml"));
    }

    @Test(expectedExceptions = RssFeedParserException.class)
    public void GIVEN_malformedRss_noTitleTag_WHEN_parsed_THEN_exception() throws Exception {
        rssFeedParser.parse(createResourceUri("malformed_rss_instead_of_title_is_tit_tag.xml"));
    }

    @Test(expectedExceptions = RssFeedParserException.class)
    public void GIVEN_malformedRss_noLinkTag_WHEN_parsed_THEN_exception() throws Exception {
        rssFeedParser.parse(createResourceUri("malformed_rss_instead_of_link_is_lin_tag.xml"));
    }

    @Test(expectedExceptions = RssFeedParserException.class)
    public void GIVEN_malformedRss_noClosingItemTag_WHEN_parsed_THEN_exception() throws Exception {
        rssFeedParser.parse(createResourceUri("malformed_rss_missing_closing_item_tag.xml"));
    }
}