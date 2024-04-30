package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSubAreasResponse extends ServerResponse {
    @SerializedName("SubAreas")
    @Expose
    List<SubArea> subAreas=null;

    public List<SubArea> getSubAreas() {
        return subAreas;
    }

    public class SubArea {
        // Getters and Setters (optional but recommended for accessing the private fields)
        @SerializedName("ID")
        @Expose
        private int ID ;
        @SerializedName("Name")
        @Expose
        private String name;
        public SubArea(int iD, String name) {
            this.ID = iD;
            this.name = name;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
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
