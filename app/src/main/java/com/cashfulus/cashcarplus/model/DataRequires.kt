package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

class DataResponseList : ArrayList<DataResponse>()

data class DataResponse(
    @SerializedName("intData") val intData: Int,
    @SerializedName("strData") val strData: String
)

data class DataRequest (
    @SerializedName("intData") val intData: Int,
    @SerializedName("strData") val strData: String
)