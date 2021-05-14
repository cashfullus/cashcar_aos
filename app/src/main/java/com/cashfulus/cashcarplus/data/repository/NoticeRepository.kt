package com.cashfulus.cashcarplus.data.repository

import com.google.gson.Gson
import com.cashfulus.cashcarplus.data.remote.RemoteNoticeSource
import com.cashfulus.cashcarplus.model.*
import retrofit2.Response

interface NoticeRepository {
    suspend fun getNoticeList(userId: Int, token: String) : ApiResponse<NoticeDataList>
}

class NoticeRepositoryImpl(private val remoteNoticeSource: RemoteNoticeSource) : NoticeRepository {
    override suspend fun getNoticeList(userId: Int, token: String): ApiResponse<NoticeDataList> {
        val apiResult: Response<String> = remoteNoticeSource.noticeApi(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, NoticeDataList::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/notice/list"))
        }
    }
}