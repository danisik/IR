package cz.zcu.kiv.nlp.ir.trec.tokenizer;

import java.util.ArrayList;

/**
 * Created by tigi on 29.2.2016.
 */
public interface Tokenizer {
    ArrayList<String> tokenize(String text);

    String removeAccents(String text);
}
