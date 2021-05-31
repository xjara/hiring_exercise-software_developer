package com.cyansecurity.vlasak.rsstopicanalyzer.processing;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Tokenizer {

    private static final String BASE_PATH = "src/main/resources/static/";
    private static final String PATH_TO_CONJUNCTIONS_FILE = BASE_PATH + "conjunctions.txt";
    private static final String PATH_TO_PRONOUNS_FILE = BASE_PATH + "pronouns.txt";
    private static final String PATH_TO_PREPOSITIONS_FILE = BASE_PATH + "prepositions.txt";
    private static final String PATH_TO_ADVERBS_FILE = BASE_PATH + "adverbs.txt";
    private static final String PATH_TO_ADJECTIVES_FILE = BASE_PATH + "adjectives.txt";
    private static final String PATH_TO_MODAL_VERBS_FILE = BASE_PATH + "modal_verbs.txt";
    private static final String PATH_TO_ARTICLES_FILE = BASE_PATH + "articles.txt";
    private static final String PATH_TO_WITHOUT_CATEGORY_FILE = BASE_PATH + "without_category.txt";

    private Set<String> conjunctionsSet;
    private Set<String> prepositionsSet;
    private Set<String> pronounsSet;
    private Set<String> adverbsSet;
    private Set<String> adjectivesSet;
    private Set<String> modalVerbsSet;
    private Set<String> articlesSet;
    private Set<String> withoutCategorySet;
    // only unique tokens are relevant
    private Set<String> tokens;

    public Tokenizer() throws IOException {
        conjunctionsSet = Collections.unmodifiableSet(readData(PATH_TO_CONJUNCTIONS_FILE));
        prepositionsSet = Collections.unmodifiableSet(readData(PATH_TO_PREPOSITIONS_FILE));
        pronounsSet = Collections.unmodifiableSet(readData(PATH_TO_PRONOUNS_FILE));
        adverbsSet = Collections.unmodifiableSet(readData(PATH_TO_ADVERBS_FILE));
        adjectivesSet = Collections.unmodifiableSet(readData(PATH_TO_ADJECTIVES_FILE));
        modalVerbsSet = Collections.unmodifiableSet(readData(PATH_TO_MODAL_VERBS_FILE));
        articlesSet = Collections.unmodifiableSet(readData(PATH_TO_ARTICLES_FILE));
        withoutCategorySet = Collections.unmodifiableSet(readData(PATH_TO_WITHOUT_CATEGORY_FILE));
    }

    private Set<String> readData(final String fileName) throws IOException{
        try (final BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            // first line of the file has to be skipped - it contains info about the source
            return br.lines().skip(1).collect(Collectors.toSet());
        }
    }

    // todo: tokens could be find by regex, so removing of useles symbols wouldn't be necessary
    public Tokenizer tokenizeAndConvertToLower(@Nonnull String text) {
        // remove useless symbols
        text = text.replaceAll("\\(|\\)|\"|“|”|\\?|!|\\:|;|\\,|\\.$|\\|", "");
        text = text.replaceAll(" - |--", " ");
        tokens = Arrays.asList(text.split(" "))
                .stream()
                .filter(w -> !w.isEmpty())
                .map(w -> removeUnecessaryCharsFromStartAndEnd(w))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        return this;
    }

    public Set<String> getTokens() {
        return tokens;
    }

    public String removeUnecessaryCharsFromStartAndEnd(String text) {
        if (text.startsWith("‘") || text.startsWith("'")) {
            text = text.substring(1);
        }
        if (text.endsWith("’") || text.endsWith("'")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public Tokenizer removeFillers() {
        tokens = tokens.stream()
                .filter(this::isNotFiller)
                .collect(Collectors.toSet());
        return this;
    }

    private boolean isNotFiller(final String word) {
        return !conjunctionsSet.contains(word) &&
                !prepositionsSet.contains(word) &&
                !pronounsSet.contains(word) &&
                !adverbsSet.contains(word) &&
                !adjectivesSet.contains(word) &&
                !modalVerbsSet.contains(word) &&
                !articlesSet.contains(word) &&
                !withoutCategorySet.contains(word);
    }
}
