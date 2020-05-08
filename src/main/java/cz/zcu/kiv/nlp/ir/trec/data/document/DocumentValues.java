package cz.zcu.kiv.nlp.ir.trec.data.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída obsahující hodnoty pro dokument.
 */
public class DocumentValues implements Serializable {

    /** Slovník slov. */
    private Map<String, DocumentWordValues> wordValues;
    /** Střední hodnota. */
    private float euclidStandard;

    final static long serialVersionUID = -8237428427114010L;

    /**
     * Constructor.
     */
    public DocumentValues() {
        wordValues = new HashMap<>();
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
    public Map<String, DocumentWordValues> getWordValues() { return  wordValues; }
}