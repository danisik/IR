package cz.zcu.kiv.nlp.ir.trec.math;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.data.Word;

import java.util.*;

/**
 * Class for computing cosine similarity.
 */
public class CosineSimilarity {

    /**
     * Compute euclid standard for document.
     * @param values - List of document values.
     * @return Euclid standard.
     */
    public static float computeEuclidStandard(List<DocumentWordValues> values) {

        float multiplication = 0;

        // Compute multiplication.
        for (DocumentWordValues documentWordValues : values) {
            multiplication += Math.pow(documentWordValues.getTfidf(), 2);
        }

        // Compute square root.
        multiplication = (float) Math.sqrt(multiplication);

        return multiplication;
    }

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

    /**
     * Compute cosine similarity between document and query.
     * @param document - Document.
     * @param query - Query.
     * @return Cosine similarity.
     */
    public static float computeCosineSimilarity(Document document, Document query) {

        float scalarProduct = computeScalarProduct(document.getWords(), query.getWords());

        float cosineSimilarity = (scalarProduct) / (document.getEuclidStandard() * query.getEuclidStandard());

        return cosineSimilarity;
    }

    /**
     * Get K most relevant documents comparing to query.
     * @param documents - List of all documents.
     * @param query - Query for comparation.
     * @param mostRelevantDocumentsCount - Number indicates how many records will be returned.
     * @return List of K most relevant documents.
     */

    public static List<ResultImpl> getMostRelevantDocumentToQuery(List<Document> documents, Document query, int mostRelevantDocumentsCount) {
        if (mostRelevantDocumentsCount > documents.size()) {
            return null;
        }

        // Compute cosine similarity for every document-query pair and create new class "CosineSimilarityRecord".
        List<ResultImpl> allRecords = new ArrayList<>();
        for (Document document : documents) {
            allRecords.add(new ResultImpl(document.getId(), computeCosineSimilarity(document, query)));
        }

        // Sort all records - first record has the highest cosine similarity, last record has the lowest cosine similarity.
        Collections.sort(allRecords);

        // Get most relevant documents.
        List<ResultImpl> mostRelevantDocuments = allRecords.subList(0, mostRelevantDocumentsCount);

        // Set rank for sorted documents.
        for (int i = 0; i < mostRelevantDocumentsCount; i++) {
            mostRelevantDocuments.get(i).setRank(i + 1);
        }

        // Return K most relevant documents.
        return mostRelevantDocuments;
    }
}
