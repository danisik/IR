package cz.zcu.kiv.nlp.ir.trec.data;

/**
 * Třída pro definici všech konstant.
 */
public class Constants {

    /** Output složka. */
    public static final String OUTPUT_DIR = "./TREC";

    /** Název souboru pro stop slova. */
    public static final String FILENAME_STOPWORDS = "stopwords/czech-stopwords.txt";
    /** Název souboru pro školní data. */
    public static final String FILENAME_CUSTOM_DATA = OUTPUT_DIR + "/czechData.bin";
    /** Název souboru pro stažená data. */
    public static final String FILENAME_CRAWLERED_DATA = OUTPUT_DIR + "/mmreality_crawlered_data.json";
    /** Název souboru obsahující topicy. */
    public static final String FILENAME_TOPIC_DATA = OUTPUT_DIR + "/topicData.bin";

    /** Název souboru pro uložení indexace školních dat. */
    public static final String FILENAME_DATA_INDEX_CUSTOM = "data/indexed_data_custom.bin";
    /** Název souboru pro uložení indexace stažených dat. */
    public static final String FILENAME_DATA_INDEX_CRAWLERED = "data/indexed_data_crawlered.bin";

    /** Číslo pro zaokrouhlování. */
    public static final int ROUNDING_NUMBER = 10000;
    /** Velikost bufferů pro načítání / ukládání objektů. */
    public static final int BUFFER_STREAM_SIZE = 16 * 1024 * 1024;

    /** Prefix pro Id dokumentu. */
    public static final String DOCUMENT_ID_PREFIX = "d";
}
