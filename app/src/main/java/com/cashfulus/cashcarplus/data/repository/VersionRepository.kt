package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteVersionSource
import com.cashfulus.cashcarplus.model.*
import com.google.gson.Gson
import retrofit2.Response
import java.util.concurrent.TimeoutException

interface VersionRepository {
    suspend fun versionApi(): ApiResponse<VersionResponse>
}

class VersionRepositoryImpl(private val remoteVersionSource: RemoteVersionSource) : VersionRepository {
    override suspend fun versionApi(): ApiResponse<VersionResponse> {
        val apiResult: Response<String> = remoteVersionSource.versionApi()

        try {
            return if (apiResult.code() == 200) {
                ApiResponse(true, Gson().fromJson(apiResult.body()!!, VersionResponse::class.java), null)
            } else {
                ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/version"))
            }
        } catch(e: TimeoutException) {
            return ApiResponse(false, null, makeErrorResponseFromMessage("서버에 접속할 수 없습니다.", "/version"))
        }
    }
}