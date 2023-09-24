package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("login_type") val login_type: String // kakao or normal
)
data class LoginPWRequest(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
        @SerializedName("login_type") val login_type: String // kakao or normal
)

data class LoginResponse(
    @SerializedName("data") val data: LoginResponseData?,
    @SerializedName("status") val status: Boolean,
)
data class LoginResponseData(
    @SerializedName("jwt_token") val jwt_token: String,
    @SerializedName("user_id") val user_id: Int
)

data class RegisterRequest(
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("email") val email: String,
    @SerializedName("alarm") val alarm: Int,
    @SerializedName("marketing") val marketing: Int,
    @SerializedName("login_type") val login_type: String
)
data class RegisterPWRequest(
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("alarm") val alarm: Int,
    @SerializedName("marketing") val marketing: Int,
    @SerializedName("login_type") val login_type: String
)

data class RegisterResponse(
    @SerializedName("data") val data: RegisterResponseData,
    @SerializedName("status") val status: Boolean,
)
data class RegisterResponseData(
    @SerializedName("jwt_token") val jwt_token: String,
    @SerializedName("user_id") val user_id: Int
)

data class UserUpdateResponse(
    @SerializedName("data") val data: String,
    @SerializedName("status") val status: Boolean,
)
data class UserUpdateResponseWithImg(
    @SerializedName("data") val data: String,
    @SerializedName("image") val image: String,
    @SerializedName("status") val status: Boolean,
)

data class UserInfoResponse(
    @SerializedName("data") val data: UserInfoResponseData,
    @SerializedName("status") val status: Boolean
)
data class UserInfoResponseData(
    @SerializedName("alarm") val alarm: Int,
    @SerializedName("call_number") val call_number: String,
    @SerializedName("date_of_birth") val date_of_birth: String,
    @SerializedName("email") val email: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("marketing") val marketing: Int,
    @SerializedName("nick_name") val nick_name: String,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile_image") val profileImage: String?
)

data class UserFcmUpdateRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("fcm_token") val fcmToken: String
)

data class UserAddress(
    @SerializedName("call_number") val callNumber: String,
    @SerializedName("detail_address") val detailAddress: String,
    @SerializedName("main_address") val mainAddress: String,
    @SerializedName("name") val name: String
)
data class UserAddressResponseData(
    @SerializedName("data") val data: UserAddress,
    @SerializedName("is_user") val isUser: Boolean
)
data class UserAddressUpdated(
    @SerializedName("is_update") val isUpdate: Boolean
)