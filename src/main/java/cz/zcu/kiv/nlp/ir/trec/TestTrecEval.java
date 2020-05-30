package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.*;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.CrawleredDocument;
import cz.zcu.kiv.nlp.ir.trec.data.enums.EDataType;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearchType;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.indexer.Index;
import cz.zcu.kiv.nlp.ir.trec.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.AdvancedTokenizer;
import org.apache.log4j.*;
import org.apache.lucene.analysis.cz.CzechAnalyzer;

import java.io.*;
import java.util.*;


/**
 * @author tigi
 *
 * Třída slouží pro vyhodnocení vámi vytvořeného vyhledávače
 *
 */
public class TestTrecEval {

    private static Logger log = Logger.getLogger(TestTrecEval.class);
    private static EDataType dataType = null;
    private static Index index;
    private static List<Document> documents;

    /**
     * Metoda vytvoří objekt indexu, načte data, zaindexuje je provede předdefinované dotazy a výsledky vyhledávání
     * zapíše souboru a pokusí se spustit evaluační skript.
     *
     * Na windows evaluační skript pravděpodbně nebude možné spustit. Pokud chcete můžete si skript přeložit a pak
     * by mělo být možné ho spustit.
     *
     * Pokud se váme skript nechce překládat/nebo se vám to nepodaří. Můžete vygenerovaný soubor s výsledky zkopírovat a
     * spolu s přiloženým skriptem spustit (přeložit) na
     * Linuxu např. pomocí vašeho účtu na serveru ares.fav.zcu.cz
     *
     * Metodu není třeba měnit kromě řádků označených T O D O  - tj. vytvoření objektu třídy {@link Index} a
     */
    public static void main(String args[]) throws Exception {
        configureLogger();

        // Init indexer.
        index = new Index(new CzechStemmerAgressive(), new AdvancedTokenizer(Constants.FILENAME_STOPWORDS),
                "", false, true, true, 10);

        // Set default options.
        index.setSearchType(ESearchType.BOOLEAN);
        dataType = EDataType.CRAWLERED;

        String indexedDataFilename = "";

        log.info("Load " + dataType.toString() + " documents.");
        switch(dataType) {
            // Načtení školních dokumentů.
            case CUSTOM:
                documents = DataLoader.loadDocumentsFromSchool(Constants.FILENAME_CUSTOM_DATA);
                indexedDataFilename = Constants.FILENAME_DATA_INDEX_CUSTOM;
                break;

            // Načtení stažených dat.
            case CRAWLERED:
                documents = DataLoader.loadCrawleredData(Constants.FILENAME_CRAWLERED_DATA);
                indexedDataFilename = Constants.FILENAME_DATA_INDEX_CRAWLERED;
                break;

            // Default.
            default:
                break;
        }

        log.info("Documents count: " + documents.size());

        // Načtení indexovaných dat.
        if (!index.loadIndexedData(indexedDataFilename)) {

            // Indexace dat.
            index.index(documents);

            // Uložení indexovaných dat.
            index.saveIndexedData(indexedDataFilename);
        }

        switch(dataType) {
            // Načtení školních dokumentů.
            case CUSTOM:
                searchInDocumentsFromSchool();
                break;

            // Načtení stažených dat.
            case CRAWLERED:
                searchInCrawleredDocuments();
                break;

            // Default.
            default:
                break;
        }
    }

    /**
     * Searching in documents from school.
     */
    private static void searchInDocumentsFromSchool() {
        String query = "";

        switch (index.getSearchType()) {
            case BOOLEAN:
                //query = "beta AND (alfa OR c) OR gamma";
                //query = "2351 AND (715682 OR 717887) OR 717884";
                //query = "2351";
                //query = "nízkopodlažního AND NOT slaný OR NOT meclov";
                query = "autooo AND NOT slaný";
                //query = "(Praha OR (text1 AND NOT text2)) AND NOT (Brno OR NOT (Praha AND NOT Plzeň)) OR NOT Ostrava";
                //query = "PRAHA NOT OSTRAVA"; // Defaultně se dává OR.
                //query = "(Praha OR (text1 AND NOT text2 )) AND NOT ( Brno OR Praha)";
                //query = "NOT alfa";
                List<Result> resultHits = index.search(query);
                System.out.println(resultHits.size());
                break;
            case SVM:
                query = "Prodej chalupy 2+1 s pozemkem o celkové výměře 2033";
                break;
        }
        /*

        List<String> lines = new ArrayList<>();
        List<Topic> topics = SerializedDataHelper.loadTopic(new File(Constants.FILENAME_TOPIC_DATA));

        for (Topic t : topics) {
            log.info("Start searching.");
            long startTime = System.currentTimeMillis();
            List<Result> resultHits = index.search(t.getTitle() + " " + t.getDescription());
            long estimatedTime = System.currentTimeMillis() - startTime;
            log.info("Searching done after " + (double)estimatedTime / 1000 + " seconds");

            System.out.println("Most relevant results for topic: " + t.getId() + " - " + t.getTitle());
            System.out.println("----------------------");
            for (Result r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);
                System.out.println("TopicID: " + t.getId() + " | DocumentID: " + r.getDocumentID() + " | Score: " + r.getScore() + " | Rank: " + r.getRank());
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }
        }

         */
    }

    /**
     * Searching in crawlered documents.
     */
    private static void searchInCrawleredDocuments() {
        String query = "";

        switch (index.getSearchType()) {
            case BOOLEAN:
                //query = "beta AND (alfa OR c) OR gamma";
                //query = "2351 AND (715682 OR 717887) OR 717884";
                //query = "2351";
                query = "nízkopodlažního AND NOT slaný OR NOT meclov";
                //query = "autooo OR NOT slaný";
                //query = "(Praha OR (text1 AND NOT text2)) AND NOT (Brno OR NOT (Praha AND NOT Plzeň)) OR NOT Ostrava";
                //query = "PRAHA NOT OSTRAVA"; // Defaultně se dává OR.
                //query = "(Praha OR (text1 AND NOT text2 )) AND NOT ( Brno OR Praha)";
                //query = "NOT alfa";
                break;
            case SVM:
                query = "Prodej chalupy 2+1 s pozemkem o celkové výměře 2033";
                break;
        }

        long startTime = System.currentTimeMillis();
        // TODO: ve finální verzi nastavit maximální počet relevantních dokumentů na velikost dokumentů.
        List<Result> resultHits = index.search(query);
        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Searching done after " + (double)estimatedTime / 1000 + " seconds");

        System.out.println("Most relevant results for query: " + query);
        System.out.println("----------------------");
        for (Result r : resultHits) {
            CrawleredDocument document = getCrawleredDocument(r.getDocumentID());
            System.out.println("DocumentID: " + r.getDocumentID() + " | Score: " + r.getScore() + " | Rank: " + r.getRank() + " | Title: " + document.getTitle()
            + " | Description: " + document.getDescription());
        }
        System.out.println();
    }

    /**
     * Get crawlered document by its ID.
     * @param id - ID of document.
     * @return Crawlered document.
     */
    private static CrawleredDocument getCrawleredDocument(String id) {
        for (Document document: documents) {
            if (document.getId().equals(id)) return (CrawleredDocument)document;
        }
        return null;
    }

    /**
     * Running trec evaluation.
     * @param predictedFile - File with results.
     * @return Output from test.
     * @throws IOException
     */
    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = "./trec_eval.8.1/./trec_eval" +
                " ./trec_eval.8.1/czech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line; (line = stdout.readLine()) != null; ) output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }


    /**
     * Logger configuration.
     */
    protected static void configureLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        File results = new File(Constants.OUTPUT_DIR);
        if (!results.exists()) {
            results.mkdir();
        }

        try {
            Appender appender = new WriterAppender(new PatternLayout(), new FileOutputStream(new File(Constants.OUTPUT_DIR + "/" + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + " - " + ".log"), false));
            BasicConfigurator.configure(appender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getRootLogger().setLevel(Level.INFO);
    }
}
