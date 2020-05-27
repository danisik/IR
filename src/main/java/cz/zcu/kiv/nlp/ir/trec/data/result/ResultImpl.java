package cz.zcu.kiv.nlp.ir.trec.data.result;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Třída představuje výsledek vrácený po vyhledávání.
 * Třídu můžete libovolně upravovat, popř. si můžete vytvořit vlastní třídu,
 * která dědí od abstraktní třídy {@link AbstractResult}
 */
public class ResultImpl extends AbstractResult {

    public ResultImpl(String documentID, float score) {
        this.setDocumentID(documentID);
        this.setScore(score);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getDocumentID().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResultImpl) {
            ResultImpl o = (ResultImpl) obj;
            return o.getDocumentID() == this.getDocumentID();
        }
        return false;
    }
}
