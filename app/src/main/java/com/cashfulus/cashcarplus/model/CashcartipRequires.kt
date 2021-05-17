package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

class CashcartipList : ArrayList<CashcartipListObject>()

data class CashcartipListObject(
    @SerializedName("cash_car_tip_id") val id: Int,
    @SerializedName("main_description") val description: String,
    @SerializedName("register_time") val registerTime: String, //"2021-05-14 10:41:53"
    @SerializedName("thumbnail_image") val image: String,
    @SerializedName("title") val title: String
)

data class CashcartipPost(
    @SerializedName("cash_car_tip_id") val id: Int,
    @SerializedName("image_information") val images: ArrayList<CashcartipImage>,
    @SerializedName("main_description") val description: String,
    @SerializedName("register_time") val registerTime: String, //"2021-05-14 10:42:05"
    @SerializedName("thumbnail_image") val thumbnailImage: String,
    @SerializedName("title") val title: String
)

data class CashcartipImage(
    @SerializedName("image") val image: String,
)