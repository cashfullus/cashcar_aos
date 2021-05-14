package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteFaqSource {
    suspend fun getFaqApi(): Response<String>
}

class RemoteFaqSourceImpl(private val service: Api) : RemoteFaqSource {
    override suspend fun getFaqApi() = service.getFaqs()
}