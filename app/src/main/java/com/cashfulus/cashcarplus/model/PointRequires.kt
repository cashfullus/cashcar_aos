package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class PointResponseData (
    @SerializedName("data") val data: PointResponse
)
data class PointResponse(
    @SerializedName("is_ongoing_donate") val isOngoingDonate: String,
    @SerializedName("is_ongoing_point") val isOngoingPoint: String,
    @SerializedName("page_count") val pageCount: Int,
    @SerializedName("scheduled_point") val scheduledPoint: Int,
    @SerializedName("user_point") val userPoint: Int,
    @SerializedName("point_history") val pointHistory: ArrayList<PointHistoryRow>
)
data class PointHistoryRow(
    @SerializedName("contents") val contents: String,
    @SerializedName("point") val point: Int,
    @SerializedName("register_time") val registerTime: String
)

data class BankInfo(
    @SerializedName("account_bank") val accountBank: String,
    @SerializedName("account_number") val account: String,
    @SerializedName("account_resident_registration") val account_resident_registration : String,
    @SerializedName("deposit") val deposit: Int,
    @SerializedName("name") val name: String,
    @SerializedName("ongoing") val ongoing: String,
    @SerializedName("status") val status: Boolean,
    @SerializedName("user_id") val userId: Int
)

data class WithdrawRequest(
    @SerializedName("name") val name: String,
    @SerializedName("account_bank") val accountBank: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("account_resident_registration") val account_resident_registration : String,
    @SerializedName("account_resident_registration_back") val account_resident_registration_back : String,
    @SerializedName("is_main") val isMain: Int,
    @SerializedName("withdrawal_point") val point: Int
)

data class WithdrawResponse(
    @SerializedName("deposit") val deposit: Boolean,
    @SerializedName("ongoing") val ongoing: Boolean
)