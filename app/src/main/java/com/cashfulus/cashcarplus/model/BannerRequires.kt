package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("data") val data: ArrayList<BannerData>
)

data class BannerData(
    @SerializedName("id") val id: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("priority") val priority: Int,
)