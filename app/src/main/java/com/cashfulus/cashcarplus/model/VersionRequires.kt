package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class VersionResponse(
    @SerializedName("version") val version: Int
)