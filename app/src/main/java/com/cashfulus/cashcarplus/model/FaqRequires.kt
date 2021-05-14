package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class FaqResponseData (
    @SerializedName("data") val data: ArrayList<FaqResponse>
)
data class FaqResponse(
    @SerializedName("description") val description: String,
    @SerializedName("title") val title: String
)