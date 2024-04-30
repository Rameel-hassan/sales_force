package com.app.salesforce.model;

import com.app.salesforce.response.ServerResponse;


public class GetSchoolsByCity extends ServerResponse {

    int SchoolID;
    double Latitude;
    double Longitude;

    public GetSchoolsByCity() {    }



    public GetSchoolsByCity(int schoolID, double latitude, double longitude) {
        SchoolID = schoolID;
        Latitude = latitude;
        Longitude = longitude;
    }

    public int getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(int schoolID) {
        SchoolID = schoolID;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
