package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCitiesResponse extends ServerResponse {
    @SerializedName("Cities")
    @Expose
    private List<City> cities = null;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public class City extends GetCitiesResponse{

        @SerializedName("ID")
        @Expose
        private int iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("ParentID")
        @Expose
        private int parentID;

        //        @SerializedName("ParentID") var parentId: Int = 0

        public City(int iD, String name) {
            this.iD = iD;
            this.name = name;
        }

        public City(int ID, String name, int parentID){
            this.iD = ID;
            this.name = name;
            this.parentID = parentID;
        }

        public int getID() {
            return iD;
        }

        public void setID(int iD) {
            this.iD = iD;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getParentID() {
            return parentID;
        }

        public void setParentID(int parentID) {
            this.parentID = parentID;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
