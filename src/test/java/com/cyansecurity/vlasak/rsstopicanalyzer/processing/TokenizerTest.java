package com.cyansecurity.vlasak.rsstopicanalyzer.processing;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws IOException {
        tokenizer = new Tokenizer();
    }

    @DataProvider
    public Object[][] getTitleTogetherWithExpectedTokens(){
        return new Object[][] {
                {"Two \"ultra-Orthodox\" Israelis killed, 160 injured! - CNN",
                        new String[]{"two", "ultra-orthodox", "israelis", "killed", "160", "injured", "cnn"},
                        new String[]{"two", "ultra-orthodox", "israelis", "killed", "160", "injured", "cnn"}},
                {"‘I’m troubled by it’: Dems trash Biden’s handling.",
                        new String[]{"i’m", "troubled", "by", "it", "dems", "trash", "biden’s", "handling"},
                        new String[]{"troubled", "dems", "trash", "biden’s", "handling"}},
                {"Humans Are Noisy -- (Not Talking About Others)",
                        new String[]{"humans", "are", "noisy", "not", "talking", "about", "others"},
                        new String[]{"humans", "noisy", "talking"}},
                {"Opinion | Is B.M.I. a Scam?",
                        new String[]{"opinion", "is", "b.m.i.", "a", "scam"},
                        new String[]{"opinion", "b.m.i.", "scam"}},
                {"Star of ‘Beethoven’ and ‘Heartbreak Kid’; Dies at 86",
                        new String[]{"star", "of", "beethoven", "and", "heartbreak", "kid", "dies", "at", "86"},
                        new String[]{"star", "beethoven", "heartbreak", "kid", "dies", "86"}},
                {"Election “Audit” In Arizona and Mr. Farley 'SNL' simply lies",
                        new String[]{"election", "audit", "in", "arizona", "and", "mr.", "farley", "snl", "simply", "lies"},
                        new String[]{"election", "audit", "arizona", "mr.", "farley", "snl", "lies"}}
        };
    }

    @Test(dataProvider = "getTitleTogetherWithExpectedTokens")
    public void testTokenizingTogetherWithCleaningFillers(final String title, final String[] expectedTokens,
                                                          final String[] expectedTokensWithoutFillers) {
        final Set<String> tokens = tokenizer.tokenizeAndConvertToLower(title).getTokens();
        assertThat("Title was incorrectly tokenized",
                new ArrayList<>(tokens), containsInAnyOrder(expectedTokens));
        final Set<String> tokensWithoutFillers = tokenizer.removeFillers().getTokens();
        assertThat("Not all fillers were removed",
                new ArrayList<>(tokensWithoutFillers), containsInAnyOrder(expectedTokensWithoutFillers));
    }
}