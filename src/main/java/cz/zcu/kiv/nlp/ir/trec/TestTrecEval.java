package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.*;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.CrawleredDocument;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.indexer.Index;
import cz.zcu.kiv.nlp.ir.trec.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.AdvancedTokenizer;
import org.apache.log4j.*;

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
    private static final String OUTPUT_DIR = "./TREC";
    private static final EDataType dataType = EDataType.CRAWLERED;
    private static Index index;

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

        index = new Index(new CzechStemmerAgressive(), new AdvancedTokenizer(Constants.FILENAME_STOPWORDS),
                false, true, true, (short)10);

        List<Document> documents = null;
        String indexedDataFilename = "";

        log.info("Load " + dataType.toString() + " documents.");
        if (dataType == EDataType.CUSTOM) {
            documents = DataLoader.loadDocumentsFromSchool(OUTPUT_DIR + "/czechData.bin");
            indexedDataFilename = Constants.FILENAME_DATA_INDEX_CUSTOM;
        }
        else if (dataType == EDataType.CRAWLERED){
            documents = DataLoader.loadCrawleredData(Constants.FILENAME_CUSTOM_DATA);
            indexedDataFilename = Constants.FILENAME_DATA_INDEX_CRAWLERED;
        }
        log.info("Documents count: " + documents.size());

        /*
        if (!index.loadIndexedData(indexedDataFilename)) {
            index.index(documents);
            index.saveIndexedData(indexedDataFilename);
        }
         */

        index.index(documents);

        if (dataType == EDataType.CUSTOM) {
            searchInDocumentsFromSchool();
        }
        else if (dataType == EDataType.CRAWLERED){
            searchInCrawleredDocuments(documents);
        }

    }

    private static void searchInDocumentsFromSchool() {
        List<String> lines = new ArrayList<>();
        List<Topic> topics = SerializedDataHelper.loadTopic(new File(OUTPUT_DIR + "/topicData.bin"));

        for (Topic t : topics) {
            log.info("Start searching.");
            long startTime = System.currentTimeMillis();
            List<Result> resultHits = index.search(t.getTitle() + " " + t.getDescription());
            long estimatedTime = System.currentTimeMillis() - startTime;
            log.info("Searching done after " + (double)estimatedTime / 1000 + " seconds");

            // TODO: delete in final version.
            System.out.println("Most relevant results for topic: " + t.getId() + " - " + t.getTitle());
            System.out.println("----------------------");
            for (Result r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);

                // TODO: delete in final version.
                System.out.println("TopicID: " + t.getId() + " | DocumentID: " + r.getDocumentID() + " | Score: " + r.getScore() + " | Rank: " + r.getRank());
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }
            System.out.println();
        }

        final File outputFile = new File(OUTPUT_DIR + "/results " + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        IOUtils.saveFile(outputFile, lines);
        //try to run evaluation
        try {
            runTrecEval(outputFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchInCrawleredDocuments(List<Document> documents) {
        String query = "Prodej chalupy 2+1 s pozemkem o celkové výměře 2033";
        // TODO: implement searching in crawlered documents.
        long startTime = System.currentTimeMillis();
        List<Result> resultHits = index.search(query);
        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Searching done after " + (double)estimatedTime / 1000 + " seconds");

        // TODO: delete in final version.
        System.out.println("Most relevant results for query: " + query);
        System.out.println("----------------------");
        for (Result r : resultHits) {
            // TODO: delete in final version.
            CrawleredDocument document = getCrawleredDocument(r.getDocumentID(), documents);
            System.out.println("DocumentID: " + r.getDocumentID() + " | Score: " + r.getScore() + " | Rank: " + r.getRank() + " | Title: " + document.getTitle()
            + " | Description: " + document.getDescription());
        }
        System.out.println();
    }

    // TODO: Delete in final version.
    private static CrawleredDocument getCrawleredDocument(String id, List<Document> documents) {
        for (Document document: documents) {
            if (document.getId().equals(id)) return (CrawleredDocument)document;
        }
        return null;
    }

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


    protected static void configureLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        File results = new File(OUTPUT_DIR);
        if (!results.exists()) {
            results.mkdir();
        }

        try {
            Appender appender = new WriterAppender(new PatternLayout(), new FileOutputStream(new File(OUTPUT_DIR + "/" + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + " - " + ".log"), false));
            BasicConfigurator.configure(appender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getRootLogger().setLevel(Level.INFO);
    }
}
