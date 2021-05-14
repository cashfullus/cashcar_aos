package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.InquiryRequest
import retrofit2.Response

interface RemoteInquirySource {
    suspend fun sendInquiry(inquiry: InquiryRequest, authorization: String): Response<String>
}

class RemoteInquirySourceImpl(private val service: Api) : RemoteInquirySource {
    override suspend fun sendInquiry(inquiry: InquiryRequest, authorization: String) = service.sendInquiry(inquiry, authorization)
}