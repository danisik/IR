package cz.zcu.kiv.nlp.ir.trec.data;

import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;

import java.io.Serializable;
import java.util.*;

public class Dictionary implements Serializable {

    private Map<String, Set<String>> invertedIndex;
    private Map<String, Float> words;
    private Map<String, DocumentValues> documentValues;


    final static long serialVersionUID = -5097715898427114010L;

    public Dictionary() {
        this.words = new HashMap<>();
        this.invertedIndex = new HashMap<>();
        this.documentValues = new HashMap<>();
    }

    public void addWord(String key) {
        this.words.put(key, (float) 0);
    }

    public boolean containsWord(String key) {
        return words.containsKey(key);
    }

    public void addDocumentId(String key, String documentId) {

        if (!invertedIndex.containsKey(key)) {
            invertedIndex.put(key, new HashSet<>());
        }
        invertedIndex.get(key).add(documentId);
    }

    public void calculateIDF(int documentsCount) {
        for (String word : words.keySet()) {
            Set<String> set = invertedIndex.get(word);
            int df = set.size();
            words.put(word, TFIDF.calculateIDF(documentsCount, df));
        }
    }

    public void addDocumentWord(String documentId, String word) {
        if (!documentValues.containsKey(documentId)) {
            documentValues.put(documentId, new DocumentValues());
        }
        documentValues.get(documentId).addWord(word);
    }

    public Map<String, Float> getWords() {
        return words;
    }

    public Map<String, DocumentValues> getDocumentValues() { return documentValues; }

    public DocumentValues getDocumentValuesById(String documentId) { return documentValues.get(documentId); }
}
