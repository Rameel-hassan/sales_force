package com.app.salesforce.model;

public class CompetitorDetail {
   private  int RetailerID;
 private   int CompetitorID;

    public CompetitorDetail(int retailerID, int competitorID) {
        RetailerID = retailerID;
        CompetitorID = competitorID;
    }

    public int getRetailerID() {
        return RetailerID;
    }

    public void setRetailerID(int retailerID) {
        RetailerID = retailerID;
    }

    public int getCompetitorDetail() {
        return CompetitorID;
    }

    public void setCompetitorDetail(int competitorID) {
        CompetitorID = competitorID;
    }
}
