package cz.zcu.kiv.nlp.ir.trec.data;

import java.io.Serializable;

public class DocumentWordValues implements Serializable {

    private float tfidf;

    private int tf;

    final static long serialVersionUID = -4321715898427114010L;

    public DocumentWordValues() {
        this.tf = 0;
    }

    public DocumentWordValues(int tf) {
        this.tf = tf;
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
