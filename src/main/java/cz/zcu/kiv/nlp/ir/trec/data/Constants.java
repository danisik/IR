package cz.zcu.kiv.nlp.ir.trec.data;

/**
 * Třída pro definici všech konstant.
 */
public class Constants {

    /** Název souboru pro stop slova. */
    public static final String FILENAME_STOPWORDS = "stopwords/czech-stopwords.txt";
    /** Název souboru pro stažená data. */
    public static final String FILENAME_CUSTOM_DATA = "data/mmreality_crawlered_data.json";
    /** Název souboru pro uložení indexace školních dat. */
    public static final String FILENAME_DATA_INDEX_CUSTOM = "data/indexed_data_custom.bin";
    /** Název souboru pro uložení indexace stažených dat. */
    public static final String FILENAME_DATA_INDEX_CRAWLERED = "data/indexed_data_crawlered.bin";

    /** Číslo pro zaokrouhlování. */
    public static final int ROUNDING_NUMBER = 10000;

    /** Prefix pro Id dokumentu. */
    public static final String DOCUMENT_ID_PREFIX = "d";
}
