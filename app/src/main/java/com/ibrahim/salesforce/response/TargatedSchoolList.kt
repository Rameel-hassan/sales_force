package com.ibrahim.salesforce.response

import android.view.View
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TargatedSchoolList {
    @SerializedName("TargatedSchoolList")
    @Expose
    val targetedSchoolsList: List<TargetedSchool>? = null

    data class TargetedSchool(
        @SerializedName("ID") val ID: Int,
        @SerializedName("ShopName") val ShopName: String?,
        @SerializedName("Name") val Name: String,
        @SerializedName("Phone1") val Phone1: String,
        var srNo: Int = 0,
        var viewVisibility: Int = View.GONE,
        var assignProductVisibility: Int = View.GONE,
        var isProductsAssigned: Boolean = false
    )

}