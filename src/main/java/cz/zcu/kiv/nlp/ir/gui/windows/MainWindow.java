package cz.zcu.kiv.nlp.ir.gui.windows;

import cz.zcu.kiv.nlp.ir.trec.data.Constants;
import cz.zcu.kiv.nlp.ir.trec.data.DataLoader;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.CrawleredDocument;
import cz.zcu.kiv.nlp.ir.trec.data.enums.EDataType;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearchType;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.indexer.Index;
import cz.zcu.kiv.nlp.ir.trec.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.AdvancedTokenizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainWindow {

    /** GUI prvky. */
    private JComboBox cmbDataType;
    private JTextField txtFieldSearch;
    private JComboBox cmbSearchType;
    private JButton btnSearch;
    private JList listDocuments;
    private JTextArea txtAreaDocumentInfo;
    private JLabel lblDocumentsSize;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelDocumentInfo;
    private JPanel jPanelLog;

    private static JFrame frame;
    private static MainWindow mainWindow;

    /** Data prvky */
    private static DefaultListModel listModel;

    /** Index prvky. */
    private static EDataType currentDataType = null;
    private static Index index;
    private static List<Document> documents;
    private static String indexedDataFilename;

    /** Defaultní hodnoty. */
    private static final String defaultlblDocumentSizeText = "Počet dokumentů: ";

    public static void main(String[] args) {
        initFrame("DanisikSearch");
        initDefaultValuesGUI();
        initListeners();

        // Init indexer.
        index = new Index(new CzechStemmerAgressive(), new AdvancedTokenizer(Constants.FILENAME_STOPWORDS),
                "", false, true, true, 10);
    }

    private static void initFrame(String title) {
        mainWindow = new MainWindow();

        frame = new JFrame(title);
        frame.setContentPane(mainWindow.jPanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void initDefaultValuesGUI() {
        JComboBox cmbDataType = mainWindow.cmbDataType;
        cmbDataType.addItem("CRAWLERED");
        cmbDataType.addItem("CUSTOM");

        JComboBox cmbSearchType = mainWindow.cmbSearchType;
        cmbSearchType.addItem("SVM");
        cmbSearchType.addItem("BOOLEAN");

        listModel = new DefaultListModel();
        mainWindow.listDocuments.setModel(listModel);
    }

    private static void initListeners() {
        initBtnSearchActionListener();
    }

    private static void loadDocuments(EDataType dataType) {
        try {
            switch (dataType) {
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

            // Vyresetování celého slovníku.
            index.resetDictionary();

            // Načtení indexovaných dat.
            if (!index.loadIndexedData(indexedDataFilename)) {

                // Indexace dat.
                index.index(documents);

                // Uložení indexovaných dat.
                index.saveIndexedData(indexedDataFilename);
            }
        }
        catch (Exception e) {
            // TODO: zaloguj to do textarea.
        }
    }

    private static void initBtnSearchActionListener() {
        mainWindow.btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Zjištění zvoleného typu vyhledávání.
                String searchType = mainWindow.cmbSearchType.getSelectedItem().toString();
                ESearchType selectedSearchType = ESearchType.getSearchType(searchType);

                // Zjištěný zvoleného typu dat.
                String dataType = mainWindow.cmbDataType.getSelectedItem().toString();
                EDataType selectedDataType = EDataType.getDataType(dataType);

                index.setSearchType(selectedSearchType);

                if (documents == null || currentDataType != selectedDataType) {
                    loadDocuments(selectedDataType);
                }

                String query = mainWindow.txtFieldSearch.getText();
                List<Result> resultHits = index.search(query);

                mainWindow.listDocuments.removeAll();

                for (Result r : resultHits) {
                    listModel.addElement(r.getDocumentID());
                }
            }
        });
    }

    /**
     * Vrátí instanci dokumentu.
     * @param id - ID dokumentu.
     * @return Instance dokumentu.
     */
    private static Document getDocument(String id) {
        for (Document document: documents) {
            if (document.getId().equals(id)) return document;
        }
        return null;
    }
}
