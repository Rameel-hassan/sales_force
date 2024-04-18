package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVisitsLocationResponse {
    @SerializedName("MyVisitsMapView")
    @Expose
    private List<MyVisitsMapView> myVisitsMapView = null;

    public List<MyVisitsMapView> getMyVisitsMapView() {
        return myVisitsMapView;
    }

    public void setMyVisitsMapView(List<MyVisitsMapView> myVisitsMapView) {
        this.myVisitsMapView = myVisitsMapView;
    }

    public class MyVisitsMapView {

        @SerializedName("VisitDate")
        @Expose
        private String visitDate;
        @SerializedName("Lattitude")
        @Expose
        private double lattitude;
        @SerializedName("Longitude")
        @Expose
        private double longitude;

        public String getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(String visitDate) {
            this.visitDate = visitDate;
        }

        public double getLattitude() {
            return lattitude;
        }

        public void setLattitude(double lattitude) {
            this.lattitude = lattitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
