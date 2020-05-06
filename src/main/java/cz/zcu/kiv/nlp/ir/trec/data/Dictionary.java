package cz.zcu.kiv.nlp.ir.trec.data;

import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;

import java.io.Serializable;
import java.util.*;

public class Dictionary implements Serializable {

    private Map<String, Set<Integer>> invertedIndex;
    private Map<String, Float> words;

    final static long serialVersionUID = -5097715898427114010L;

    public Dictionary() {
        this.words = new HashMap<>();
        this.invertedIndex = new HashMap<>();
    }

    public void addWord(String key) {
        this.words.put(key, (float) 0);
    }

    public boolean containsWord(String key) {
        return words.containsKey(key);
    }

    public void addDocument(String key, int docIDinList) {

        if (!invertedIndex.containsKey(key)) {
            invertedIndex.put(key, new HashSet<>());
        }
        invertedIndex.get(key).add(docIDinList);
    }

    public void calculateIDF(int documentsCount) {
        for (String word : words.keySet()) {
            Set<Integer> set = invertedIndex.get(word);
            int df = set.size();
            words.put(word, TFIDF.calculateIDF(documentsCount, df));
        }
    }

    public Map<String, Float> getWords() {
        return words;
    }
}
