/**
 * Copyright (c) 2014, Michal Konkol
 * All rights reserved.
 */
package cz.zcu.kiv.nlp.ir.trec.tokenizer;

import cz.zcu.kiv.nlp.ir.trec.utils.Utils;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 * Advanced tokenizer.
 */
public class AdvancedTokenizer implements Tokenizer {
    /** Regex, podle kterého se text tokenizuje. */
    public static final String defaultRegex =
            "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)" +
            "|" +
            "[0-9]+\\.[0-9]+\\.[0-9]*" +
            "|" +
            "[a-zA-Z]+\\*[a-zA-Z]+" +
            "|" +
            "(\\d+[.,](\\d+)?)" +
            "|" +
            "([\\p{L}\\d]+)" +
            "|" +
            "(<.*?>)" +
            "|" +
            "([\\p{Punct}])" +

            "|" +
            "(\\\")*(\\.)*([a-zA-Z]+)+(\")*(\\.)*(\\\")*(\\,)*" +
            "|" +
            "(\\…)*" +
            "|" +
            "(\\„)*([a-zA-Z]+)+(\\“)*" +
            "|" +
            "(\\\")*([a-zA-Z]+)+(\\\")*(\\:)*" +
            "|" +
            "(\\\")*([a-zA-Z]+)+(\\\")*(\\.)*(\\\")*(\\,)*" +
            "|" +
            "([a-zA-Z]+)*(\\…)*(\\.)*" +
            "|" +
            "(\\\")*([0-9]+)+(\\\")*(\\.)*(\\\")*(\\,)*" +
            "|" +
            "([0-9]+|[a-zA-Z]+)+(\\.|\\,)+$"
    ;

    /** Seznam stop slov. */
    private static List<String> stopwords;

    /**
     * Constructor.
     * @param stopwordsFilename - Filename stop slov.
     */
    public AdvancedTokenizer(String stopwordsFilename) {
        stopwords = Utils.readFromFile(stopwordsFilename);
    }

    /**
     * Metoda pro roztokenizování textu.
     * @param text - Text.
     * @param regex - Regex, podle kterého se text roztokenizuje.
     * @return Seznam slov v textu.
     */
    public static ArrayList<String> tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String word = text.substring(start, end);
            if (!stopwords.contains(word) && word.length() > 1) words.add(word);
        }

        return words;
    }

    /**
     * Smazání diakritiky v textu.
     * @param text - Text.
     * @return Text bez diakritiky.
     */
    public String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Metoda pro roztokenizování textu.
     * @param text - Text.
     * @return Seznam slov v textu.
     */
    @Override
    public ArrayList<String> tokenize(String text) {
        return tokenize(text, defaultRegex);
    }

    /**
     * Vrať všechny stopwords.
     * @return List stopwords.
     */
    @Override
    public List<String> getStopWords() {
        return stopwords;
    }
}
