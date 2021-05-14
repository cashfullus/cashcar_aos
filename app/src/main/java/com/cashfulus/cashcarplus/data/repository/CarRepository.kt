package com.cashfulus.cashcarplus.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.cashfulus.cashcarplus.data.remote.RemoteCarSource
import com.cashfulus.cashcarplus.model.*
import retrofit2.Response

interface CarRepository {
    suspend fun carList(userId: Int, token: String) : ApiResponse<MyCarResponseRef>

    suspend fun carInfo(userId: Int, vehicleId: Int, token: String) : ApiResponse<MyCarInfoRef>
    suspend fun modifyCarInfo(supporters: Int, isForeignCar: Int, brand: String, model: String, year: Int, carNumber: String, ownerRelationship: String, userId: Int, vehicleId: Int, token: String) : ApiResponse<CarInfoChanged>
    suspend fun deleteCarInfo(userId: Int, vehicleId: Int, token: String) : ApiResponse<MyCarDeleteResponse>

    suspend fun registerCarInfo(userId: Int, supporters: Int, isForeignCar: Int, brand: String, model: String, year: Int, carNumber: String, owner: String, token: String) : ApiResponse<CarRegisterResponse>
}

class CarRepositoryImpl(private val remoteCarSource: RemoteCarSource) : CarRepository {
    override suspend fun carList(userId: Int, token: String): ApiResponse<MyCarResponseRef> {
        val apiResult: Response<String> = remoteCarSource.getVehicleList(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, MyCarResponseRef::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/vehicle/list"))
        }
    }

    override suspend fun carInfo(userId: Int, vehicleId: Int, token: String): ApiResponse<MyCarInfoRef> {
        val apiResult: Response<String> = remoteCarSource.getVehicleInfo(userId, vehicleId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, MyCarInfoRef::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/vehicle/information"))
        }
    }

    override suspend fun modifyCarInfo(supporters: Int, isForeignCar: Int, brand: String, model: String, year: Int, carNumber: String, ownerRelationship: String, userId: Int, vehicleId: Int, token: String) : ApiResponse<CarInfoChanged> {
        val apiResult: Response<String> = remoteCarSource.postVehicleInfo(CarInfoRequest(supporters, isForeignCar, brand, model, year, carNumber, ownerRelationship), userId, vehicleId, "Bearer "+token)

        if (apiResult.code() == 200) {
            val result = Gson().fromJson(apiResult.body()!!, CarInfoChanged::class.java)
            if(result.data.doubleCheckNumber && result.data.targetVehicle)
                return ApiResponse(true, result, null)
            else if(!result.data.doubleCheckNumber)
                return ApiResponse(false, null, makeCustomErrorResponse(404, "이미 등록된 차량 번호입니다.", "/vehicle/information"))
            else
                return ApiResponse(false, null, makeCustomErrorResponse(404, "해당 차량이 존재하지 않습니다.", "/vehicle/information"))
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/vehicle/information"))
        }
    }

    override suspend fun deleteCarInfo(userId: Int, vehicleId: Int, token: String): ApiResponse<MyCarDeleteResponse> {
        val apiResult: Response<String> = remoteCarSource.deleteVehicle(userId, vehicleId, "Bearer "+token)

        if (apiResult.code() == 200) {
            val result = Gson().fromJson(apiResult.body()!!, MyCarDeleteResponse::class.java)
            if(result.status)
                return ApiResponse(true, result, null)
            else
                return ApiResponse(false, null, makeCustomErrorResponse(404, "오류가 발생했습니다. 다시 시도해 주세요.", "/vehicle/information"))
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/vehicle/information"))
        }
    }

    override suspend fun registerCarInfo(userId: Int, supporters: Int, isForeignCar: Int, brand: String, model: String, year: Int, carNumber: String, owner: String, token: String): ApiResponse<CarRegisterResponse> {
        val apiResult: Response<String> = remoteCarSource.registerVehicle(CarRegisterRequest(userId, supporters, isForeignCar, brand, model, year, carNumber, owner), "Bearer "+token)

        if (apiResult.code() == 201) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, CarRegisterResponse::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/vehicle/register"))
        }
    }
}