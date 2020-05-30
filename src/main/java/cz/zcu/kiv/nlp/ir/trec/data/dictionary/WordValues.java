package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import gnu.trove.set.hash.THashSet;

import java.io.Serializable;
import java.util.Set;

/**
 * Třída reprezentující informace o slově.
 */
public class WordValues implements Serializable {

    /** Seznam ID dokumentů, který obsahují toto slovo. */
    private THashSet<DocumentValues> documentValues;

    /** IDF hodnota daného slova. */
    private float idf;

    /** Číslo pro serializaci */
    final static long serialVersionUID = -5097715898422114010L;

    /**
     * Constructor.
     */
    public WordValues() {
        this.documentValues = new THashSet<>();
        this.idf = 0;
    }

    /**
     * Přidá odkaz na hodnoty dokumentu.
     * @param documentValues - Hodnoty dokumentu.
     */
    public void addDocumentValues(DocumentValues documentValues) {
        this.documentValues.add(documentValues);
    }

    /**
     * Vrátí seznam ID dokumentů.
     * @return Seznam dokumentů.
     */
    public Set<DocumentValues> getDocumentValues() {
        return this.documentValues;
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
