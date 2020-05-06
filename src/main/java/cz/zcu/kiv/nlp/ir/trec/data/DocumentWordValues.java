package cz.zcu.kiv.nlp.ir.trec.data;

public class DocumentWordValues {

    private float tfidf;

    private int tf;

    public DocumentWordValues() {
        this.tf = 0;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public void incrementTf() { this.tf += 1; }

    public float getTfidf() {
        return tfidf;
    }

    public void setTfidf(float tfidf) {
        this.tfidf = tfidf;
    }
}
