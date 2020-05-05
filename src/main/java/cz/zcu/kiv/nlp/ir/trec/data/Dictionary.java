package cz.zcu.kiv.nlp.ir.trec.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary implements Serializable {

    private Map<String, ArrayList<Document>> documents;
    private Map<String, Float> words;

    final static long serialVersionUID = -5097715898427114010L;

    public Dictionary() {
        this.words = new HashMap<>();
        this.documents = new HashMap<>();
    }

    public void addWord(String key, Float idf) {
        words.put(key, idf);
    }

    public Float getWordIdf(String key) {
        return words.get(key);
    }

    public Map<String, Float> getAllWords() {
        return words;
    }

    public void addDocument(String key, Document document) {

        if (!documents.containsKey(key)) {
            documents.put(key, new ArrayList<>());
        }
        documents.get(key).add(document);
    }

    public List<Document> getDocuments(String key) {
        return documents.get(key);
    }

    public Map<String, ArrayList<Document>> getAllDocuments() {
        return documents;
    }
}
