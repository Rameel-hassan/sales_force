package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAreasResponse extends ServerResponse {
    @SerializedName("Areas")
    @Expose
    private List<Area> areas = null;

    public List<Area> getAreas() {
        return areas;
    }

    public class Area {

        @SerializedName("ID")
        @Expose
        private int iD;
        @SerializedName("Name")
        @Expose
        private String name;

        public Area(int iD, String name) {
            this.iD = iD;
            this.name = name;
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

        @Override
        public String toString() {
            return getName();
        }
    }
}
