package cz.zcu.kiv.nlp.ir.trec.data.document.crawlered;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Třída reprezentující data stažená z internetu pomocí crawleru.
 */
public class CrawleredDocument implements Document, Serializable {

    /** ID článku. */
    private String id;

    /** Titulek článku. */
    private String title = "";

    /** Cena bytu. */
    private String price = "";

    /** Dodatečné informace k ceně. */
    private String priceAdditionalInfo = "";

    /** Detailní overview. */
    private DetailedOverview detailedOverview;

    /** Popisek článku. */
    private String description = "";

    /** Parametry bytu. */
    private PropertyParameters propertyParameters;

    /** Url. */
    private String url = "";

    @Override
    public String getText() {
        return description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public String getDataForPreprocessing() {
        return title + " " + price + " " + priceAdditionalInfo + " " + detailedOverview.getText() + " " + description + " " +
                propertyParameters.getText() + " " + url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PropertyParameters getPropertyParameters() {
        return propertyParameters;
    }

    public void setPropertyParameters(PropertyParameters propertyParameters) {
        this.propertyParameters = propertyParameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DetailedOverview getDetailedOverview() {
        return detailedOverview;
    }

    public void setDetailedOverview(DetailedOverview detailedOverview) {
        this.detailedOverview = detailedOverview;
    }

    public String getPriceAdditionalInfo() {
        return priceAdditionalInfo;
    }

    public void setPriceAdditionalInfo(String priceAdditionalInfo) {
        this.priceAdditionalInfo = priceAdditionalInfo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
