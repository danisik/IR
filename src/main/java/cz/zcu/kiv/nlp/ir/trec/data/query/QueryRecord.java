package cz.zcu.kiv.nlp.ir.trec.data.query;

import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import gnu.trove.set.hash.THashSet;
import org.apache.lucene.search.BooleanClause;

/**
 * Query record.
 */
public class QueryRecord {

    /** Operátor pro danou klauzuli. */
    private BooleanClause.Occur occur;

    /** Seznam výsledků pro danou klauzuli. */
    private THashSet<String> results;

    /**
     * Konstruktor.
     * @param occur - Operátor.
     * @param results - Seznam výsledků.
     */
    public QueryRecord(BooleanClause.Occur occur, THashSet<String> results) {
        this.occur = occur;
        this.results = results;
    }

    /**
     * Získání operátoru.
     * @return Operátor.
     */
    public BooleanClause.Occur getOccur() {
        return this.occur;
    }

    /**
     * Získání výsledků.
     * @return Seznam výsledků.
     */
    public THashSet<String> getResults() {
        return this.results;
    }
}
