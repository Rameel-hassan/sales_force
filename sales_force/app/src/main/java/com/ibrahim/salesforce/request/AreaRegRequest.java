package com.app.salesforce.request;

public class AreaRegRequest {
    private int UserId=-1;
    private int RegionID,CityId;
    String area_name;

    public AreaRegRequest(int userId, int regionID, int cityId, String area_name) {
        UserId = userId;
        RegionID = regionID;
        CityId = cityId;
        this.area_name = area_name;
    }

    public AreaRegRequest() {
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getRegionID() {
        return RegionID;
    }

    public void setRegionID(int regionID) {
        RegionID = regionID;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }
}
