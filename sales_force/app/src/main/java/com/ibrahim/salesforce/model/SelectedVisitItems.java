package com.app.salesforce.model;

public class SelectedVisitItems {

    private int Subject;
    private int Series;
    private String Competitors;
    private int SaleOfficerId;
    private int ClassID;
    private int JobID;
    private int JobItemID;
    private boolean Delivered;
    private boolean Returned;
    private boolean bFinal=false;

    public SelectedVisitItems(int subject, int series, int ClassID, String competitors, int saleOfficerId, int JobItemID, boolean Delivered, boolean Returned, boolean bFinal) {
        Subject = subject;
        Series = series;
        this.ClassID=ClassID;
        Competitors = competitors;
        SaleOfficerId = saleOfficerId;
        this.JobItemID=JobItemID;
        this.Delivered=Delivered;
        this.Returned=Returned;
        this.bFinal=bFinal;
    }

    public int getSubject() {
        return Subject;
    }

    public void setSubject(int subject) {
        Subject = subject;
    }

    public int getSeries() {
        return Series;
    }

    public void setSeries(int series) {
        Series = series;
    }


    public String getCompetitors() {
        return Competitors;
    }

    public void setCompetitors(String competitors) {
        Competitors = competitors;
    }

    public int getSaleOfficerId() {
        return SaleOfficerId;
    }

    public void setSaleOfficerId(int saleOfficerId) {
        SaleOfficerId = saleOfficerId;
    }
    public int getClassID() {
        return ClassID;
    }

    public void setClassID(int classID) {
        ClassID = classID;
    }
    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public int getJobItemID() {
        return JobItemID;
    }

    public void setJobItemID(int jobItemID) {
        JobItemID = jobItemID;
    }
    public boolean isDelievered() {
        return Delivered;
    }

    public void setDelievered(boolean delievered) {
        Delivered = delievered;
    }

    public boolean isReturned() {
        return Returned;
    }

    public void setReturned(boolean returned) {
        Returned = returned;
    }
    public boolean isbFinal() {
        return bFinal;
    }

    public void setbFinal(boolean bFinal) {
        this.bFinal = bFinal;
    }
}
