package cz.zcu.kiv.nlp.ir.trec.data.document;

import gnu.trove.map.hash.THashMap;

import java.io.Serializable;

/**
 * Třída obsahující hodnoty pro dokument.
 */
public class DocumentValues implements Serializable {

    /** ID dokumentu. */
    private String documentID;
    /** Slovník slov. */
    private THashMap<String, DocumentWordValues> wordValues;
    /** Střední hodnota. */
    private float euclidStandard;

    final static long serialVersionUID = -8237428427114010L;

    /**
     * Constructor.
     */
    public DocumentValues() {
        wordValues = new THashMap<>();
    }

    /**
     * Přidání slova do slovníku.
     * @param word - Slovo.
     */
    public void addWord(String word) {
        if (!wordValues.containsKey(word)) {
            wordValues.put(word, new DocumentWordValues());
        }
        wordValues.get(word).incrementTf();
    }

    /**
     * Nastavení střední hodnoty.
     * @param euclidStandard - Střední hodnota.
     */
    public void setEuclidStandard(float euclidStandard) {
        this.euclidStandard = euclidStandard;
    }

    /**
     * Získání střední hodnoty.
     * @return Střední hodnota.
     */
    public float getEuclidStandard() {
        return euclidStandard;
    }

    /**
     * Získání slovníku slov.
     * @return - Slovník slov.
     */
    public THashMap<String, DocumentWordValues> getWordValues() { return  wordValues; }

    /**
     * Nastaví ID dokumentu.
     * @param documentID - ID dokumentu.
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     * Získání id dokumentu.
     * @return ID dokumentu.
     */
    public String getDocumentID() {
        return documentID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getDocumentID().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DocumentValues) {
            DocumentValues o = (DocumentValues) obj;
            return o.getDocumentID() == this.getDocumentID();
        }
        return false;
    }
}
