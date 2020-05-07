package cz.zcu.kiv.nlp.ir.trec.data.document.crawlered;

public class DetailedOverview {

    private String offerNumber = "";
    private String ownership = "";
    private String construction = "";
    private String propertyStatus = "";
    private String objectLocation = "";
    private String locality = "";

    public DetailedOverview() {
    }

    public DetailedOverview(String offerNumber, String ownership, String construction, String propertyStatus, String objectLocation, String locality) {
        this.offerNumber = offerNumber;
        this.ownership = ownership;
        this.construction = construction;
        this.propertyStatus = propertyStatus;
        this.objectLocation = objectLocation;
        this.locality = locality;
    }

    public String getOfferNumber() {
        return offerNumber;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getConstruction() {
        return construction;
    }

    public String getPropertyStatus() {
        return propertyStatus;
    }

    public String getObjectLocation() {
        return objectLocation;
    }

    public String getLocality() {
        return locality;
    }

    public String getText() {
        return offerNumber + " " + ownership + " " + construction + " " + propertyStatus + " " + objectLocation + " " + locality;
    }
}
