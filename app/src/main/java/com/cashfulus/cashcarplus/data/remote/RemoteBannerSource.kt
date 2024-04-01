package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteBannerSource {
    suspend fun bannerApi(): Response<String>
}

class RemoteBannerSourceImpl(private val service: Api) : RemoteBannerSource {
    override suspend fun bannerApi() = service.getBanner()
}