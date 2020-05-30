package cz.zcu.kiv.nlp.ir.trec.data.enums;

/**
 * Enumerátor pro typ načítajících dat.
 */
public enum EDataType {
    CRAWLERED,
    CUSTOM;

    public static EDataType getDataType(String stringDataType) {
        if (stringDataType.equals(EDataType.CRAWLERED.toString())) {
            return EDataType.CRAWLERED;
        }
        else {
            return EDataType.CUSTOM;
        }
    }
}
