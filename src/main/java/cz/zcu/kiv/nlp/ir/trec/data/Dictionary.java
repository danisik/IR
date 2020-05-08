package cz.zcu.kiv.nlp.ir.trec.data;

import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;

import java.io.Serializable;
import java.util.*;

/**
 * Slovník obsahující veškerá zaindexovaná data.
 */
public class Dictionary implements Serializable {

    /** Invertovaný index. */
    private Map<String, Set<String>> invertedIndex;
    /** Seznam všech slov ve slovníku. */
    private Map<String, Float> words;
    /** Seznam hodnot dokumentů podle documentId. */
    private Map<String, DocumentValues> documentValues;

    final static long serialVersionUID = -5097715898427114010L;

    /**
     * Constructor.
     */
    public Dictionary() {
        this.words = new HashMap<>();
        this.invertedIndex = new HashMap<>();
        this.documentValues = new HashMap<>();
    }

    /**
     * Přidej slovo do slovníku slov.
     * @param word - Slovo.
     */
    public void addWord(String word) {
        this.words.put(word, (float) 0);
    }

    /**
     * Zjištění, zda zadané slovo se vyskytuje ve slovníku slov.
     * @param word - Slovo.
     * @return True pokud se slovo nachází ve slovníku.
     */
    public boolean containsWord(String word) {
        return words.containsKey(word);
    }

    /**
     * Přidání id dokumentu do invertovaného indexu.
     * @param word - Slovo.
     * @param documentId - Id dokumentu.
     */
    public void addDocumentId(String word, String documentId) {

        if (!invertedIndex.containsKey(word)) {
            invertedIndex.put(word, new HashSet<>());
        }
        invertedIndex.get(word).add(documentId);
    }

    /**
     * Spočítání IDF pro dané slovo ve slovníku.
     * @param documentsCount - Počet všech dokumentů.
     */
    public void calculateIDF(int documentsCount) {
        for (String word : words.keySet()) {
            Set<String> set = invertedIndex.get(word);
            int df = set.size();
            words.put(word, TFIDF.calculateIDF(documentsCount, df));
        }
    }

    /**
     * Přidání daného slova do dokument slovníku.
     * @param documentId - Id dokumentu.
     * @param word - Slovo.
     */
    public void addDocumentWord(String documentId, String word) {
        if (!documentValues.containsKey(documentId)) {
            documentValues.put(documentId, new DocumentValues());
        }
        documentValues.get(documentId).addWord(word);
    }

    /**
     * Metoda pro získání slovníku slov.
     * @return Slovník slov.
     */
    public Map<String, Float> getWords() {
        return words;
    }

    /**
     * Získání všech hodnot dokumentů.
     * @return Mapa hodnot dokumentů.
     */
    public Map<String, DocumentValues> getDocumentValues() { return documentValues; }

    /**
     * Získání hodnot dokumentu podle jeho Id.
     * @param documentId - Id dokumentu.
     * @return Hodnoty dokumentu.
     */
    public DocumentValues getDocumentValuesById(String documentId) { return documentValues.get(documentId); }
}
