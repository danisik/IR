package cz.zcu.kiv.nlp.ir.trec.tokenizer;

import java.util.ArrayList;

/**
 * Created by tigi on 29.2.2016.
 * Tokenizer.
 */
public interface Tokenizer {
    /**
     * Metoda pro roztokenizování textu.
     * @param text - Text.
     * @return Seznam slov v textu.
     */
    ArrayList<String> tokenize(String text);

    /**
     * Smazání diakritiky v textu.
     * @param text - Text.
     * @return Text bez diakritiky.
     */
    String removeAccents(String text);
}
