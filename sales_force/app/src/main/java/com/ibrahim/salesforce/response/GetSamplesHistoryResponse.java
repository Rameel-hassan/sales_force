package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSamplesHistoryResponse {
    @SerializedName("SampleHistory")
    @Expose
    private List<Sample> samplesHistory = null;

    @SerializedName("SampleHistoryDetail")
    @Expose
    private List<SampledDetails> sampledDetails=null;

    @SerializedName("Result")
    @Expose
    private String result="";

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Sample> getSamplesHistory() {
        return samplesHistory;
    }

    public void setSamplesHistory(List<Sample> samplesHistory) {
        this.samplesHistory = samplesHistory;
    }

    public List<SampledDetails> getSampledDetails() {
        return sampledDetails;
    }

    public void setSampledDetails(List<SampledDetails> sampledDetails) {
        this.sampledDetails = sampledDetails;
    }

    public class SampledDetails{
        @SerializedName("JobItemID") private int jobItemID;
        @SerializedName("SubjectName") private String subjectName;
        @SerializedName("SeriesName") private String seriesName;
        @SerializedName("ClassName") private String className;
        @SerializedName("Required") private boolean required;
        @SerializedName("Delivered") private boolean delivered;
        private String jobDate;
        private int srNo;

        public int getJobItemID() {
            return jobItemID;
        }

        public String getJobDate() {
            return jobDate;
        }

        public void setJobDate(String jobDate) {
            this.jobDate = jobDate;
        }

        public void setJobItemID(int jobItemID) {
            this.jobItemID = jobItemID;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getSeriesName() {
            return seriesName;
        }

        public void setSeriesName(String seriesName) {
            this.seriesName = seriesName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public boolean isDelivered() {
            return delivered;
        }

        public void setDelivered(boolean delivered) {
            this.delivered = delivered;
        }

        public int getSrNo() {
            return srNo;
        }

        public void setSrNo(int srNo) {
            this.srNo = srNo;
        }
    }

    public class Sample {
        @SerializedName("JobDate") private String JobDate;
        @SerializedName("JobID") private int JobID;
        @SerializedName("RetailerID") private int RetailerID;
        @SerializedName("ShopName") private String ShopName;
        @SerializedName("Address") private String shopAddress;
        @SerializedName("ContactCellNo")private String ContactNo;
        private int srNo;


        public String getJobDate() {
            return JobDate;
        }

        public void setJobDate(String jobDate) {
            JobDate = jobDate;
        }

        public int getJobID() {
            return JobID;
        }

        public void setJobID(int jobID) {
            JobID = jobID;
        }

        public int getRetailerID() {
            return RetailerID;
        }

        public void setRetailerID(int retailerID) {
            RetailerID = retailerID;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String shopName) {
            ShopName = shopName;
        }

        public String getShopAddress() {
            return shopAddress;
        }

        public void setShopAddress(String shopAddress) {
            this.shopAddress = shopAddress;
        }

        public String getContactNo() {
            return ContactNo;
        }

        public void setContactNo(String contactCellNo) {
            ContactNo = contactCellNo;
        }

        public int getSrNo() {
            return srNo;
        }

        public void setSrNo(int srNo) {
            this.srNo = srNo;
        }
    }
}
