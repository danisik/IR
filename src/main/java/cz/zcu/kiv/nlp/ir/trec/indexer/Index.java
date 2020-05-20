package cz.zcu.kiv.nlp.ir.trec.indexer;

import cz.zcu.kiv.nlp.ir.trec.Searcher;
import cz.zcu.kiv.nlp.ir.trec.data.Constants;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.Dictionary;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.WordValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearchType;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.math.CosineSimilarity;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;
import cz.zcu.kiv.nlp.ir.trec.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.Tokenizer;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;


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
    private QueryParser parser;

    private ESearchType searchType;

    /**
     * Constructor.
     * @param stemmer - Použitý stemmer.
     * @param tokenizer - Použitý tokenizer.
     * @param analyzer - Použitý analyzer.
     * @param defaultField - Základní field, ve kterém se bude hledat.
     * @param removeAccentsBeforeStemming - Pokud true, smaže diakritiku před stemmerem.
     * @param removeAccentsAfterStemming - Pokud true, smaže diakritiku po stemmeru.
     * @param toLowercase - Pokud true, tak všechny znaky převede do lowercase.
     * @param mostRelevantDocumentsCount - Počet dokumentů, kteří jsou nejvíce relevantní k dotazu.
     */
    public Index(Stemmer stemmer, Tokenizer tokenizer, Analyzer analyzer, String defaultField, boolean removeAccentsBeforeStemming,
                 boolean removeAccentsAfterStemming, boolean toLowercase, short mostRelevantDocumentsCount) {
        this.dictionary = new Dictionary();
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
        this.mostRelevantDocumentsCount = mostRelevantDocumentsCount;

        // Default search type.
        this.searchType = ESearchType.SVM;

        this.parser = new QueryParser(defaultField, analyzer);

        log.info("Stemmer: " + stemmer.getClass().getSimpleName());
        log.info("Tokenizer: " + tokenizer.getClass().getSimpleName());
        log.info("Accents removed before steming: " + removeAccentsBeforeStemming);
        log.info("Accents removed after steming: " + removeAccentsAfterStemming);
        log.info("To lower case: " + toLowercase);
    }

    /**
     * Metoda zaindexuje zadaný seznam dokumentů
     *
     * @param documents list dokumentů
     */
    public void index(List<Document> documents) {
        int documentsCount = documents.size();

        if (documentsCount == 0) {
            log.info("List of documents did not contain single document !");
            return;
        }

        long startTime = System.currentTimeMillis();
        log.info("Start indexing.");

        // Iterate through every document.
        for (Document document : documents) {
            String documentId = document.getId();

            // Get line of text of specific documents attributes.
            String line = document.getDataForPreprocessing();

            // Lowercase line if set.
            if (toLowercase) {
                line.toLowerCase();
            }

            // Remove accents before stemming if sets.
            if (removeAccentsBeforeStemming) {
                line = tokenizer.removeAccents(line);
            }

            // Get words with tokenizer.
            ArrayList<String> words = tokenizer.tokenize(line);
            for (String word : words) {

                // Apply stemmer if created.
                if (stemmer != null) {
                    word = stemmer.stem(word);
                }

                // Remove accents after stemming if sets.
                if (removeAccentsAfterStemming) {
                    word = tokenizer.removeAccents(word);
                }

                // Add word into dictionary if not presented.
                if (!dictionary.containsWord(word)) {
                    dictionary.addWord(word);
                }
                // Add document id for specific word.
                dictionary.addDocumentId(word, documentId);
                // Add word into document's dictionary.
                dictionary.addDocumentWord(documentId, word);
            }
        }

        // Calculating IDF for dictionary.
        dictionary.calculateIDF(documentsCount);

        // Calculating TFIDF for every word in every document.
        calculateDocumentsWordsTFIDF(documents);

        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Indexing done after " + (double)estimatedTime / 1000 + " seconds");
    }

    /**
     * Metoda pro hledání v dokumentech.
     * @param query - Query.
     * @return List výsledků nejvíce relevantních k zadané query.
     */
    public List<Result> search(String query) {
        List<Result> results = new ArrayList<>();

        switch(searchType) {
            // SVM searching.
            case SVM:
                DocumentValues indexedQuery = new DocumentValues();

                // Tokenize query.
                ArrayList<String> words = tokenizer.tokenize(query);
                for (String word : words) {
                    // Apply stemmer and lemmatizer on word.
                    indexedQuery.addWord(getProcessedForm(word));
                }

                // Calculate tfidf for query words.
                calculateDocumentWordsTFIDF(indexedQuery);

                // Compare cosine similarities and reutnr most relevant documents.
                results = CosineSimilarity.getMostRelevantDocumentToQuery(dictionary, indexedQuery, mostRelevantDocumentsCount);
                break;

            // BOOLEAN searching.
            case BOOLEAN:
                Query q;
                try {
                    q = parser.parse(query);
                } catch (ParseException e) {
                    log.warn("Query '" + query + "' is not valid query!");
                    break;
                }

                if (q instanceof TermQuery) {

                }
                else if (q instanceof BooleanQuery) {

                }
                else {
                    log.warn("Unsupported query type !");
                    break;
                }

                break;

            // Default.
            default:
                break;
        }

        return results;
    }

    /**
     * Metoda vypočítá tfidf hodnotu pro všechny dokumenty.
     * @param documents - Dokumenty.
     */
    private void calculateDocumentsWordsTFIDF(List<Document> documents) {
        for (Document document : documents) {
            DocumentValues documentValues = dictionary.getDocumentValuesById(document.getId());
            calculateDocumentWordsTFIDF(documentValues);
        }
    }

    /**
     * Metoda vypočítá tfidf hodnotu pro všechny slova v zadaném dokumentu.
     * @param documentValues - Hodnoty dokumentu.
     */
    private void calculateDocumentWordsTFIDF(DocumentValues documentValues) {
        Map<String, WordValues> wordsWithIDF = dictionary.getWords();
        Map<String, DocumentWordValues> documentWords = documentValues.getWordValues();

        // TODO: indonesz -> hází error, zřejmě není ve slovníku ??
        float euclidStandard = 0;
        for (String word : documentWords.keySet()) {
            DocumentWordValues documentWordValues = documentWords.get(word);
            float tfidf = TFIDF.calculateTFIDF(documentWordValues.getTf(), wordsWithIDF.get(word).getIdf());
            documentWordValues.setTfidf(tfidf);
            euclidStandard += Math.pow(tfidf, 2);
        }
        euclidStandard = (float) Math.sqrt(euclidStandard);
        documentValues.setEuclidStandard(euclidStandard);
    }

    /**
     * Metoda načte indexovaná data ze souboru.
     * @param filename - Název souboru.
     * @return True pokud načítání proběhlo úspěšně.
     */
    public boolean loadIndexedData(String filename) {
        log.info("Loading indexed data.");
        try {
            long startTime = System.currentTimeMillis();

            FileInputStream fin = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fin, Constants.BUFFER_STREAM_SIZE);
            ObjectInputStream ois = new ObjectInputStream(bis);
            this.dictionary = (Dictionary) ois.readObject();
            ois.close();

            long estimatedTime = System.currentTimeMillis() - startTime;

            log.info("Indexed data was loaded successfully from file '" + filename + "' after " + (double)estimatedTime / 1000 + " seconds.");
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

    /**
     * Metoda uloží indexovaná data do souboru.
     * @param filename - Název souboru.
     * @return True pokud ukládání proběhlo úspěšně.
     */
    public boolean saveIndexedData(String filename) {
        log.info("Saving indexed data.");
        try {
            long startTime = System.currentTimeMillis();

            FileOutputStream fout = new FileOutputStream(filename);
            BufferedOutputStream bos = new BufferedOutputStream(fout, Constants.BUFFER_STREAM_SIZE);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this.dictionary);
            oos.close();

            long estimatedTime = System.currentTimeMillis() - startTime;
            log.info("Saving indexed data into file '" + filename + "' was successful after " + (double)estimatedTime / 1000 + " seconds.");

            return true;
        } catch (Exception e) {
            log.warn("Saving indexed data into file '" + filename + "'  was unsuccessful!");
            return false;
        }
    }

    /**
     * Vrátí slovo, které proběhlo úpravou (stemmer, lemmer, lowercase, accent).
     * @param word - Zpracovávané slovo.
     * @return Zpracované slovo.
     */
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

    /**
     * Nastaví počet dokumentů, který se mají vrátit z hledání.
     * @param mostRelevantDocumentsCount - Počet dokumentů.
     */
    public void setMostRelevantDocumentsCount(short mostRelevantDocumentsCount) {
        this.mostRelevantDocumentsCount = mostRelevantDocumentsCount;
    }

    /**
     * Nastaví typ hledání (SVM, Boolean, ...).
     * @param searchType - Typ hledání.
     */
    public void setSearchType(ESearchType searchType) {
        this.searchType = searchType;
    }

    // TODO: delete in final version.
    public ESearchType getSearchType() {
        return searchType;
    }
}
