package com.cashfulus.cashcarplus.util

import com.google.gson.annotations.SerializedName

data class TestAuth1(
    @SerializedName("email") val email: String = "deftmata@samsung.kr",
    @SerializedName("password") val password: String = "faker",
    @SerializedName("login_type") val loginType: String = "normal"
)