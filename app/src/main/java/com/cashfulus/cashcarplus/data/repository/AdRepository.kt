package com.cashfulus.cashcarplus.data.repository

import com.google.gson.Gson
import com.cashfulus.cashcarplus.data.remote.RemoteMissionSource
import com.cashfulus.cashcarplus.model.*
import retrofit2.Response

interface AdRepository {
    suspend fun getAdList(category: String, page: Int, token: String) : ApiResponse<ArrayList<MissionResponse>>
    suspend fun getAd(adId: Int, token: String): ApiResponse<MissionResponse>
}

class AdRepositoryImpl(private val remoteMissionSource: RemoteMissionSource) : AdRepository {
    override suspend fun getAdList(category: String, page: Int, token: String): ApiResponse<ArrayList<MissionResponse>> {
        val apiResult: Response<String> = remoteMissionSource.getAdList(category, page, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, MissionResponseList::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/list"))
        }
    }

    override suspend fun getAd(adId: Int, token: String): ApiResponse<MissionResponse> {
        val apiResult: Response<String> = remoteMissionSource.getAd(adId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, MissionResponse::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad"))
        }
    }
}