package com.app.salesforce.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTodayTargetedSchools {

    @SerializedName("TodayTargatedSchools")
    private List<TodayTargetedSchool> todayTargatedSchools;

    public List<TodayTargetedSchool> getTodayTargatedSchools() {
        return todayTargatedSchools;
    }


    public class TodayTargetedSchool {
        @SerializedName("Id")
        private int id;

        @SerializedName("VendorName")
        private String vendorName;

        @SerializedName("PrincipalName")
        private String principalName;

        @SerializedName("PhoneNumber")
        private String phoneNumber;

        @SerializedName("CityName")
        private String cityName;

        @SerializedName("AreaName")
        private String areaName;

        @SerializedName("Visited")
        private boolean visited;

        public TodayTargetedSchool() {
        }

        public TodayTargetedSchool(int id, String vendorName, String principalName, String phoneNumber, String cityName, String areaName) {
            this.id = id;
            this.vendorName = vendorName;
            this.principalName = principalName;
            this.phoneNumber = phoneNumber;
            this.cityName = cityName;
            this.areaName = areaName;
        }

        public TodayTargetedSchool(int id, String vendorName, String principalName, String phoneNumber, String cityName, String areaName, boolean visited) {
            this.id = id;
            this.vendorName = vendorName;
            this.principalName = principalName;
            this.phoneNumber = phoneNumber;
            this.cityName = cityName;
            this.areaName = areaName;
            this.visited = visited;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public void setPrincipalName(String principalName) {
            this.principalName = principalName;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public boolean getVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public int getId() {
            return id;
        }

        public String getVendorName() {
            return vendorName;
        }

        public String getPrincipalName() {
            return principalName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getCityName() {
            return cityName;
        }

        public String getAreaName() {
            return areaName;
        }
    }
}


