package cz.zcu.kiv.nlp.ir.gui.windows;

import cz.zcu.kiv.nlp.ir.trec.data.Constants;
import cz.zcu.kiv.nlp.ir.trec.data.DataLoader;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.CrawleredDocument;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.DetailedOverview;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.PropertyParameters;
import cz.zcu.kiv.nlp.ir.trec.data.enums.EDataType;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearchType;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.indexer.Index;
import cz.zcu.kiv.nlp.ir.trec.stemmer.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.trec.tokenizer.AdvancedTokenizer;
import gnu.trove.set.hash.THashSet;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.*;

import java.util.List;
import java.util.Objects;

/**
 * Hlavní třída aplikace.
 */
public class MainWindow extends Application {

    /** GUI prvky. */
    @FXML
    private Label lblDocumentCountsNumber;

    @FXML
    private ListView<String> listDocuments;

    @FXML
    private Button btnSearch;

    @FXML
    private ComboBox<String> cmbSearchType;

    @FXML
    private ComboBox<String> cmbDataType;

    @FXML
    public TextFlow txtFlowDocumentInfo;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Label lblStatusDescription;

    @FXML
    private Label lblStatusText;

    @FXML
    private Spinner spinnerDocumentCount;
    private SpinnerValueFactory<Integer> spinnerValueFactory;

    /** Index prvky. */
    private EDataType currentDataType = null;
    private Index index;
    private List<Document> documents;
    private String indexedDataFilename;

    /** Defaultní hodnoty. */
    private final int initialSpinnerValue = 10;
    private final int minSpinnerValue = 0;
    private final int maxSpinnerValue = 100000;

    /** Logger v aplikaci. */
    static Logger log = Logger.getLogger(MainWindow.class);

    /**
     * Main metoda.
     * @param args - Vstupní parametry.
     */
    public static void main(String[] args) {
        configureLogger();
        launch(args);
    }

    /**
     * Startovací metoda pro okno.
     * @param stage - Hlavní stage aplikace-
     * @throws Exception - Exception.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("cz/zcu/kiv/nlp/ir/gui/windows/MainWindow.fxml")));
        stage.setTitle("DanisikSearch");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Načtení a zaindexování dokumentů podle zvoleného dataType.
     * @param dataType - Typ dat.
     */
    private void loadDocuments(EDataType dataType) {
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

            setStatus("Načítání indexovaných dokumentů");
            // Načtení indexovaných dat.
            if (!index.loadIndexedData(indexedDataFilename)) {

                setStatus("Indexace nenalezena, probíhá indexace");
                // Indexace dat.
                index.index(documents);

                // Uložení indexovaných dat.
                index.saveIndexedData(indexedDataFilename);
            }
            setStatus("Indexace dokončena");
        }
        catch (Exception e) {
            // Pokud nastala chyba při načítání nebo indexaci, nastav list dokumentů na null hodnotu.
            documents = null;
        }
    }

    /**
     * Inicializace dat pro GUI prvky a Indexer.
     */
    @FXML
    public void initialize() {
        // Defaultní data pro combobox reprezentující typ dat.
        cmbDataType.getItems().add("CRAWLERED");
        cmbDataType.getItems().add("CUSTOM");
        cmbDataType.getSelectionModel().select(0);

        // Defaultní data pro combobox reprezentující typ hledání.
        cmbSearchType.getItems().add("SVM");
        cmbSearchType.getItems().add("BOOLEAN");
        cmbSearchType.getSelectionModel().select(0);

        // Value factory.
        spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minSpinnerValue, maxSpinnerValue, initialSpinnerValue);
        spinnerDocumentCount.setValueFactory(spinnerValueFactory);

        // Inicializace indexeru.
        index = new Index(new CzechStemmerAgressive(), new AdvancedTokenizer(Constants.FILENAME_STOPWORDS),
                "", false, true, true, 10);
    }

    /**
     * Click event pro tlačítko "Vyhledat".
     * @param actionEvent - Event.
     */
    @FXML
    public void search(ActionEvent actionEvent) {
        // Zjištění zvoleného typu vyhledávání.
        String searchType = cmbSearchType.getSelectionModel().getSelectedItem();
        ESearchType selectedSearchType = ESearchType.getSearchType(searchType);

        // Zjištěný zvoleného typu dat.
        String dataType = cmbDataType.getSelectionModel().getSelectedItem();
        EDataType selectedDataType = EDataType.getDataType(dataType);

        index.setSearchType(selectedSearchType);
        index.setMostRelevantDocumentsCount(spinnerValueFactory.getValue());

        // Pokud ještě nebyly načteny žádné dokumenty nebo se změnil typ dat pro vyhledávání, tak načti dokumenty.
        if (documents == null || currentDataType != selectedDataType) {
            loadDocuments(selectedDataType);
            if (documents == null) {
                setStatus("Nastal error při indexaci dokumentů!");
                return;
            }

            currentDataType = selectedDataType;
        }

        setStatus("Hledání nejrelevantnějších dokumentů");
        String query = txtFieldSearch.getText();
        List<Result> resultHits = index.search(query);
        int mostRelevantDocumentsCount = resultHits.size();

        if (resultHits == null) {
            setStatus("Query není validní!");
            setDocumentCount(0, 0);
            return;
        }
        setStatus("Hledání dokončeno");

        listDocuments.getItems().clear();

        if (selectedSearchType == ESearchType.SVM) {
            List<Result> mostRelevantDocuments = index.getMostRelevantDocuments(resultHits);
            mostRelevantDocumentsCount = mostRelevantDocuments.size();

            // Přidej výsledky do listu dokumentů v GUI.
            for (Result r : mostRelevantDocuments) {
                listDocuments.getItems().add(r.getDocumentID());
            }
        }
        else {
            // Přidej výsledky do listu dokumentů v GUI.
            for (Result r : resultHits) {
                listDocuments.getItems().add(r.getDocumentID());
            }
        }

        // Nastav číslo reprezentující počet nalezených dokumentů.
        setDocumentCount(mostRelevantDocumentsCount, resultHits.size());
    }

    /**
     * Zobrazení informací pro daný dokument při kliknutí na položku v seznamu vyhledaných dokumentů.
     * @param mouse - Event.
     */
    @FXML
    public void displayDocumentInfo(MouseEvent mouse) {
        String item = listDocuments.getSelectionModel().getSelectedItem();

        // Kontrola jestli uživatel klikl na nějaký prvek v listu nebo do práznda.
        if (item == null) {
            mouse.consume();
        }
        else {
            Document document = getDocument(item);
            txtFlowDocumentInfo.getChildren().clear();
            prepareTextFlow(document);
        }
    }

    /**
     * Zobrazení informací daného dokumentu v textflow.
     * @param document
     */
    private void prepareTextFlow(Document document) {
        ObservableList<Node> childrens = txtFlowDocumentInfo.getChildren();
        childrens.addAll(addDocumentInfo("ID", document.getId()), new Text(System.lineSeparator()));

        if (document instanceof DocumentNew) {
            // Zobrazení informací pro školní dokument.
            DocumentNew doc = (DocumentNew) document;
            childrens.addAll(addDocumentInfo("Titulek", doc.getTitle()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Text", doc.getText()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Datum", doc.getDate().toString()), new Text(System.lineSeparator()));
        }
        else if (document instanceof CrawleredDocument) {
            // Zobrazení informací pro stažený dokument.
            CrawleredDocument doc = (CrawleredDocument) document;
            childrens.addAll(addDocumentInfo("Titulek", doc.getTitle()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Cena", doc.getPrice()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Informace k ceně", doc.getPriceAdditionalInfo()), new Text(System.lineSeparator()));

            DetailedOverview overview = doc.getDetailedOverview();
            childrens.addAll(addDocumentInfo("Číslo nabídky", overview.getOfferNumber()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Vlastnictví", overview.getOwnership()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Konstrukce", overview.getConstruction()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Stav nemovitosti", overview.getPropertyStatus()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Umístění objektu", overview.getObjectLocation()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Lokalita", overview.getLocality()), new Text(System.lineSeparator()));

            childrens.addAll(addDocumentInfo("Popis", doc.getDescription()), new Text(System.lineSeparator()));

            PropertyParameters propertyParameters = doc.getPropertyParameters();
            childrens.addAll(addDocumentInfo("Plocha užitná", propertyParameters.getAreaUsable()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Plocha podlahová", propertyParameters.getFloorArea()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Plocha parcely", propertyParameters.getParcelArea()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Plocha zastavěná", propertyParameters.getBuiltUpArea()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Dopravní spojení", propertyParameters.getTransportLinks()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Parkování", propertyParameters.getParking()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Příjezd", propertyParameters.getArrivalRoad()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Elektrický proud", propertyParameters.getElectricalCurrent()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Zdroj teplé vody", propertyParameters.getHotWaterSource()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Kvalita vody", propertyParameters.getWaterQuality()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Rozvod plynu", propertyParameters.getGasDistribution()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Vytápění", propertyParameters.getHeating()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Využívané palivo", propertyParameters.getFuelUsed()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Kanalizace", propertyParameters.getDrain()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Zavedené inž. sítě", propertyParameters.getEstablishedNetworkInfrastructure()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Druh krytiny", propertyParameters.getTypeOfCovering()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Podlahy", propertyParameters.getTypeOfPlaster()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Stav vlhkosti", propertyParameters.getHumidityStatus()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Stěny", propertyParameters.getCeilings()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Tvar střechy", propertyParameters.getRoofShape()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Typ konstrukce", propertyParameters.getConstructionType()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Koupelna", propertyParameters.getBathroom()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("WC", propertyParameters.getToilet()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Charakter pozemku", propertyParameters.getPlotCharacter()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Oplocení", propertyParameters.getFencing()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Stav zahrady", propertyParameters.getGardenCondition()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Zahrada", propertyParameters.getGarden()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Vedlejší prostory a stavby", propertyParameters.getSecondarySpaces()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Okolní prostranství", propertyParameters.getSurroundingAreas()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Služby", propertyParameters.getServices()), new Text(System.lineSeparator()));
            childrens.addAll(addDocumentInfo("Využití volného času", propertyParameters.getUseOfFreeTime()), new Text(System.lineSeparator()));

            childrens.addAll(addDocumentInfo("URL", doc.getUrl()), new Text(System.lineSeparator()));
        }
    }

    /**
     * Získání textflow pro daný atribut dokumentu.
     * @param stringTitle - Název atributu (např. Title).
     * @param stringText - Hodnota atributu (např. Prodej domu).
     * @return Nastylovaný textflow s labely.
     */
    private Node addDocumentInfo(String stringTitle, String stringText) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPrefWidth(txtFlowDocumentInfo.getPrefWidth());

        // Vymaž veškerá odřádkování z textu.
        stringTitle = stringTitle.replaceAll("\\n\\r|\\r\\n|\\r|\\n", "");

        // Nastavení titulku atributu.
        Text title = new Text(" " + stringTitle + ": ");
        title.setStyle("-fx-font-weight: bold");

        textFlow.getChildren().add(title);

        // Získání jednotlivých slov ze zadaný query.
        THashSet<String> queryTokens = index.getQueryTokens();

        // Získání jednotlivých slov z popisku atributu.
        String[] textTokens = stringText.split(" ");

        String basicToken = "";
        boolean containsBasicChars = false;

        // Cyklus projede všechny slova z popisku atributu
        // a pokud se dané slovo nachází v query, tak ho zvýrazni.
        for (int i = 0; i < textTokens.length; i++) {
            String token = textTokens[i];
            String preprocessedToken = index.getProcessedForm(token);

            if (token.contains(",") || token.contains(".") || token.contains("(") || token.contains(")")) {
                basicToken = token;
                token = token.replaceAll(",", "");
                preprocessedToken = preprocessedToken.replaceAll(",", "");
                containsBasicChars = true;
            }

            Label text = new Label(token);
            token = token.toLowerCase();

            if (queryTokens.contains(preprocessedToken)) {
                // Nastavení barvy textu na bílou.
                text.setTextFill(Color.WHITE);

                // Nastavení background color na červenou.
                AnchorPane anchorPane = new AnchorPane();
                anchorPane.getChildren().add(text);
                anchorPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

                textFlow.getChildren().add(anchorPane);
            }
            else {
                textFlow.getChildren().add(text);
            }

            if (containsBasicChars) {
                Label difference = new Label(StringUtils.difference(token, basicToken.toLowerCase()));
                textFlow.getChildren().add(difference);
            }

            // Pokud aktuální token není poslední, tak přidej mezeru.
            if ((i + 1) < textTokens.length) {
                textFlow.getChildren().add(new Text(" "));
            }

            basicToken = "";
            containsBasicChars = false;
        }

        return textFlow;
    }

    /**
     * Nastavení textu pro status.
     * @param text - Zobrazovaný text.
     */
    private void setStatus(String text) {
        lblStatusDescription.setText(text);
    }

    /**
     * Nastavení čísla reprezentující počet nalezených dokumentů.
     * @param documentCount - Počet dokumentů (nejrelevantnějších).
     * @param maxDocumentCount - Počet celkově nalezených dokumentů.
     */
    private void setDocumentCount(int documentCount, int maxDocumentCount) {
        lblDocumentCountsNumber.setText("" + documentCount + " (" + maxDocumentCount + ")");
    }

    /**
     * Vrátí instanci dokumentu.
     * @param id - ID dokumentu.
     * @return Instance dokumentu.
     */
    private Document getDocument(String id) {
        for (Document document: documents) {
            if (document.getId().equals(id)) return document;
        }
        return null;
    }

    /**
     * Nastavení loggeru.
     */
    protected static void configureLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        Logger.getRootLogger().setLevel(Level.INFO);
    }
}
