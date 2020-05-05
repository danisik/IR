package cz.zcu.kiv.nlp.ir.trec.tokenizer;

/**
 * Created by tigi on 29.2.2016.
 */
public interface Tokenizer {
    String[] tokenize(String text, String stopwordsFilename);

    String removeAccents(String text);
}
