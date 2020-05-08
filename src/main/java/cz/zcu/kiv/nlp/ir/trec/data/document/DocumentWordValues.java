package cz.zcu.kiv.nlp.ir.trec.data.document;

import java.io.Serializable;

/**
 * Třída obsahující data pro slovo ve slovníku.
 */
public class DocumentWordValues implements Serializable {

    /** TFIDF hodnota. */
    private float tfidf;

    /** TF hodnota. */
    private int tf;

    final static long serialVersionUID = -4321715898427114010L;

    /**
     * Constructor.
     */
    public DocumentWordValues() {
        this.tf = 0;
    }

    /**
     * Constructor.
     */
    public DocumentWordValues(int tf) {
        this.tf = tf;
    }

    /**
     * Získání hodnoty TF.
     * @return TF hodnota.
     */
    public int getTf() {
        return tf;
    }

    /**
     * Nastavení TF hodnoty.
     * @param tf - TF hodnota.
     */
    public void setTf(int tf) {
        this.tf = tf;
    }

    /**
     * Incrementace TF hodnoty.
     */
    public void incrementTf() { this.tf += 1; }

    /**
     * Vrácení TFIDF hodnoty.
     * @return TFIDF hodnota.
     */
    public float getTfidf() {
        return tfidf;
    }

    /**
     * Nastavení TFIDF hodnoty.
     * @param tfidf - TFIDF hodnota.
     */
    public void setTfidf(float tfidf) {
        this.tfidf = tfidf;
    }
}
