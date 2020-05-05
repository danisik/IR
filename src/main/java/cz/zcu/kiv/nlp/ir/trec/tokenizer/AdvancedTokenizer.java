/**
 * Copyright (c) 2014, Michal Konkol
 * All rights reserved.
 */
package cz.zcu.kiv.nlp.ir.trec.tokenizer;

import cz.zcu.kiv.nlp.ir.trec.data.Constants;
import cz.zcu.kiv.nlp.ir.trec.utils.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 */
public class AdvancedTokenizer implements Tokenizer {
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

    public static String[] tokenize(String text, String regex, String stopwordsFilename) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);

        ArrayList<String> stopWords = Utils.readFromFile(stopwordsFilename);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String word = text.substring(start, end);
            if (!stopWords.contains(word)) words.add(word);
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }

    public String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public String[] tokenize(String text, String stopwordsFilename) {
        return tokenize(text, defaultRegex, stopwordsFilename);
    }
}
