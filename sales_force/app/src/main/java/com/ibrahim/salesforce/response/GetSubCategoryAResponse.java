package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSubCategoryAResponse {

    @SerializedName("SubCategoryA")
    @Expose
    private List<SubCategoryA> subCategoryA;

    public List<SubCategoryA> getSubCategoryA() {
        return subCategoryA;
    }

    public void setSubCategoryA(List<SubCategoryA> subCategoryA) {
        this.subCategoryA = subCategoryA;
    }

    public class SubCategoryA {

        @SerializedName("SubCategoryAID")
        @Expose
        private int subCategoryAID;
        @SerializedName("SubCategoryAName")
        @Expose
        private String subCategoryAName;

        public SubCategoryA(int subCategoryAID, String subCategoryAName) {
            this.subCategoryAID = subCategoryAID;
            this.subCategoryAName = subCategoryAName;
        }

        public int getSubCategoryAID() {
            return subCategoryAID;
        }

        public void setSubCategoryAID(int subCategoryAID) {
            this.subCategoryAID = subCategoryAID;
        }

        public String getSubCategoryAName() {
            return subCategoryAName;
        }

        public void setSubCategoryAName(String subCategoryAName) {
            this.subCategoryAName = subCategoryAName;
        }

        @Override
        public String toString() {
            return getSubCategoryAName();
        }
    }
}
