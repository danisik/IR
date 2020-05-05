package cz.zcu.kiv.nlp.ir.trec.math;

/**
 * Class for calculating TF-IDF.
 */
public class TFIDF {

    /**
     * Calculate idf for word in word map.
     * @param documentsCount - Documents count in input.
     * @param df - Document frequency for word.
     * @return idf for word.
     */
    public static float calculateIDF(int documentsCount, int df) {
        float idf = (float) Math.log10((float)documentsCount / df);
        return idf;
    }

    /**
     * Calculate tf idf for word.
     * @param tf - Term frequency of word in document.
     * @param idf - Inverse document frequency for word in word map.
     * @return tf idf for word.
     */
    public static float calculateTFIDF(int tf, float idf) {
        float tfidf = (1 + (float)Math.log10(tf)) * idf;
        return tfidf;
    }
}
