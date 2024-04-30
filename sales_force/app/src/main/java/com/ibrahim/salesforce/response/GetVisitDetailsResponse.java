package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVisitDetailsResponse {
    @SerializedName("MyVisits")
    @Expose
    private List<MyVisit> myVisits = null;

    public List<MyVisit> getMyVisits() {
        return myVisits;
    }

    public void setMyVisits(List<MyVisit> myVisits) {
        this.myVisits = myVisits;
    }

    public class MyVisit {

        @SerializedName("VisitDate")
        @Expose
        private String visitDate;
        @SerializedName("CustomerName")
        @Expose
        private String customerName;

        public String getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(String visitDate) {
            this.visitDate = visitDate;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

    }
}
