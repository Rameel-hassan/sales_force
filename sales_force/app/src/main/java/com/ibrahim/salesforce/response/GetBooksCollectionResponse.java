package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBooksCollectionResponse extends ServerResponse {
    @SerializedName("AssignedSampleInfo")
    @Expose
    private List<BooksSampleInfo> assignedSampleInfo = null;

    public List<BooksSampleInfo> getAssignedSampleInfo() {
        return assignedSampleInfo;
    }

    public void setAssignedSampleInfo(List<BooksSampleInfo> assignedSampleInfo) {
        this.assignedSampleInfo = assignedSampleInfo;
    }
}
