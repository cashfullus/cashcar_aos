package com.cashfulus.cashcarplus.data.repository

import com.cashfulus.cashcarplus.data.remote.RemoteCashcarTipSource
import com.cashfulus.cashcarplus.model.*
import com.google.gson.Gson
import retrofit2.Response

interface CashcarTipRepository {
    suspend fun getCashcarTipList(page: Int, userId: Int, token: String): ApiResponse<CashcartipList>
    suspend fun getCashcarTipPost(tipId: Int, userId: Int, token: String): ApiResponse<CashcartipPost>
}

class CashcarTipRepositoryImpl(private val remoteCashcarTipSource: RemoteCashcarTipSource) : CashcarTipRepository {
    /** 캐시카팁 리스트 아이템 10개 단위 */
    override suspend fun getCashcarTipList(page: Int, userId: Int, token: String): ApiResponse<CashcartipList> {
        val apiResult: Response<String> = remoteCashcarTipSource.getCashcarTipList(page, userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, CashcartipList::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/cash-car-tip/list"))
        }
    }

    override suspend fun getCashcarTipPost(tipId: Int, userId: Int, token: String): ApiResponse<CashcartipPost> {
        val apiResult: Response<String> = remoteCashcarTipSource.getCashcarTipPost(tipId, userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, CashcartipPost::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/cash-car-tip"))
        }
    }
}