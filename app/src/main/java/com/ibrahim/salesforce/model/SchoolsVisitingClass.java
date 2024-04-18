package com.ibrahim.salesforce.model;

public class SchoolsVisitingClass {
    String id, sales_officer_name, visits;

    public SchoolsVisitingClass(String id, String sales_officer_name, String visits) {
        this.id = id;
        this.sales_officer_name = sales_officer_name;
        this.visits = visits;
    }

    public SchoolsVisitingClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSales_officer_name() {
        return sales_officer_name;
    }

    public void setSales_officer_name(String sales_officer_name) {
        this.sales_officer_name = sales_officer_name;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }
}
