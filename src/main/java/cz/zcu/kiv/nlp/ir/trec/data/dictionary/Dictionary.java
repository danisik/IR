package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.Serializable;
import java.util.*;

/**
 * Slovník obsahující veškerá zaindexovaná data.
 */
public class Dictionary implements Serializable {

    /** Seznam všech slov ve slovníku. */
    private THashMap<String, WordValues> words;

    /** Počet zaindexovaných dokumentů. */
    private int indexedDocuments = 0;

    final static long serialVersionUID = -5097715898427114010L;

    /**
     * Constructor.
     */
    public Dictionary() {
        this.words = new THashMap<>();
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
     * Přidání hodnoty dokumentu do invertovaného indexu.
     * @param word - Slovo.
     * @param documentValues - Hodnoty dokumentu.
     */
    public void addDocumentValues(String word, DocumentValues documentValues) {
        this.words.get(word).addDocumentValues(documentValues);
    }

    /**
     * Spočítání IDF pro dané slovo ve slovníku.
     */
    public void calculateIDF() {
        for (String word : words.keySet()) {
            WordValues wordValues = words.get(word);
            Set<DocumentValues> set = wordValues.getDocumentValues();
            int df = set.size();
            wordValues.setIdf(TFIDF.calculateIDF(indexedDocuments, df));
        }
    }

    /**
     * Get set of documentValues, which contains at least 1 word from query.
     * @param query - Query.
     * @return Set of documentValues.
     */
    public THashSet<DocumentValues> getDocumentIDsForQuery(DocumentValues query) {
        THashSet<DocumentValues> documentValues = new THashSet<>();

        Map<String, DocumentWordValues> queryWords = query.getWordValues();
        for (String word : queryWords.keySet()) {
            if (!words.containsKey(word)) continue;

            documentValues.addAll(words.get(word).getDocumentValues());
        }

        return documentValues;
    }

    public THashSet<DocumentValues> getAllDocumentValues() {
        THashSet<DocumentValues> documentValues = new THashSet<>();

        for (String word : words.keySet()) {
            documentValues.addAll(words.get(word).getDocumentValues());
        }

        return documentValues;
    }

    /**
     * Metoda pro získání slovníku slov.
     * @return Slovník slov.
     */
    public THashMap<String, WordValues> getWords() {
        return words;
    }

    /**
     * Vrátí hodnoty slova pro dané slovo.
     * @param word - Slovo.
     * @return Hodnoty slova.
     */
    public WordValues getWordValues(String word) { return words.get(word); }

    /**
     * Přičtení counteru pro zaindexované dokumenty.
     */
    public void incrementIndexedDocuments() {
        indexedDocuments++;
    }
}
