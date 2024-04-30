package com.app.salesforce.request;

import com.app.salesforce.model.CompetitorDetail;
import com.app.salesforce.model.JobItemsDetails;
import com.app.salesforce.model.SelectedVisitItems;

import java.util.List;

public class VisitReportRequest {

    private int RetailerID;
    private String ActivityType;
    private String PurposeofVisit;
    private String ActivityDetails;
    private String NextVisitDate;
    private String PreviousVisitDate;
    private int SaleOfficerId;
    private String SampleRecipt;
    private List<SelectedVisitItems> JobItems;
    private int JobID;
    private List<CompetitorDetail> CompetitorDetails;
    private String ContactPersonName;
    private String ContactPersonDesignation;
    private String ContcatPersonPhNo;
    private String SampleMonth;
    private String SchoolShopName;
    private String SessionMonth;
    private int eXValue;
    private boolean isCombined;
    private int otherSOID;


    private List<JobItemsDetails> JobItemDetails;

    public int getRetailerID() {
        return RetailerID;
    }

    public void setRetailerID(int retailerID) {
        RetailerID = retailerID;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getPurposeofVisit() {
        return PurposeofVisit;
    }

    public void setPurposeofVisit(String purposeofVisit) {
        PurposeofVisit = purposeofVisit;
    }

    public String getActivityDetails() {
        return ActivityDetails;
    }

    public void setActivityDetails(String activityDetails) {
        ActivityDetails = activityDetails;
    }

    public String getNextVisitDate() {
        return NextVisitDate;
    }

    public void setNextVisitDate(String nextVisitDate) {
        NextVisitDate = nextVisitDate;
    }

    public int getSaleOfficerId() {
        return SaleOfficerId;
    }

    public void setSaleOfficerId(int saleOfficerId) {
        SaleOfficerId = saleOfficerId;
    }

    public String getSampleRecipt() {
        return SampleRecipt;
    }

    public void setSampleRecipt(String sampleRecipt) {
        SampleRecipt = sampleRecipt;
    }

    public List<SelectedVisitItems> getJobItems() {
        return JobItems;
    }

    public void setJobItems(List<SelectedVisitItems> jobItems) {
        JobItems = jobItems;
    }

    public String getPreviousVisitDate() {
        return PreviousVisitDate;
    }

    public void setPreviousVisitDate(String previousVisitDate) {
        PreviousVisitDate = previousVisitDate;
    }

    public List<JobItemsDetails> getJobItemDetails() {
        return JobItemDetails;
    }

    public void setJobItemDetails(List<JobItemsDetails> jobItemDetails) {
        JobItemDetails = jobItemDetails;
    }
    public List<CompetitorDetail> getCompetitorDetails() {
        return CompetitorDetails;
    }

    public void setCompetitorDetails(List<CompetitorDetail> competitorDetails) {
        CompetitorDetails = competitorDetails;
    }
    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }
    public String getContactPersonName() {
        return ContactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        ContactPersonName = contactPersonName;
    }

    public String getContactPersonDesignation() {
        return ContactPersonDesignation;
    }

    public void setContactPersonDesignation(String contactPersonDesignation) {
        ContactPersonDesignation = contactPersonDesignation;
    }

    public String getContcatPersonPhNo() {
        return ContcatPersonPhNo;
    }

    public void setContcatPersonPhNo(String contcatPersonPhNo) {
        ContcatPersonPhNo = contcatPersonPhNo;
    }

    public String getSampleMonth() {
        return SampleMonth;
    }

    public void setSampleMonth(String sampleMonth) {
        SampleMonth = sampleMonth;
    }

    public String getSessionMonth() {
        return SessionMonth;
    }

    public void setSessionMonth(String sessionMonth) {
        SessionMonth = sessionMonth;
    }

    public int geteXValue() {
        return eXValue;
    }

    public void seteXValue(int eXValue) {
        this.eXValue = eXValue;
    }

    public String getSchoolShopName() {
        return SchoolShopName;
    }

    public void setSchoolShopName(String schoolShopName) {
        SchoolShopName = schoolShopName;
    }

    public boolean isCombined() {
        return isCombined;
    }

    public void setCombined(boolean combined) {
        isCombined = combined;
    }

    public int getOtherSOID() {
        return otherSOID;
    }

    public void setOtherSOID(int otherSOID) {
        this.otherSOID = otherSOID;
    }


}
