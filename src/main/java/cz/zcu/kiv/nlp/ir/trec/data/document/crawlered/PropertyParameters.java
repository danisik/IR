package cz.zcu.kiv.nlp.ir.trec.data.document.crawlered;

public class PropertyParameters {

    private String areaUsable = "";
    private String floorArea = "";
    private String parcelArea = "";
    private String builtUpArea = "";
    private String transportLinks = "";
    private String parking = "";
    private String arrivalRoad = "";
    private String electricalCurrent = "";
    private String hotWaterSource = "";
    private String waterQuality = "";
    private String gasDistribution = "";
    private String heating = "";
    private String fuelUsed = "";
    private String drain = "";
    private String establishedNetworkInfrastructure = "";
    private String typeOfCovering = "";
    private String typeOfPlaster = "";
    private String humidityStatus = "";
    private String ceilings = "";
    private String roofShape = "";
    private String constructionType = "";
    private String bathroom = "";
    private String toilet = "";
    private String plotCharacter = "";
    private String fencing = "";
    private String gardenCondition = "";
    private String garden = "";
    private String secondarySpaces = "";
    private String surroundingAreas = "";
    private String services = "";
    private String useOfFreeTime = "";

    public PropertyParameters(String areaUsable, String floorArea, String parcelArea, String builtUpArea, String transportLinks, String parking,
                              String arrivalRoad, String electricalCurrent, String hotWaterSource, String waterQuality, String gasDistribution,
                              String heating, String fuelUsed, String drain, String establishedNetworkInfrastructure, String typeOfCovering,
                              String typeOfPlaster, String humidityStatus, String ceilings, String roofShape, String constructionType,
                              String bathroom, String toilet, String plotCharacter, String fencing, String gardenCondition, String garden,
                              String secondarySpaces, String surroundingAreas, String services, String useOfFreeTime) {
        this.areaUsable = areaUsable;
        this.floorArea = floorArea;
        this.parcelArea = parcelArea;
        this.builtUpArea = builtUpArea;
        this.transportLinks = transportLinks;
        this.parking = parking;
        this.arrivalRoad = arrivalRoad;
        this.electricalCurrent = electricalCurrent;
        this.hotWaterSource = hotWaterSource;
        this.waterQuality = waterQuality;
        this.gasDistribution = gasDistribution;
        this.heating = heating;
        this.fuelUsed = fuelUsed;
        this.drain = drain;
        this.establishedNetworkInfrastructure = establishedNetworkInfrastructure;
        this.typeOfCovering = typeOfCovering;
        this.typeOfPlaster = typeOfPlaster;
        this.humidityStatus = humidityStatus;
        this.ceilings = ceilings;
        this.roofShape = roofShape;
        this.constructionType = constructionType;
        this.bathroom = bathroom;
        this.toilet = toilet;
        this.plotCharacter = plotCharacter;
        this.fencing = fencing;
        this.gardenCondition = gardenCondition;
        this.garden = garden;
        this.secondarySpaces = secondarySpaces;
        this.surroundingAreas = surroundingAreas;
        this.services = services;
        this.useOfFreeTime = useOfFreeTime;
    }

    public String getAreaUsable() {
        return areaUsable;
    }

    public String getFloorArea() {
        return floorArea;
    }

    public String getParcelArea() {
        return parcelArea;
    }

    public String getBuiltUpArea() {
        return builtUpArea;
    }

    public String getTransportLinks() {
        return transportLinks;
    }

    public String getParking() {
        return parking;
    }

    public String getArrivalRoad() {
        return arrivalRoad;
    }

    public String getElectricalCurrent() {
        return electricalCurrent;
    }

    public String getHotWaterSource() {
        return hotWaterSource;
    }

    public String getWaterQuality() {
        return waterQuality;
    }

    public String getGasDistribution() {
        return gasDistribution;
    }

    public String getHeating() {
        return heating;
    }

    public String getFuelUsed() {
        return fuelUsed;
    }

    public String getDrain() {
        return drain;
    }

    public String getEstablishedNetworkInfrastructure() {
        return establishedNetworkInfrastructure;
    }

    public String getTypeOfCovering() {
        return typeOfCovering;
    }

    public String getTypeOfPlaster() {
        return typeOfPlaster;
    }

    public String getHumidityStatus() {
        return humidityStatus;
    }

    public String getCeilings() {
        return ceilings;
    }

    public String getRoofShape() {
        return roofShape;
    }

    public String getConstructionType() {
        return constructionType;
    }

    public String getBathroom() {
        return bathroom;
    }

    public String getToilet() {
        return toilet;
    }

    public String getPlotCharacter() {
        return plotCharacter;
    }

    public String getFencing() {
        return fencing;
    }

    public String getGardenCondition() {
        return gardenCondition;
    }

    public String getGarden() {
        return garden;
    }

    public String getSecondarySpaces() {
        return secondarySpaces;
    }

    public String getSurroundingAreas() {
        return surroundingAreas;
    }

    public String getServices() {
        return services;
    }

    public String getUseOfFreeTime() {
        return useOfFreeTime;
    }

    public String getText() {
        return areaUsable + " " + floorArea + " " + parcelArea + " " + builtUpArea + " " + transportLinks + " " + parking + " " + arrivalRoad + " " + electricalCurrent
                + " " + hotWaterSource + " " + waterQuality + " " + gasDistribution + " " + heating + " " + fuelUsed + " " + drain  + " " + establishedNetworkInfrastructure
                + " " + typeOfCovering  + " " + typeOfPlaster + " " + humidityStatus + " " + ceilings + " " + roofShape + " " + constructionType
                + " " + bathroom + " " + toilet + " " + plotCharacter + " " + fencing + " " + gardenCondition + " " + garden + " " + secondarySpaces
                + " " + surroundingAreas + " " + services + " " + useOfFreeTime;
    }
}
