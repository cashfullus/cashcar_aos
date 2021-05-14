package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteAlarmSource
import com.cashfulus.cashcarplus.model.AlarmResponseData
import com.cashfulus.cashcarplus.model.ApiResponse
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.google.gson.Gson
import retrofit2.Response

interface AlarmRepository {
    suspend fun getAlarmApi(userId: Int, page: Int, token: String): ApiResponse<AlarmResponseData>
}

class AlarmRepositoryImpl(private val remoteAlarmSource: RemoteAlarmSource): AlarmRepository {
    override suspend fun getAlarmApi(userId: Int, page: Int, token: String): ApiResponse<AlarmResponseData> {
        val apiResult: Response<String> = remoteAlarmSource.getAlarmApi(userId, page, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, AlarmResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/alarm/history"))
        }
    }
}