package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Třída reprezentující informace o slově.
 */
public class WordValues implements Serializable {

    /** Seznam ID dokumentů, který obsahují toto slovo. */
    private Set<String> documentIDs;

    /** IDF hodnota daného slova. */
    private float idf;

    final static long serialVersionUID = -5097715898422114010L;

    /**
     * Constructor.
     */
    public WordValues() {
        this.documentIDs = new HashSet<>();
        this.idf = 0;
    }

    /**
     * Přidá ID dokumentu do seznamu.
     * @param documentID - ID dokumentu.
     */
    public void addDocumentID(String documentID) {
        this.documentIDs.add(documentID);
    }

    /**
     * Vrátí seznam ID dokumentů.
     * @return Seznam dokumentů.
     */
    public Set<String> getDocumentIDs() {
        return this.documentIDs;
    }

    /**
     * Nastaví IDF hodnotu slova.
     * @param idf - IDF hodnota.
     */
    public void setIdf(float idf) {
        this.idf = idf;
    }

    /**
     * Vrátí IDF hodnotu slova.
     * @return IDF hodnota.
     */
    public float getIdf() {
        return this.idf;
    }
}
