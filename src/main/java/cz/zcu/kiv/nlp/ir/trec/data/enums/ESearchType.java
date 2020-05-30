package cz.zcu.kiv.nlp.ir.trec.data.enums;

/**
 * Enumerator pro typ vyhledávání.
 */
public enum ESearchType {
    BOOLEAN,
    SVM;

    public static ESearchType getSearchType(String stringSearchType) {
        if (stringSearchType.equals(ESearchType.BOOLEAN.toString())) {
            return ESearchType.BOOLEAN;
        }
        else {
            return ESearchType.SVM;
        }
    }
}
