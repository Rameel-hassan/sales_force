package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSubCatResponse {
    @SerializedName("SubCategory")
    @Expose
    private List<SubCategory> subCategory = null;

    public List<SubCategory> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(List<SubCategory> subCategory) {
        this.subCategory = subCategory;
    }

    public class SubCategory {

        @SerializedName("ID")
        @Expose
        private int iD;
        @SerializedName("SubName")
        @Expose
        private String subName;

        public SubCategory(int iD, String subName) {
            this.iD = iD;
            this.subName = subName;
        }

        public int getID() {
            return iD;
        }

        public void setID(int iD) {
            this.iD = iD;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        @Override
        public String toString() {
            return getSubName();
        }
    }
}
