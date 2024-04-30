package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetLiveLocationResponse extends ServerResponse {
    @SerializedName("Data")
    @Expose
    private long data;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }
}
