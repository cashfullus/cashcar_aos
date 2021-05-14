package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class AlarmResponseData(
    @SerializedName("data") val data: ArrayList<AlarmResponse>
)
data class AlarmResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("required") val required: Int,
    @SerializedName("is_read_alarm") val isReadAlarm: Int,
    @SerializedName("description") val description: String,
    @SerializedName("alarm_type") val alarmType: String,
)