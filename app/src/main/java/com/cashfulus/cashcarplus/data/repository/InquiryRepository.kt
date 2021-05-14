package com.cashfulus.cashcarplus.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.cashfulus.cashcarplus.data.remote.RemoteInquirySource
import com.cashfulus.cashcarplus.model.*
import retrofit2.Response

interface InquiryRepository {
    suspend fun sendInquiry(userId: Int, company: String, staffName: String, phone: String, email: String, contents: String, token: String) : ApiResponse<Void>
}

class InquiryRepositoryImpl(private val remoteInquirySource: RemoteInquirySource) : InquiryRepository {
    override suspend fun sendInquiry(userId: Int, company: String, staffName: String, phone: String, email: String, contents: String, token: String): ApiResponse<Void> {
        val apiResult: Response<String> = remoteInquirySource.sendInquiry(InquiryRequest(userId, company, staffName, phone, email, contents), "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, null, null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/postapi"))
        }
    }
}