package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class InquiryRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("company") val company: String,
    @SerializedName("staffName") val staffName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("contents") val contents: String
)