package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.TestAuth1
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

interface RemoteUserSource {
    suspend fun loginApi(loginData: LoginRequest): Response<String>
    suspend fun loginPWApi(loginData: LoginPWRequest): Response<String>
    suspend fun registerApi(registerRequest: RegisterRequest): Response<String>
    suspend fun registerPWApi(registerPWRequest: RegisterPWRequest): Response<String>
    suspend fun getUserInfo(userId: Int, authorization: String): Response<String>
    suspend fun postUserInfo(user_id: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, profile_image: File, authorization: String): Response<String>
    suspend fun postUserInfo(user_id: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, authorization: String): Response<String>
    suspend fun getUserAddress(user_id: Int, authorization: String): Response<String>
    suspend fun postUserAddress(user_id: Int, userAddressRequest: UserAddress, authorization: String): Response<String>
    suspend fun updateFcm(userFcmUpdateRequest: UserFcmUpdateRequest): Response<String>

    suspend fun postUserAlarm(is_on: Int, user_id: Int, authorization: String): Response<String>
    suspend fun postMarketingAlarm(is_on: Int, user_id: Int, authorization: String): Response<String>

    suspend fun loginTest(loginInfo: TestAuth1): Response<String>
}

class RemoteUserSourceImpl(private val service: Api) : RemoteUserSource {
    override suspend fun loginApi(loginData: LoginRequest) = service.loginApi(loginData)
    override suspend fun loginPWApi(loginData: LoginPWRequest) = service.loginPWApi(loginData)
    override suspend fun registerApi(registerRequest: RegisterRequest) = service.registerApi(registerRequest)
    override suspend fun registerPWApi(registerPWRequest: RegisterPWRequest) = service.registerApiPW(registerPWRequest)
    override suspend fun getUserInfo(userId: Int, authorization: String): Response<String> = service.getUserInfo(userId, authorization)
    override suspend fun getUserAddress(user_id: Int, authorization: String) = service.getUserAddress(user_id, authorization)
    override suspend fun postUserAddress(user_id: Int, userAddressRequest: UserAddress, authorization: String) = service.postUserAddress(user_id, userAddressRequest, authorization)
    override suspend fun updateFcm(userFcmUpdateRequest: UserFcmUpdateRequest) = service.updateFcm(userFcmUpdateRequest)
    override suspend fun loginTest(loginInfo: TestAuth1) = service.loginTmp(loginInfo)
    override suspend fun postUserAlarm(is_on: Int, user_id: Int, authorization: String) = service.postUserAlarm(is_on, user_id, authorization)
    override suspend fun postMarketingAlarm(is_on: Int, user_id: Int, authorization: String) = service.postMarketingAlarm(is_on, user_id, authorization)

    override suspend fun postUserInfo(user_id: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, authorization: String): Response<String> {
        val nicknameForm = nickname.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailForm = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameForm = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val callNumberForm = callNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderForm = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val birthForm = birth.toRequestBody("text/plain".toMediaTypeOrNull())
        val alarmForm = alarm.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val marketingForm = marketing.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return service.postUserInfo(user_id, nicknameForm, emailForm, nameForm, callNumberForm, genderForm, birthForm, alarmForm, marketingForm, authorization)
    }

    override suspend fun postUserInfo(user_id: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, profile_image: File, authorization: String): Response<String> {
        val profileImageFile = profile_image.asRequestBody("image/*".toMediaTypeOrNull())
        val profileImageFileBody: MultipartBody.Part = MultipartBody.Part.createFormData("profile_image", profile_image.name + ".jpg", profileImageFile)

        val nicknameForm = nickname.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailForm = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameForm = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val callNumberForm = callNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderForm = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val birthForm = birth.toRequestBody("text/plain".toMediaTypeOrNull())
        val alarmForm = alarm.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val marketingForm = marketing.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return service.postUserInfo(user_id, nicknameForm, emailForm, nameForm, callNumberForm, genderForm, birthForm, alarmForm, marketingForm, profileImageFileBody, authorization)
    }
}