package cz.zcu.kiv.nlp.ir.trec.math;

import cz.zcu.kiv.nlp.ir.trec.data.dictionary.Dictionary;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultComparator;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import gnu.trove.set.hash.THashSet;

import java.util.*;

/**
 * Class for computing cosine similarity.
 */
public class CosineSimilarity {

    /**
     * Compute scalar product between document and query.
     * @param documentValues - Document values.
     * @param queryValues - Query values.
     * @return Scalar product.
     */
    private static float computeScalarProduct(Map<String, DocumentWordValues> documentValues, Map<String, DocumentWordValues> queryValues) {

        float scalarProduct = 0;

        for (String word : documentValues.keySet()) {
            if (queryValues.containsKey(word)) {
                scalarProduct += (queryValues.get(word).getTfidf() * documentValues.get(word).getTfidf());
            }
        }

        return scalarProduct;
    }

    /**
     * Výpočet cosinovy podobnosti mezi dokumentem a query.
     * @param document - Dokument.
     * @param query - Query.
     * @return Cosinova podobnost.
     */
    private static float computeCosineSimilarity(DocumentValues document, DocumentValues query) {
        float scalarProduct = computeScalarProduct(document.getWordValues(), query.getWordValues());

        return (scalarProduct) / (document.getEuclidStandard() * query.getEuclidStandard());
    }

    /**
     * Metoda pro získání dokumentů k zadané query.
     * @param dictionary - Slovník.
     * @param query - Query.
     * @return List výsledků.
     */
    public static List<Result> geRelevantDocumentsToQuery(Dictionary dictionary, DocumentValues query) {

        // Get all documents, which contains at least one word as query.
        THashSet<DocumentValues> documentValues = dictionary.getDocumentIDsForQuery(query);

        // Compute cosine similarity for document-query pair.
        List<Result> allRecords = new ArrayList<>();
        for (DocumentValues documentValue : documentValues) {
            allRecords.add(new ResultImpl(documentValue.getDocumentID(), computeCosineSimilarity(documentValue, query)));
        }

        allRecords.sort(new ResultComparator());

        return allRecords;
    }
}
