package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;

import java.io.Serializable;
import java.util.*;

/**
 * Slovník obsahující veškerá zaindexovaná data.
 */
public class Dictionary implements Serializable {

    /** Seznam všech slov ve slovníku. */
    private Map<String, WordValues> words;

    /** Seznam hodnot dokumentů podle documentId. */
    private Map<String, DocumentValues> documentValues;

    final static long serialVersionUID = -5097715898427114010L;

    /**
     * Constructor.
     */
    public Dictionary() {
        this.words = new HashMap<>();
        this.documentValues = new HashMap<>();
    }

    /**
     * Přidej slovo do slovníku slov.
     * @param word - Slovo.
     */
    public void addWord(String word) {
        this.words.put(word, new WordValues());
    }

    /**
     * Zjištění, zda zadané slovo se vyskytuje ve slovníku slov.
     * @param word - Slovo.
     * @return True pokud se slovo nachází ve slovníku.
     */
    public boolean containsWord(String word) {
        return this.words.containsKey(word);
    }

    /**
     * Přidání id dokumentu do invertovaného indexu.
     * @param word - Slovo.
     * @param documentId - Id dokumentu.
     */
    public void addDocumentId(String word, String documentId) {
        this.words.get(word).addDocumentID(documentId);
    }

    /**
     * Spočítání IDF pro dané slovo ve slovníku.
     * @param documentsCount - Počet všech dokumentů.
     */
    public void calculateIDF(int documentsCount) {
        for (String word : words.keySet()) {
            WordValues wordValues = words.get(word);
            Set<String> set = wordValues.getDocumentIDs();
            int df = set.size();
            wordValues.setIdf(TFIDF.calculateIDF(documentsCount, df));
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

    public Set<String> getDocumentIDsForQuery(DocumentValues query) {

        Set<String> documentIDs = new HashSet<>();

        Map<String, DocumentWordValues> queryWords = query.getWordValues();
        for (String word : queryWords.keySet()) {
            documentIDs.addAll(words.get(word).getDocumentIDs());
        }

        return documentIDs;
    }

    /**
     * Metoda pro získání slovníku slov.
     * @return Slovník slov.
     */
    public Map<String, WordValues> getWords() {
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
