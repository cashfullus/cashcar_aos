package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.CarInfoRequest
import com.cashfulus.cashcarplus.model.CarRegisterRequest
import retrofit2.Response

interface RemoteCarSource {
    suspend fun getVehicleList(userId: Int, authorization: String): Response<String>

    suspend fun getVehicleInfo(userId: Int, vehicleId: Int, authorization: String): Response<String>
    suspend fun postVehicleInfo(carInfoRequest: CarInfoRequest, userId: Int, vehicleId: Int, authorization: String): Response<String>
    suspend fun deleteVehicle(userId: Int, vehicleId: Int, authorization: String): Response<String>

    suspend fun registerVehicle(carRegisterRequest: CarRegisterRequest, authorization: String): Response<String>
}

class RemoteCarSourceImpl(private val service: Api) : RemoteCarSource {
    override suspend fun getVehicleList(userId: Int, authorization: String) = service.getVehicleList(userId, authorization)

    override suspend fun getVehicleInfo(userId: Int, vehicleId: Int, authorization: String) = service.getVehicleInfo(userId, vehicleId, authorization)
    override suspend fun postVehicleInfo(carInfoRequest: CarInfoRequest, userId: Int, vehicleId: Int, authorization: String) = service.postVehicleInfo(carInfoRequest, userId, vehicleId, authorization)
    override suspend fun deleteVehicle(userId: Int, vehicleId: Int, authorization: String) = service.deleteVehicle(userId, vehicleId, authorization)

    override suspend fun registerVehicle(carRegisterRequest: CarRegisterRequest, authorization: String) = service.registerVehicle(carRegisterRequest, authorization)
}