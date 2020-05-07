package cz.zcu.kiv.nlp.ir.trec.math;

import cz.zcu.kiv.nlp.ir.trec.data.Dictionary;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultComparator;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;

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
    public static float computeScalarProduct(Map<String, DocumentWordValues> documentValues, Map<String, DocumentWordValues> queryValues) {

        float scalarProduct = 0;

        for (String word : documentValues.keySet()) {
            if (queryValues.containsKey(word)) {
                scalarProduct += (queryValues.get(word).getTfidf() * documentValues.get(word).getTfidf());
            }
        }

        return scalarProduct;
    }

    public static float computeCosineSimilarity(DocumentValues document, DocumentValues query) {
        float scalarProduct = computeScalarProduct(document.getWordValues(), query.getWordValues());

        float cosineSimilarity = (scalarProduct) / (document.getEuclidStandard() * query.getEuclidStandard());

        return cosineSimilarity;
    }

    public static List<Result> getMostRelevantDocumentToQuery(Dictionary dictionary, DocumentValues query, int mostRelevantDocumentsCount) {
        Map<String, DocumentValues> documentValues = dictionary.getDocumentValues();

        if (mostRelevantDocumentsCount > documentValues.size()) {
            return null;
        }

        // Compute cosine similarity for every document-query pair.
        List<Result> allRecords = new ArrayList<>();
        for (String documentId : documentValues.keySet()) {
            DocumentValues document = documentValues.get(documentId);
            allRecords.add(new ResultImpl(documentId, computeCosineSimilarity(document, query)));
        }

        Collections.sort(allRecords, new ResultComparator());

        // Get most relevant documents.
        List<Result> mostRelevantDocuments = allRecords.subList(0, mostRelevantDocumentsCount);

        for (int i = 0; i < mostRelevantDocumentsCount; i++) {
            Result result = mostRelevantDocuments.get(i);
            if (result instanceof ResultImpl) {
                ((ResultImpl)result).setRank(i + 1);
            }
        }

        // Return K most relevant documents.
        return mostRelevantDocuments;
    }
}
