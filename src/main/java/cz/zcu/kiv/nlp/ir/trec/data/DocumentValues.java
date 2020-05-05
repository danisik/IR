package cz.zcu.kiv.nlp.ir.trec.data;

public class DocumentValues {

    private double tfidf;

    private int tf;

    public DocumentValues(int tf) {
        this.tf = tf;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public void incrementTf() { this.tf += 1; }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }
}
