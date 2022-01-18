package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteDonationSource
import com.cashfulus.cashcarplus.model.*
import com.google.gson.Gson
import retrofit2.Response

interface DonationRepository {
    suspend fun getDonationList(userId: Int, token: String): ApiResponse<DonationListResponseData>
    suspend fun getDonationList(count: Int, page: Int, userId: Int, token: String): ApiResponse<DonationListResponseData>
    suspend fun registerDonation(point: Int, isReceipt: Boolean, name: String, organizationId: Int, userId: Int, token: String): ApiResponse<DonationResponse>
}

class DonationRepositoryImpl(private val remoteDonationSource: RemoteDonationSource): DonationRepository {
    override suspend fun getDonationList(userId: Int, token: String): ApiResponse<DonationListResponseData> {
        val apiResult: Response<String> = remoteDonationSource.getDonationList(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, DonationListResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/donate/list"))
        }
    }

    override suspend fun getDonationList(count: Int, page: Int, userId: Int, token: String): ApiResponse<DonationListResponseData> {
        val apiResult: Response<String> = remoteDonationSource.getDonationList(count, page, userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, DonationListResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/donate/list"))
        }
    }

    override suspend fun registerDonation(point: Int, isReceipt: Boolean, name: String, organizationId: Int, userId: Int, token: String): ApiResponse<DonationResponse> {
        val apiResult: Response<String> = remoteDonationSource.registerDonation(DonationRequest(point, if(isReceipt) 1 else 0, name), organizationId, userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, DonationResponse::class.java), null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/withdrawal/donate"))
        }
    }
}