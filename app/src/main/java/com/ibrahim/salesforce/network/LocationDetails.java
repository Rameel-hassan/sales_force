package com.ibrahim.salesforce.network;

import com.google.gson.annotations.SerializedName;



/**
 *
 *
 * @author Rameel Hassan
 * Created 6/18/22 at 9:29 PM
 */
public class LocationDetails {
    int id;
    int adminCompanyID;
    int userID;
    Long createdDate;
    Double lat;
    @SerializedName("long")
    Double lng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminCompanyID() {
        return adminCompanyID;
    }

    public void setAdminCompanyID(int adminCompanyID) {
        this.adminCompanyID = adminCompanyID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
