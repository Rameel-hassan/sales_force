package com.ibrahim.salesforce.model;

public class LatLongMockModel {
    private double latitude;
    private double longitude;

    public LatLongMockModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
