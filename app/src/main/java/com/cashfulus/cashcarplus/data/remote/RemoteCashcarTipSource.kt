package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteCashcarTipSource {
    suspend fun getCashcarTipList(page: Int, user_id: Int, authorization: String): Response<String>
    suspend fun getCashcarTipPost(tip_id: Int, user_id: Int, authorization: String): Response<String>
}

class RemoteCashcarTipSourceImpl(private val service: Api) : RemoteCashcarTipSource {
    override suspend fun getCashcarTipList(page: Int, user_id: Int, authorization: String) = service.getCashcarTipList(page, user_id, authorization)
    override suspend fun getCashcarTipPost(tip_id: Int, user_id: Int, authorization: String) = service.getCashcarTipPost(tip_id, user_id, authorization)
}