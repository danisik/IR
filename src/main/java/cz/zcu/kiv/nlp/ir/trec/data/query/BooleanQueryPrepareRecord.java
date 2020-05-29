package cz.zcu.kiv.nlp.ir.trec.data.query;

/**
 * Třída reprezentující mezivýsledek při zpracovávání BOOLEAN query.
 */
public class BooleanQueryPrepareRecord {

    /** Kolik slov bylo v mezikroku použito. */
    private int offset;

    /** Část query obsahující NOT obalená v závorkách. */
    private String query;

    /**
     * Konstruktor.
     * @param offset - Kolik slov bylo v mezikroku použito.
     * @param query - Část query obsahující NOT obalená v závorkách.
     */
    public BooleanQueryPrepareRecord(int offset, String query) {
        this.offset = offset;
        this.query = query;
    }

    /**
     * Metoda vrací číslo reprezentující kolik slov bylo v mezikroku použito.
     * @return Kolik slov bylo v mezikroku použito.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Metoda vrací část query obsahující NOT obalená v závorkách.
     * @return Část query.
     */
    public String getQuery() {
        return query;
    }
}
