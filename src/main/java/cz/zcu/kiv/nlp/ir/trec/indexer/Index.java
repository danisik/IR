package cz.zcu.kiv.nlp.ir.trec.indexer;

import cz.zcu.kiv.nlp.ir.trec.Searcher;
import cz.zcu.kiv.nlp.ir.trec.data.*;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.math.CosineSimilarity;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;
import cz.zcu.kiv.nlp.ir.trec.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.Tokenizer;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
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
    private short mostRelevantDocumentsCount;

    public Index(Stemmer stemmer, Tokenizer tokenizer, boolean removeAccentsBeforeStemming, boolean removeAccentsAfterStemming, boolean toLowercase, short mostRelevantDocumentsCount) {
        this.dictionary = new Dictionary();
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
        this.mostRelevantDocumentsCount = mostRelevantDocumentsCount;

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
        for (Document document : documents) {
            String documentId = document.getId();
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
                dictionary.addDocumentId(word, documentId);
                dictionary.addDocumentWord(documentId, word);
            }
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

        calculateDocumentsWordsTFIDF(documents);

        estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Calculating TFIDF done after " + (double)estimatedTime / 1000 + " seconds");

        // TODO: projít znovu list dokumentů a vytvořit invertovaný index + zároveň vypočítat tf-idf pro každý dokument.
    }

    public void index(Document document) {
        // todo implement
    }

    public List<Result> search(String query) {
        //  todo implement
        // TODO: tokenize query
        // TODO: compute tf-idf
        // TODO: compute cosine similarity and return most relevant documents.

        DocumentValues indexedQuery = new DocumentValues();

        ArrayList<String> words = tokenizer.tokenize(query);
        for (String word : words) {
            indexedQuery.addWord(getProcessedForm(word));
        }

        calculateDocumentWordsTFIDF(indexedQuery);

        List<Result> results = CosineSimilarity.getMostRelevantDocumentToQuery(dictionary, indexedQuery, mostRelevantDocumentsCount);

        return results;
    }


    private void calculateDocumentsWordsTFIDF(List<Document> documents) {
        for (Document document : documents) {
            DocumentValues documentValues = dictionary.getDocumentValuesById(document.getId());
            calculateDocumentWordsTFIDF(documentValues);
        }
    }

    private void calculateDocumentWordsTFIDF(DocumentValues documentValues) {
        Map<String, Float> wordsWithIDF = dictionary.getWords();
        Map<String, DocumentWordValues> documentWords = documentValues.getWordValues();

        // TODO: indonesz -> hází error, zřejmě není ve slovníku ??
        float euclidStandard = 0;
        for (String word : documentWords.keySet()) {
            DocumentWordValues documentWordValues = documentWords.get(word);
            float tfidf = TFIDF.calculateTFIDF(documentWordValues.getTf(), wordsWithIDF.get(word));
            documentWordValues.setTfidf(tfidf);
            euclidStandard += Math.pow(tfidf, 2);
        }
        euclidStandard = (float) Math.sqrt(euclidStandard);
        documentValues.setEuclidStandard(euclidStandard);
    }

    public boolean loadIndexedData(String filename) {
        try {
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.dictionary = (Dictionary) ois.readObject();
            ois.close();
            log.info("Indexed data was loaded successfully from file '" + filename + "'.");
            return true;
        } catch (FileNotFoundException e) {
            log.warn("File '" + filename + "' for indexed data was not found!");
            return false;
        } catch (EOFException e) {
            log.warn("File '" + filename + "' for indexed data is damaged and cannot be loaded!");
            return false;
        } catch (Exception e) {
            log.warn("Error shown when loading data from file '" + filename + "' for indexed data!");
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

    private String getProcessedForm(String word) {
        if (toLowercase) {
            word = word.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            word = tokenizer.removeAccents(word);
        }
        if (stemmer != null) {
            word = stemmer.stem(word);
        }
        if (removeAccentsAfterStemming) {
            word = tokenizer.removeAccents(word);
        }
        return word;
    }

    public void setMostRelevantDocumentsCount(short mostRelevantDocumentsCount) {
        this.mostRelevantDocumentsCount = mostRelevantDocumentsCount;
    }
}
