package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteBannerSource
import com.cashfulus.cashcarplus.data.remote.RemoteVersionSource
import com.cashfulus.cashcarplus.model.*
import com.google.gson.Gson
import retrofit2.Response
import java.util.concurrent.TimeoutException

interface BannerRepository {
    suspend fun bannerApi(): ApiResponse<BannerResponse>
}

class BannerRepositoryImpl(private val remoteBannerSource: RemoteBannerSource) : BannerRepository {
    override suspend fun bannerApi(): ApiResponse<BannerResponse> {
        val apiResult: Response<String> = remoteBannerSource.bannerApi()

        try {
            return if (apiResult.code() == 200) {
                ApiResponse(true, Gson().fromJson(apiResult.body()!!, BannerResponse::class.java), null)
            } else {
                ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/home/splash"))
            }
        } catch(e: TimeoutException) {
            return ApiResponse(false, null, makeErrorResponseFromMessage("서버에 접속할 수 없습니다.", "/home/splash"))
        }
    }
}