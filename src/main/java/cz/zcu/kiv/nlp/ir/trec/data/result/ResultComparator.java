package cz.zcu.kiv.nlp.ir.trec.data.result;

import java.util.Comparator;

/**
 * Komparátor pro výsledky.
 */
public class ResultComparator implements Comparator<Result> {

    /**
     * Porovná dva výsledky, kdy největší bude vždy první.
     * @param r1 - První výsledek.
     * @param r2 - Druhý výsledek.
     * @return Hodnota reprezentující zda výsledek 1 je větší / menší / stejný jak výsledek 2.
     */
    @Override
    public int compare(Result r1, Result r2) {
        // Sort all records - first record has the highest cosine similarity, last record has the lowest cosine similarity.
        if (r1.getScore() > r2.getScore()) return -1;
        if (r1.getScore() == r2.getScore()) return 0;
        return 1;
    }
}
