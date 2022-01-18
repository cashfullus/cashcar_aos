package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.WithdrawRequest
import retrofit2.Response

interface RemotePointSource {
    suspend fun getPointInfoAll(page: Int, user_id: Int, authorization: String): Response<String>
    suspend fun getPointInfoCategory(category: String, page: Int, user_id: Int, authorization: String): Response<String> // 'donate', 'positive', 'negative'
    suspend fun getBankInfo(user_id: Int, authorization: String): Response<String>
    suspend fun registerWithdraw(withdrawRequest: WithdrawRequest, user_id: Int, authorization: String): Response<String>
}

class RemotePointSourceImpl(private val service: Api) : RemotePointSource {
    override suspend fun getPointInfoAll(page: Int, user_id: Int, authorization: String) = service.getPointInfo(page, user_id, authorization)
    override suspend fun getPointInfoCategory(category: String, page: Int, user_id: Int, authorization: String) = service.getPointInfo(category, page, user_id, authorization)
    override suspend fun getBankInfo(user_id: Int, authorization: String) = service.getBankInfo(user_id, authorization)
    override suspend fun registerWithdraw(withdrawRequest: WithdrawRequest, user_id: Int, authorization: String) = service.registerWithdraw(withdrawRequest, user_id, authorization)
}