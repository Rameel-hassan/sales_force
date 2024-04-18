package com.ibrahim.salesforce.model;

public class MainOption {

    private int icon_id;
    private String option_name;

    public MainOption(int icon_id, String option_name) {
        this.icon_id = icon_id;
        this.option_name = option_name;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }
}
