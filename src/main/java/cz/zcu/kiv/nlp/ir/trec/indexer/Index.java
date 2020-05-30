package cz.zcu.kiv.nlp.ir.trec.indexer;

import cz.zcu.kiv.nlp.ir.trec.Searcher;
import cz.zcu.kiv.nlp.ir.trec.data.Constants;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.Dictionary;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.WordValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentValues;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearchType;
import cz.zcu.kiv.nlp.ir.trec.data.query.BooleanQueryPreparer;
import cz.zcu.kiv.nlp.ir.trec.data.query.QueryRecord;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.math.CosineSimilarity;
import cz.zcu.kiv.nlp.ir.trec.math.TFIDF;
import cz.zcu.kiv.nlp.ir.trec.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.Tokenizer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private int mostRelevantDocumentsCount;
    private QueryParser parser;

    private ESearchType searchType;

    /**
     * Constructor.
     * @param stemmer - Použitý stemmer.
     * @param tokenizer - Použitý tokenizer.
     * @param defaultField - Základní field, ve kterém se bude hledat.
     * @param removeAccentsBeforeStemming - Pokud true, smaže diakritiku před stemmerem.
     * @param removeAccentsAfterStemming - Pokud true, smaže diakritiku po stemmeru.
     * @param toLowercase - Pokud true, tak všechny znaky převede do lowercase.
     * @param mostRelevantDocumentsCount - Počet dokumentů, kteří jsou nejvíce relevantní k dotazu.
     */
    public Index(Stemmer stemmer, Tokenizer tokenizer, String defaultField, boolean removeAccentsBeforeStemming,
                 boolean removeAccentsAfterStemming, boolean toLowercase, int mostRelevantDocumentsCount) {
        this.dictionary = new Dictionary();
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
        this.mostRelevantDocumentsCount = mostRelevantDocumentsCount;

        // Default search type.
        this.searchType = ESearchType.SVM;

        // Query parser.
        this.parser = new QueryParser(defaultField, new WhitespaceAnalyzer());

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
        log.info("Start indexing list of " + documentsCount + " documents.");

        // Iterate through every document.
        for (Document document : documents) {
            indexSingleDocument(document);
            dictionary.incrementIndexedDocuments();
        }

        // Calculating IDF for dictionary.
        dictionary.calculateIDF();

        // Calculating TFIDF for every word in every document.
        calculateDocumentsWordsTFIDF();

        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Indexing done after " + (double)estimatedTime / 1000 + " seconds");
    }

    /**
     * Metoda zaindexuje jeden dokument
     *
     * @param document - Dokument k zaindexování.
     */
    public void index(Document document) {
        long startTime = System.currentTimeMillis();
        log.info("Start indexing single document.");

        // Index single document.
        indexSingleDocument(document);
        dictionary.incrementIndexedDocuments();

        // Calculating IDF for dictionary.
        dictionary.calculateIDF();

        // Calculating TFIDF for every word in document.
        calculateDocumentsWordsTFIDF();

        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Indexing done after " + (double)estimatedTime / 1000 + " seconds");
    }

    /**
     * Metoda zaindexuje zadaný dokument do dictionary.
     * @param document - Dokument k zaindexování.
     */
    private void indexSingleDocument(Document document) {
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

        DocumentValues documentValues = new DocumentValues();
        documentValues.setDocumentID(documentId);

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
            dictionary.addDocumentValues(word, documentValues);

            // Add word into document's dictionary.
            documentValues.addWord(word);
        }
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

                // Compare cosine similarities and return most relevant documents.
                results = CosineSimilarity.getMostRelevantDocumentToQuery(dictionary, indexedQuery, mostRelevantDocumentsCount);
                break;

            // BOOLEAN searching.
            case BOOLEAN:
                Query q;
                try {

                    String newQuery = BooleanQueryPreparer.prepareQuery(Arrays.asList(query.split(" ")));

                    q = parser.parse(newQuery);
                    results = new ArrayList<>(processQuery(q));
                } catch (ParseException e) {
                    log.warn("Query '" + query + "' is not valid query!");
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
     */
    private void calculateDocumentsWordsTFIDF() {
        for (DocumentValues documentValues : dictionary.getAllDocumentValues()) {
            calculateDocumentWordsTFIDF(documentValues);
        }
    }

    /**
     * Metoda vypočítá tfidf hodnotu pro všechny slova v zadaném dokumentu.
     * @param documentValues - Hodnoty dokumentu.
     */
    private void calculateDocumentWordsTFIDF(DocumentValues documentValues) {
        THashMap<String, WordValues> wordsWithIDF = dictionary.getWords();
        THashMap<String, DocumentWordValues> documentWords = documentValues.getWordValues();

        float euclidStandard = 0;
        for (String word : documentWords.keySet()) {
            DocumentWordValues documentWordValues = documentWords.get(word);

            if (!wordsWithIDF.containsKey(word)) continue;

            float tfidf = TFIDF.calculateTFIDF(documentWordValues.getTf(), wordsWithIDF.get(word).getIdf());
            documentWordValues.setTfidf(tfidf);
            euclidStandard += Math.pow(tfidf, 2);
        }
        euclidStandard = (float) Math.sqrt(euclidStandard);
        documentValues.setEuclidStandard(euclidStandard);
    }

    /**
     * Metoda zpracovávající BOOLEAN query.
     * @param query - Query (podporovány TermQuery, BooleanQuery).
     * @return Set výsledků pro daný TermQuery.
     */
    public THashSet<Result> processQuery(Query query) {
        // TODO: priorita operátorů !!!! (AND -> OR -> NOT)
        THashSet<Result> results = new THashSet<>();

        if (query instanceof TermQuery) {
            // Pokud je query typu TermQuery, tak získej seznam hodnot dokumentů pro zpracované slovo.
            Term term = ((TermQuery)query).getTerm();
            String text = getProcessedForm(term.text());

            WordValues wordValues = dictionary.getWordValues(text);

            if (wordValues != null) {
                for (DocumentValues documentValues : wordValues.getDocumentValues()) {
                    results.add(new ResultImpl(documentValues.getDocumentID(), 0));
                }
            }
        }
        else {
            // Pokud je query typu BooleanQuery, tak projeď všechny klauzule.
            ArrayList<QueryRecord> queryRecords = new ArrayList<>();

            for (BooleanClause clause : ((BooleanQuery)query).clauses()) {
                queryRecords.add(new QueryRecord(clause.getOccur(), processQuery(clause.getQuery())));
            }

            // Po projetí všech klauzulí projeď všechny query záznamy.
            if (queryRecords.size() > 0) {
                for (int i = 0; i < queryRecords.size(); i++) {
                    QueryRecord record = queryRecords.get(i);
                    THashSet<Result> queryResults = record.getResults();

                    // Zkontroluj typ operátoru.
                    switch(record.getOccur()) {
                        case MUST:
                            // Operátor AND - získej všechny hodnoty dokumentů, který se vyskytují v obou slovech.

                            if (i == 0) {
                                // Pokud je to první klauzule, zkopíruj všechny záznamy.
                                results.addAll(queryResults);
                            }
                            else {
                                results.retainAll(queryResults);
                            }
                            break;
                        case SHOULD:
                            // Operátor OR - získej všechny hodnoty dokumentů spojením obou listů.

                            results.addAll(queryResults);
                            break;
                        case MUST_NOT:
                            // Operátor NOT, který je použit buď s AND nebo OR.

                            boolean contains;
                            for (DocumentValues documentValues : dictionary.getAllDocumentValues()) {
                                contains = false;

                                // Pokud se dokument nenachází ve výsledkách pro daný token, tak ho přidej do výsledků.
                                for (Result result : queryResults) {
                                    if (result.getDocumentID().equals(documentValues.getDocumentID())) {
                                        contains = true;
                                        break;
                                    }
                                }

                                if (!contains) {
                                    results.add(new ResultImpl(documentValues.getDocumentID(), 0));
                                }

                            }
                            break;
                    }
                }
            }
        }

        return results;
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

    /**
     * Vrátí aktuálně nastavený typ vyhledávání.
     * @return Typ vyhledávání.
     */
    public ESearchType getSearchType() {
        return searchType;
    }
}
