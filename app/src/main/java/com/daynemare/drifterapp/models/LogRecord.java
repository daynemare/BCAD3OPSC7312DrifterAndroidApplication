package com.daynemare.drifterapp.models;

public class LogRecord {

    private String dateTime;
    private String transportMethod;
    private String startingLocation;
    private String destinationLocation;
    private String destLatitude;
    private String destLongitude;

    public LogRecord(){}

    public LogRecord(String dateTime, String transportMethod, String startingLocation, String destinationLocation, String destLatitude, String destLongitude) {
        this.dateTime = dateTime;
        this.transportMethod = transportMethod;
        this.startingLocation = startingLocation;
        this.destinationLocation = destinationLocation;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(String destLatitude) {
        this.destLatitude = destLatitude;
    }

    public String getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(String destLongitude) {
        this.destLongitude = destLongitude;
    }




}
