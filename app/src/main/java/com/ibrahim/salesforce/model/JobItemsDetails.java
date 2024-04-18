package com.ibrahim.salesforce.model;

public class JobItemsDetails {
    private int ClassID;
    private int SeriesID;
    private int SubjectID;;

    public JobItemsDetails(int classID, int seriesID, int subjectID) {
        ClassID = classID;
        SeriesID = seriesID;
        SubjectID = subjectID;

    }

    public int getClassID() {
        return ClassID;
    }

    public void setClassID(int classID) {
        ClassID = classID;
    }

    public int getSeriesID() {
        return SeriesID;
    }

    public void setSeriesID(int seriesID) {
        SeriesID = seriesID;
    }

    public int getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(int subjectID) {
        SubjectID = subjectID;
    }


}
