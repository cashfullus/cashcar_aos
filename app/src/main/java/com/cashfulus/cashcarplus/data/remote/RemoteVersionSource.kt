package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import retrofit2.Response

interface RemoteVersionSource {
    suspend fun versionApi(): Response<String>
}

class RemoteVersionSourceImpl(private val service: Api) : RemoteVersionSource {
    override suspend fun versionApi() = service.getVersion()
}