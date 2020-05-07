package cz.zcu.kiv.nlp.ir.trec.data.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DocumentValues implements Serializable {

    private Map<String, DocumentWordValues> wordValues;
    private float euclidStandard;

    final static long serialVersionUID = -8237428427114010L;

    public DocumentValues() {
        wordValues = new HashMap<>();
    }

    public void addWord(String word) {
        if (!wordValues.containsKey(word)) {
            wordValues.put(word, new DocumentWordValues());
        }
        wordValues.get(word).incrementTf();
    }

    public void setEuclidStandard(float euclidStandard) {
        this.euclidStandard = euclidStandard;
    }

    public float getEuclidStandard() {
        return euclidStandard;
    }

    public Map<String, DocumentWordValues> getWordValues() { return  wordValues; }
}
