package com.app.salesforce.model;

import com.app.salesforce.response.Clas;
import com.app.salesforce.response.Subjects;

import java.util.ArrayList;

public class SubjectAndClassesModel {

    private Subjects subject;
    private ArrayList<Clas> classes;
    private boolean isSelected=false;
    public SubjectAndClassesModel(Subjects subject) {
        this.subject = subject;
        this.classes = (ArrayList<Clas>) subject.getClasses();
    }



    public SubjectAndClassesModel() {
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public ArrayList<Clas> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Clas> classes) {
        this.classes = classes;
    }
}
