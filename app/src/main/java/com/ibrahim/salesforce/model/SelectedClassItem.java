package com.ibrahim.salesforce.model;

import com.ibrahim.salesforce.response.Sery;

import java.util.ArrayList;
import java.util.List;

public class SelectedClassItem {
    private int selectedClassID;
    private int selectedSubjectID;
    private String className;
    private String subjectName;
    private Sery Series;
    private int JobItemID;
    private boolean Delievered;
    private boolean Returned;
    private boolean bFinal;
    private boolean required;
    private boolean isTempAdded;
    public ArrayList<Publisher> lstPublisher;
    public SelectedClassItem(int CLassID, String className, int selectedSubjectID, String subjectName, Sery series, boolean bFinal, ArrayList<String> competitors, int JobItemID, boolean Delievered, boolean Returned) {
        this.selectedClassID = CLassID;
        this.subjectName = subjectName;
        this.className=className;
        this.selectedSubjectID=selectedSubjectID;
        this.Series=series;
        this.bFinal = bFinal;
        this.JobItemID=JobItemID;
        this.Delievered=Delievered;
        this.Returned=Returned;
        initPublishers(competitors);

    }

    private void initPublishers(ArrayList<String> publishers) {
        lstPublisher= new ArrayList<>();
        for (String publisher : publishers) {
            lstPublisher.add(new Publisher(publisher,false));
        }
        lstPublisher.get(0).setSelected(true);
    }
    public SelectedClassItem(int CLassID, String className, int selectedSubjectID, String subjectName, Sery series, ArrayList<String> competitors, int JobItemID, boolean Delievered, boolean Returned,boolean isAdded) {
        this.selectedClassID = CLassID;
        this.subjectName = subjectName;
        this.className=className;
        this.selectedSubjectID=selectedSubjectID;
        this.Series=series;
        this.JobItemID=JobItemID;
        this.Delievered=Delievered;
        this.Returned=Returned;
        initPublishers(competitors);
        this.isTempAdded = isAdded;

    }

    public SelectedClassItem(int CLassID,  String className,int selectedSubjectID,String subjectName,Sery series,ArrayList<String> competitors, int JobItemID,boolean Delievered,boolean Returned) {
        this.selectedClassID = CLassID;
        this.subjectName = subjectName;
        this.className=className;
        this.selectedSubjectID=selectedSubjectID;
        this.Series=series;
        this.JobItemID=JobItemID;
        this.Delievered=Delievered;
        this.Returned=Returned;
        initPublishers(competitors);

    }

    public boolean isbFinal() {
        return bFinal;
    }

    public void setbFinal(boolean bFinal) {
        this.bFinal = bFinal;
    }

    public int getSelectedClassID() {
        return selectedClassID;
    }

    public void setSelectedClassID(int subjectID) {
        this.selectedClassID = subjectID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String subjectName) {
        this.className = subjectName;
    }

    public int getSelectedSubjectID() {
        return selectedSubjectID;
    }

    public void setSelectedSubjectID(int selectedSubjectID) {
        this.selectedSubjectID = selectedSubjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    public Sery getSeries() {
        return Series;
    }

    public void setSeries(Sery series) {
        Series = series;
    }

    public int getJobItemID() {
        return JobItemID;
    }

    public void setJobItemID(int jobItemID) {
        JobItemID = jobItemID;
    }
    public boolean isDelievered() {
        return Delievered;
    }

    public void setDelievered(boolean delievered) {
        Delievered = delievered;
    }

    public boolean isReturned() {
        return Returned;
    }

    public void setReturned(boolean returned) {
        Returned = returned;
    }
    public List<String> getLstPublisher() {

        ArrayList<String> list = new ArrayList<String>();
        for(Publisher p :lstPublisher){
            list.add(p.getPublisher());
        }
        return list;
    }

    public void setLstPublisher(ArrayList<String> lstPublisher) {
        initPublishers(lstPublisher);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isTempAdded() {
        return isTempAdded;
    }

    public void setTempAdded(boolean tempAdded) {
        isTempAdded = tempAdded;
    }
}

