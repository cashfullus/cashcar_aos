package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.DataRequest
import retrofit2.Response

interface RemoteNoticeSource {
    suspend fun noticeApi(user_id: Int, authorization: String) : Response<String>
}

class RemoteNoticeSourceImpl(private val service: Api) : RemoteNoticeSource {
    override suspend fun noticeApi(user_id: Int, authorization: String) = service.getNotices(user_id, authorization)
}