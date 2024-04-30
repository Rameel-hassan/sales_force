package com.app.salesforce.model;

public class Publisher {
    String publisher;
    boolean isSelected=false;

    public Publisher(String publisher, boolean b) {
        this.publisher=publisher;
        this.isSelected=b;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
