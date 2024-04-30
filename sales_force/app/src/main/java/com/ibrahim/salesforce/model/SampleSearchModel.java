package com.app.salesforce.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SampleSearchModel implements Searchable {
    private String mTitle;
    private int ID;

    public SampleSearchModel(String title, int ID) {
        mTitle = title;
        this.ID = ID;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SampleSearchModel setTitle(String title,int ID) {
        mTitle = title;

        return this;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}