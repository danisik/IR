package cz.zcu.kiv.nlp.ir.trec.data;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Třída představuje výsledek vrácený po vyhledávání.
 * Třídu můžete libovolně upravovat, popř. si můžete vytvořit vlastní třídu,
 * která dědí od abstraktní třídy {@link AbstractResult}
 */
public class ResultImpl extends AbstractResult implements Comparable<ResultImpl> {

    public ResultImpl(String documentID, float score) {
        this.setDocumentID(documentID);
        this.setScore(score);
    }

    @Override
    public int compareTo(ResultImpl cosineSimilarityRecord) {
        if (this.getScore() > cosineSimilarityRecord.getScore()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
