package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteFaqSource
import com.cashfulus.cashcarplus.model.ApiResponse
import com.cashfulus.cashcarplus.model.FaqResponseData
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.google.gson.Gson
import retrofit2.Response

interface FaqRepository {
    suspend fun getFaqApi() : ApiResponse<FaqResponseData>
}

class FaqRepositoryImpl(private val remoteFaqSource: RemoteFaqSource) : FaqRepository {
    override suspend fun getFaqApi(): ApiResponse<FaqResponseData> {
        val apiResult: Response<String> = remoteFaqSource.getFaqApi()

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, FaqResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/faq/list"))
        }
    }
}

