package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemotePointSource
import com.cashfulus.cashcarplus.model.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Response

interface PointRepository {
    suspend fun getPointInfoAll(page: Int, userId: Int, token: String): ApiResponse<PointResponse>
    suspend fun getPointInfoCategory(category: String, page: Int, userId: Int, token: String): ApiResponse<PointResponse> // 'donate', 'positive', 'negative'
    suspend fun getBankInfo(userId: Int, token: String): ApiResponse<BankInfo>
    suspend fun registerWithdraw(name: String, accountBank: String, accountNumber: String, isMain: Int, point: Int, userId: Int, token: String): ApiResponse<WithdrawResponse>
}

class PointRepositoryImpl(private val remotePointSource: RemotePointSource) : PointRepository {
    override suspend fun getPointInfoAll(page: Int, userId: Int, token: String): ApiResponse<PointResponse> {
        val apiResult: Response<String> = remotePointSource.getPointInfoAll(page, userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            val result = Gson().fromJson(apiResult.body()!!, PointResponseData::class.java)
            return ApiResponse(true, result.data, null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/faq/list"))
        }
    }

    override suspend fun getPointInfoCategory(category: String, page: Int, userId: Int, token: String): ApiResponse<PointResponse> {
        val apiResult: Response<String> = remotePointSource.getPointInfoCategory(category, page, userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            val result = Gson().fromJson(apiResult.body()!!, PointResponseData::class.java)
            return ApiResponse(true, result.data, null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/faq/list"))
        }
    }

    override suspend fun getBankInfo(userId: Int, token: String): ApiResponse<BankInfo> {
        val apiResult: Response<String> = remotePointSource.getBankInfo(userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, BankInfo::class.java), null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/withdrawal/point"))
        }
    }

    override suspend fun registerWithdraw(name: String, accountBank: String, accountNumber: String, isMain: Int, point: Int, userId: Int, token: String): ApiResponse<WithdrawResponse> {
        val apiResult: Response<String> = remotePointSource.registerWithdraw(WithdrawRequest(name, accountBank, accountNumber, isMain, point), userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, WithdrawResponse::class.java), null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/faq/list"))
        }
    }
}