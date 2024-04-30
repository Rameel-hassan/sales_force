package com.app.salesforce.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TargatedProducts {
    @SerializedName("TargatedProducts")
    @Expose
    var targatedProductsList: List<TargetedProducts>? = null

    data class TargetedProducts(
        @SerializedName("SeriesName") val seriesName: String,
        @SerializedName("ClassName") val className: String,
        @SerializedName("SubjectName") val subjectName: String
    )
}