package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVisitHistoryResponse {

    @SerializedName("VisitHistory")
    @Expose
    private List<VisitHistory> visitHistoryList = null;

    public List<VisitHistory> getVisitHistoryList() {
        return visitHistoryList;
    }

    public void setVisitHistoryList(List<VisitHistory> visitHistoryList) {
        this.visitHistoryList = visitHistoryList;
    }

    public class VisitHistory{
        @SerializedName("RetailerName")
        @Expose
        private String retailerName;
        @SerializedName("Address")
        @Expose
        private String address;
        @SerializedName("VisitDate")
        @Expose
        private String visitDate;
        @SerializedName("PurposeOfVisit")
        @Expose
        private String purposeOfVisit;
        @SerializedName("ContactPerson")
        @Expose
        private String contactPerson;
        @SerializedName("Comments")
        @Expose
        private String comments;


        private String visitType;
        private int srNo;

        public String getRetailerName() {
            return retailerName;
        }

        public void setRetailerName(String retailerName) {
            this.retailerName = retailerName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(String visitDate) {
            this.visitDate = visitDate;
        }

        public String getPurposeOfVisit() {
            return purposeOfVisit;
        }

        public void setPurposeOfVisit(String purposeOfVisit) {
            this.purposeOfVisit = purposeOfVisit;
        }

        public String getContactPerson() {
            if(contactPerson==null){
                return "";
            }
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public int getSrNo() {
            return srNo;
        }

        public void setSrNo(int srNo) {
            this.srNo = srNo;
        }

        public String getVisitType() {
            return visitType;
        }

        public void setVisitType(String visitType) {
            this.visitType = visitType;
        }
    }

}
