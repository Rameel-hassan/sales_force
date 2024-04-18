package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetZonesResponse extends ServerResponse {
    @SerializedName("Zones")
    @Expose
    private List<Zone> Zones;

    public List<Zone> getZones() {
        return Zones;
    }

    public void setZones(List<Zone> Zones) {
        this.Zones = Zones;
    }
    public class Zone extends  GetZonesResponse{
        @SerializedName("ID")
        @Expose
        private int ID;
        @SerializedName("Name")
        @Expose
        private String Name;

        public Zone(int i, String s) {
            ID=i;
            Name=s;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
        @Override
        public String toString() {
            return getName();
        }
    }
}

