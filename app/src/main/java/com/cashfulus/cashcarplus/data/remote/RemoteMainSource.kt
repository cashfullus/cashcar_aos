package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.DataRequest
import retrofit2.Response

interface RemoteMainSource {

}

class RemoteMainSourceImpl(private val service: Api) : RemoteMainSource {
}