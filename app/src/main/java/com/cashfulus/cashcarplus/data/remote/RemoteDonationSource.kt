package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteDonationSource {
    suspend fun getDonationList(user_id: Int, authorization: String): Response<String>
    suspend fun getDonationList(count: Int, page: Int, user_id: Int, authorization: String): Response<String>
}

class RemoteDonationSourceImpl(private val service: Api) : RemoteDonationSource {
    override suspend fun getDonationList(user_id: Int, authorization: String) = service.getDonationList(user_id, authorization)
    override suspend fun getDonationList(count: Int, page: Int, user_id: Int, authorization: String) = service.getDonationList(count, page, user_id, authorization)
}