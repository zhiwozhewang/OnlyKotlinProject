package com.enn.ionic.bean

import com.google.gson.annotations.SerializedName

data class BindCompanyQuryBean(

    val phone: String = "",
    val city: String = "",
    val customerName: String = "",
    val customerAddress: String = "",
    @SerializedName("creditCode", alternate = ["dutyParagraph"])
    val dutyParagraph: String = "",

    val bindType: String,
    val companyCode: String,
    val companyName: String,
    val customerBp: String
)