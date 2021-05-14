package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteAlarmSource {
    suspend fun getAlarmApi(user_id: Int, page: Int, authorization: String): Response<String>
}

class RemoteAlarmSourceImpl(private val service: Api) : RemoteAlarmSource {
    override suspend fun getAlarmApi(user_id: Int, page: Int, authorization: String) = service.getAlarmHistory(user_id, page, authorization)
}