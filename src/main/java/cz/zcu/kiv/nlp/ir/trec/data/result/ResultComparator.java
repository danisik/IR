package cz.zcu.kiv.nlp.ir.trec.data.result;

import java.util.Comparator;

public class ResultComparator implements Comparator<Result> {

    @Override
    public int compare(Result r1, Result r2) {
        // Sort all records - first record has the highest cosine similarity, last record has the lowest cosine similarity.
        if (r1.getScore() > r2.getScore()) return -1;
        if (r1.getScore() == r2.getScore()) return 0;
        return 1;
    }
}
