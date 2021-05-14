package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class NoticeDataList(
    @SerializedName("data") val data: ArrayList<NoticeData>
)

data class NoticeData(
    @SerializedName("notice_id") val noticeId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("register_time") val registerTime: String
)