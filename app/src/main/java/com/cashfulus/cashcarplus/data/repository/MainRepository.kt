package com.cashfulus.cashcarplus.data.repository

import com.google.gson.Gson
import com.cashfulus.cashcarplus.data.remote.RemoteMainSource
import com.cashfulus.cashcarplus.model.ApiResponse
import com.cashfulus.cashcarplus.model.DataRequest
import com.cashfulus.cashcarplus.model.DataResponseList
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import retrofit2.Response

interface MainRepository {
    //suspend fun getDataList(data: DataRequest) : ApiResponse<DataResponseList>
}

class MainRepositoryImpl(private val remoteMainSource: RemoteMainSource) : MainRepository {
    /*override suspend fun getDataList(data: DataRequest): ApiResponse<DataResponseList> {
        val apiResult: Response<String> = remoteMainSource.postApi(data)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, DataResponseList::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/postapi"))
        }
    }*/
}