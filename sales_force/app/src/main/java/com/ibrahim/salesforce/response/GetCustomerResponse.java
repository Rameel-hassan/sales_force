package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCustomerResponse extends ServerResponse {
    @SerializedName("Customers")
    @Expose
    private List<CustomersRelatedtoSO> customers = null;

    public List<CustomersRelatedtoSO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomersRelatedtoSO> customers) {
        this.customers = customers;
    }
}
