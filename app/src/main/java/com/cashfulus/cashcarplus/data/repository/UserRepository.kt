package com.cashfulus.cashcarplus.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.data.remote.RemoteUserSource
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.TestAuth1
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException

interface UserRepository {
    suspend fun login(email: String, loginType: String) : ApiResponse<LoginResponse>
    suspend fun loginPW(email: String, password: String, loginType: String) : ApiResponse<LoginResponse>
    suspend fun register(fcmToken: String, email: String, alarm: Boolean, marketing: Boolean, loginType: String) : ApiResponse<RegisterResponse>
    suspend fun registerPW(fcmToken: String, email: String, password: String, alarm: Boolean, marketing: Boolean, loginType: String) : ApiResponse<RegisterResponse>
    suspend fun getUserInfo(userId: Int, token: String) : ApiResponse<UserInfoResponse>
    suspend fun updateUserInfo(token: String, userId: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, imgUri: Uri): ApiResponse<UserUpdateResponseWithImg>
    suspend fun updateUserInfo(token: String, userId: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int): ApiResponse<UserUpdateResponse>
    suspend fun getUserAddress(userId: Int, token: String): ApiResponse<UserAddress>
    suspend fun postUserAddress(userId: Int, callNumber: String, detailAddress: String, mainAddress: String, name: String, token: String): ApiResponse<UserAddressUpdated>
    suspend fun updateFcm(userId: Int, fcmToken: String) : ApiResponse<Void>

    suspend fun loginTmp(): ApiResponse<LoginResponse>
}

class UserRepositoryImpl(private val remoteUserSource: RemoteUserSource) : UserRepository {
    override suspend fun login(email: String, loginType: String): ApiResponse<LoginResponse> {
        val apiResult: Response<String> = remoteUserSource.loginApi(LoginRequest(email, loginType))

        // 200 : 로그인 성공 (카카오 로그인 시도시 이메일이 존재할시 바로 패스)
        // 404 : 해당 계정이 없음
        // 409 : 이메일 또는 핸드폰번호 중복
        try {
            return if (apiResult.code() == 200) {
                ApiResponse(true, Gson().fromJson(apiResult.body()!!, LoginResponse::class.java), null)
            } else if(apiResult.code() == 404) { // 404 : 해당 계정이 없음
                ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "해당하는 계정이 존재하지 않습니다.", "/login"))
            } else {
                ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/login"))
            }
        } catch(e: TimeoutException) {
            return ApiResponse(false, null, makeErrorResponseFromMessage("서버에 접속할 수 없습니다.", "/login"))
        }
    }

    override suspend fun loginPW(email: String, password: String, loginType: String): ApiResponse<LoginResponse> {
        val apiResult: Response<String> = remoteUserSource.loginPWApi(LoginPWRequest(email, password, loginType))

        // 200 : 로그인 성공 (카카오 로그인 시도시 이메일이 존재할시 바로 패스)
        // 404 : 해당 계정이 없음
        // 409 : 이메일 또는 핸드폰번호 중복
        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, LoginResponse::class.java), null)
        } else if(apiResult.code() == 404) { // 404 : 해당 계정이 없음
            ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "해당하는 계정이 존재하지 않습니다.", "/login"))
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/login"))
        }
    }

    override suspend fun register(fcmToken: String, email: String, alarm: Boolean, marketing: Boolean, loginType: String): ApiResponse<RegisterResponse> {
        val alarmInt = if(alarm) 1 else 0
        val marketingInt = if(marketing) 1 else 0
        val apiResult: Response<String> = remoteUserSource.registerApi(RegisterRequest(fcmToken, email, alarmInt, marketingInt, loginType))

        if (apiResult.code() == 201) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, RegisterResponse::class.java), null)
        } else if(apiResult.code() == 409) { // 중복된 회원가입 시도(이메일 중복확인)
            return ApiResponse(false, null, makeCustomErrorResponse(409, "같은 이메일로 가입된 계정이 존재합니다.", "/register"))
        } else if(apiResult.code() == 400) { // 데이터 형식 실패
            return ApiResponse(false, null, makeCustomErrorResponse(400, "데이터 형식이 잘못되었습니다.", "/register"))
        }  else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/register"))
        }
    }

    override suspend fun registerPW(fcmToken: String, email: String, password: String, alarm: Boolean, marketing: Boolean, loginType: String): ApiResponse<RegisterResponse> {
        val alarmInt = if(alarm) 1 else 0
        val marketingInt = if(marketing) 1 else 0
        val apiResult: Response<String> = remoteUserSource.registerPWApi(RegisterPWRequest(fcmToken, email, password, alarmInt, marketingInt, loginType))

        if (apiResult.code() == 201) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, RegisterResponse::class.java), null)
        } else if(apiResult.code() == 409) { // 중복된 회원가입 시도(이메일 중복확인)
            return ApiResponse(false, null, makeCustomErrorResponse(409, "같은 이메일로 가입된 계정이 존재합니다.", "/register"))
        } else if(apiResult.code() == 400) { // 데이터 형식 실패
            return ApiResponse(false, null, makeCustomErrorResponse(400, "데이터 형식이 잘못되었습니다.", "/register"))
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/register"))
        }
    }

    override suspend fun getUserInfo(userId: Int, token: String): ApiResponse<UserInfoResponse> {
        val apiResult: Response<String> = remoteUserSource.getUserInfo(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, UserInfoResponse::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/profile"))
        }
    }

    override suspend fun updateUserInfo(token: String, userId: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int, imgUri: Uri): ApiResponse<UserUpdateResponseWithImg> {
        val apiResult: Response<String> = remoteUserSource.postUserInfo(userId, nickname, email, name, callNumber, gender, birth, alarm, marketing, File(imgUri.path!!), "Bearer "+token)

        if(apiResult.code() == 201) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, UserUpdateResponseWithImg::class.java), null)
        } else if(apiResult.code() == 404) {// 사용자 정보가 존재하지 않음
            return ApiResponse(false, null, makeCustomErrorResponse(404, "해당 사용자 정보가 존재하지 않습니다.", "/user/profile"))
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/profile"))
        }
    }

    override suspend fun updateUserInfo(token: String, userId: Int, nickname: String, email: String, name: String, callNumber: String, gender: String, birth: String, alarm: Int, marketing: Int): ApiResponse<UserUpdateResponse> {
        val apiResult: Response<String> = remoteUserSource.postUserInfo(userId, nickname, email, name, callNumber, gender, birth, alarm, marketing, "Bearer "+token)

        if(apiResult.code() == 201) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, UserUpdateResponse::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/profile"))
        }
    }

    override suspend fun getUserAddress(userId: Int, token: String): ApiResponse<UserAddress> {
        val apiResult: Response<String> = remoteUserSource.getUserAddress(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            val result = Gson().fromJson(apiResult.body()!!, UserAddressResponseData::class.java)
            return ApiResponse(true, result.data, null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/set/address"))
        }
    }

    override suspend fun postUserAddress(userId: Int, callNumber: String, detailAddress: String, mainAddress: String, name: String, token: String): ApiResponse<UserAddressUpdated> {
        val apiResult: Response<String> = remoteUserSource.postUserAddress(userId, UserAddress(callNumber, detailAddress, mainAddress, name), "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, UserAddressUpdated::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/set/address"))
        }
    }

    override suspend fun updateFcm(userId: Int, fcmToken: String): ApiResponse<Void> {
        val apiResult: Response<String> = remoteUserSource.updateFcm(UserFcmUpdateRequest(userId, fcmToken))

        return if (apiResult.code() == 200) {
            ApiResponse(true, null, null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/fcm"))
        }
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(
            App().context().cacheDir, "FILE_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+"_")
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    override suspend fun loginTmp(): ApiResponse<LoginResponse> {
        val apiResult: Response<String> = remoteUserSource.loginTest(TestAuth1("katarina@tekken.com", "garen", "normal")) //TestAuth1()

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, LoginResponse::class.java), null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/fcm"))
        }
    }
}