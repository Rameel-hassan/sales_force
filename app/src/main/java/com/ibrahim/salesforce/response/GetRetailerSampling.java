package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRetailerSampling {
    @SerializedName("RetailerSampling")
    @Expose
    private List<SampleItems> sampleItemsList = null;

    public List<SampleItems> getSampleItemsList() {
        return sampleItemsList;
    }

    public void setSampleItemsList(List<SampleItems> sampleItemsList) {
        this.sampleItemsList = sampleItemsList;
    }

    public class SampleItems {

        @SerializedName("JobID")
        @Expose
        private int JobID;
        @SerializedName("Subjects")
        @Expose
        private int Subjects;
        @SerializedName("SubjectName")
        @Expose
        private String SubjectName;
        @SerializedName("Series")
        @Expose
        private int Series;
        @SerializedName("SeriesName")
        @Expose
        private String SeriesName;
        @SerializedName("ClassID")
        @Expose
        private int ClassID;
        @SerializedName("ClassName")
        @Expose
        private String ClassName;
        @SerializedName("JobItemID")
        @Expose
        private int JobItemID;
        @SerializedName("Delivered")
        @Expose
        private boolean Delivered;
        @SerializedName("Returned")
        @Expose
        private boolean Returned;
        @SerializedName("Competitors")
        @Expose
        private String Competitors;
        @SerializedName("bFinal")
        @Expose
        private boolean bFinal;

        public boolean isbFinal() {
            return bFinal;
        }

        public void setbFinal(boolean bFinal) {
            this.bFinal = bFinal;
        }


        public SampleItems(int jobID, int subjects, String subjectName, int series, String seriesName, int classID, String className, String Competitors, int JobItemID, boolean isDelievered, boolean isReturned, boolean bFinal) {
            JobID = jobID;
            Subjects = subjects;
            SubjectName = subjectName;
            Series = series;
            SeriesName = seriesName;
            ClassID = classID;
            ClassName = className;
            this.JobItemID=JobItemID;
            Delivered=isDelievered;
            Returned=isReturned;
            this.bFinal = bFinal;
            this.Competitors=Competitors;

        }

        public int getJobID() {
            return JobID;
        }

        public void setJobID(int jobID) {
            JobID = jobID;
        }

        public int getSubjects() {
            return Subjects;
        }

        public void setSubjects(int subjects) {
            Subjects = subjects;
        }

        public String getSubjectName() {
            return SubjectName;
        }

        public void setSubjectName(String subjectName) {
            SubjectName = subjectName;
        }

        public int getSeries() {
            return Series;
        }

        public void setSeries(int series) {
            Series = series;
        }

        public String getSeriesName() {
            return SeriesName;
        }

        public void setSeriesName(String seriesName) {
            SeriesName = seriesName;
        }

        public int getClassID() {
            return ClassID;
        }

        public void setClassID(int classID) {
            ClassID = classID;
        }

        public String getClassName() {
            return ClassName;
        }

        public void setClassName(String className) {
            ClassName = className;
        }

        public int getJobItemID() {
            return JobItemID;
        }

        public void setJobItemID(int jobItemID) {
            JobItemID = jobItemID;
        }
        public boolean isDelivered() {
            return Delivered;
        }

        public void setDelivered(boolean delivered) {
            Delivered = delivered;
        }

        public boolean isReturned() {
            return Returned;
        }

        public void setReturned(boolean returned) {
            Returned = returned;
        }

        public String getCompetitors() {
            return Competitors;
        }

        public void setCompetitors(String competitors) {
            Competitors = competitors;
        }
    }
}
