package cz.zcu.kiv.nlp.ir.trec.data;

import cz.zcu.kiv.nlp.ir.trec.SerializedDataHelper;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.CrawleredDocument;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.DetailedOverview;
import cz.zcu.kiv.nlp.ir.trec.data.document.crawlered.PropertyParameters;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static final List<Document> loadDocumentsFromSchool(String filename) {
        List<Document> documents = new ArrayList<>();
        File serializedData = new File(filename);

        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.loadDocument(serializedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documents;
    }

    public static final List<Document> loadCrawleredData(String filename) throws Exception {
        List<Document> documents = new ArrayList<>();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(filename));

        int i = 1;

        JSONArray objArray  =  (JSONArray) obj;
        for (Object objRecord : objArray) {
            JSONObject jsonObjRecord = (JSONObject) objRecord;
            CrawleredDocument document = new CrawleredDocument();
            document.setId(Constants.DOCUMENT_ID_PREFIX + "" + i);
            document.setTitle(jsonObjRecord.get("title").toString());
            document.setPrice(jsonObjRecord.get("price").toString());
            document.setPriceAdditionalInfo(jsonObjRecord.get("priceAdditionalInfo").toString());
            document.setDetailedOverview(loadDetailedOverview(jsonObjRecord));
            document.setDescription(jsonObjRecord.get("description").toString());
            document.setPropertyParameters(loadPropertyParameters(jsonObjRecord));
            document.setUrl(jsonObjRecord.get("url").toString());
            documents.add(document);

            i++;
        }
        return documents;
    }

    private static final DetailedOverview loadDetailedOverview(JSONObject jsonObjRecord) {
        JSONObject jsonDetailedOverview = (JSONObject) jsonObjRecord.get("detailedOverview");

        DetailedOverview detailedOverview = new DetailedOverview(
                jsonDetailedOverview.get("offerNumber").toString(),
                jsonDetailedOverview.get("ownership").toString(),
                jsonDetailedOverview.get("construction").toString(),
                jsonDetailedOverview.get("propertyStatus").toString(),
                jsonDetailedOverview.get("objectLocation").toString(),
                jsonDetailedOverview.get("locality").toString()
        );

        return detailedOverview;
    }

    private static final PropertyParameters loadPropertyParameters(JSONObject jsonObjRecord) {
        JSONObject jsonPropertyParameters = (JSONObject) jsonObjRecord.get("propertyParameters");

        PropertyParameters propertyParameters = new PropertyParameters(
                jsonPropertyParameters.get("areaUsable").toString(),
                jsonPropertyParameters.get("floorArea").toString(),
                jsonPropertyParameters.get("parcelArea").toString(),
                jsonPropertyParameters.get("builtUpArea").toString(),
                jsonPropertyParameters.get("transportLinks").toString(),
                jsonPropertyParameters.get("parking").toString(),
                jsonPropertyParameters.get("arrivalRoad").toString(),
                jsonPropertyParameters.get("electricalCurrent").toString(),
                jsonPropertyParameters.get("hotWaterSource").toString(),
                jsonPropertyParameters.get("waterQuality").toString(),
                jsonPropertyParameters.get("gasDistribution").toString(),
                jsonPropertyParameters.get("heating").toString(),
                jsonPropertyParameters.get("fuelUsed").toString(),
                jsonPropertyParameters.get("drain").toString(),
                jsonPropertyParameters.get("establishedNetworkInfrastructure").toString(),
                jsonPropertyParameters.get("typeOfCovering").toString(),
                jsonPropertyParameters.get("typeOfPlaster").toString(),
                jsonPropertyParameters.get("humidityStatus").toString(),
                jsonPropertyParameters.get("ceilings").toString(),
                jsonPropertyParameters.get("roofShape").toString(),
                jsonPropertyParameters.get("constructionType").toString(),
                jsonPropertyParameters.get("bathroom").toString(),
                jsonPropertyParameters.get("toilet").toString(),
                jsonPropertyParameters.get("plotCharacter").toString(),
                jsonPropertyParameters.get("fencing").toString(),
                jsonPropertyParameters.get("gardenCondition").toString(),
                jsonPropertyParameters.get("garden").toString(),
                jsonPropertyParameters.get("secondarySpaces").toString(),
                jsonPropertyParameters.get("surroundingAreas").toString(),
                jsonPropertyParameters.get("services").toString(),
                jsonPropertyParameters.get("useOfFreeTime").toString()
                );

        return propertyParameters;
    }
}
