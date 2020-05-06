package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.*;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;
import cz.zcu.kiv.nlp.ir.trec.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.Tokenizer;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tigi
 *
 * Třída reprezentující index.
 *
 * Tuto třídu doplňte tak aby implementovala rozhranní {@link Indexer} a {@link Searcher}.
 * Pokud potřebujete, přidejte další rozhraní, která tato třída implementujte nebo
 * přidejte metody do rozhraní {@link Indexer} a {@link Searcher}.
 *
 *
 */
public class Index implements Indexer, Searcher {

    private static Logger log = Logger.getLogger(Index.class);

    private Dictionary dictionary;

    private Stemmer stemmer;
    private Tokenizer tokenizer;
    private boolean removeAccentsBeforeStemming;
    private boolean removeAccentsAfterStemming;
    private boolean toLowercase;

    public Index(Stemmer stemmer, Tokenizer tokenizer, boolean removeAccentsBeforeStemming, boolean removeAccentsAfterStemming, boolean toLowercase) {
        this.dictionary = new Dictionary();
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;

        log.info("Stemmer: " + stemmer.getClass().getSimpleName());
        log.info("Tokenizer: " + tokenizer.getClass().getSimpleName());
        log.info("Accents removed before steming: " + removeAccentsBeforeStemming);
        log.info("Accents removed after steming: " + removeAccentsAfterStemming);
        log.info("To lower case: " + toLowercase);
    }

    public void index(List<Document> documents) {
        //  todo implement - implementovat jako přidávání do words, ne jako inicializaci words.

        long startTime = System.currentTimeMillis();
        log.info("Start indexing.");
        // Iterate through every document.
        int i = 0;
        for (Document document : documents) {
            document.initWords();
            String line = document.getDataForPreprocessing();

            if (toLowercase) {
                line.toLowerCase();
            }

            if (removeAccentsBeforeStemming) {
                line = tokenizer.removeAccents(line);
            }

            ArrayList<String> words = tokenizer.tokenize(line);
            for (String word : words) {

                if (stemmer != null) {
                    word = stemmer.stem(word);
                }

                if (removeAccentsAfterStemming) {
                    word = tokenizer.removeAccents(word);
                }

                if (!dictionary.containsWord(word)) {
                    dictionary.addWord(word);
                }
                dictionary.addDocument(word, i);

                document.addWord(word);
            }

            i++;
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Indexing done after " + (double)estimatedTime / 1000 + " seconds");
        int documentsCount = documents.size();

        // Calculating IDF.
        startTime = System.currentTimeMillis();
        log.info("Calculating IDF for every word.");

        dictionary.calculateIDF(documentsCount);

        estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Calculating IDF done after " + (double)estimatedTime / 1000 + " seconds");

        // Calculating TFIDF.
        startTime = System.currentTimeMillis();
        log.info("Calculating TFIDF for every word in documents.");

        calculateDocumentWordsTFIDF(documents);

        estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Calculating TFIDF done after " + (double)estimatedTime / 1000 + " seconds");

        // TODO: projít znovu list dokumentů a vytvořit invertovaný index + zároveň vypočítat tf-idf pro každý dokument.
    }

    public void calculateDocumentWordsTFIDF(List<Document> documents) {
        Map<String, Float> wordsWithIDF = dictionary.getWords();

        for (Document document : documents) {
            Map<String, DocumentWordValues> documentWords = document.getWords();

            float euclidStandard = 0;
            for (String word : documentWords.keySet()) {
                DocumentWordValues documentWordValues = documentWords.get(word);
                float tfidf = TFIDF.calculateTFIDF(documentWordValues.getTf(), wordsWithIDF.get(word));
                documentWordValues.setTfidf(tfidf);
                euclidStandard += Math.pow(tfidf, 2);
            }
            euclidStandard = (float) Math.sqrt(euclidStandard);
            document.setEuclidStandard(euclidStandard);
        }
    }

    public void index(Document document) {
        // todo implement
    }

    public List<Result> search(String query) {
        //  todo implement
        return null;
    }

    public boolean loadIndexedData(String filename) {
        try {
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.dictionary = (Dictionary) ois.readObject();
            ois.close();
            log.info("Indexed data was loaded successfully from file '" + filename + "'.");
            return true;
        } catch (Exception e) {
            log.warn("File '" + filename + "' for indexed data was not found!");
            return false;
        }
    }

    public boolean saveIndexedData(String filename) {
        try {
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this.dictionary);
            oos.close();
            log.info("Saving indexed data into file '" + filename + "' was successful.");
            return true;
        } catch (Exception e) {
            log.warn("Saving indexed data into file '" + filename + "'  was unsuccessful!");
            return false;
        }
    }

    public String getProcessedForm(String text) {
        if (toLowercase) {
            text = text.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            text = tokenizer.removeAccents(text);
        }
        if (stemmer != null) {
            text = stemmer.stem(text);
        }
        if (removeAccentsAfterStemming) {
            text = tokenizer.removeAccents(text);
        }
        return text;
    }
}
