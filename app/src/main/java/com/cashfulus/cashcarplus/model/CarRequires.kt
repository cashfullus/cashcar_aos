package com.cashfulus.cashcarplus.model

import com.google.gson.annotations.SerializedName

data class MyCarResponse(
    @SerializedName("brand") val brand: String,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("is_foreign_car") val isForeignCar: Int,
    @SerializedName("supporters") val supporters: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("vehicle_id") val vehicleId: Int,
    @SerializedName("vehicle_model_name") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("owner_relationship") val ownerRelationship: String,
    @SerializedName("is_delete") val isDelete: Boolean
)

class MyCarResponseList : ArrayList<MyCarResponse>()
data class MyCarResponseRef(
    @SerializedName("data") val data: MyCarResponseList,
    @SerializedName("status") val status: Boolean
)
data class MyCarInfoRef(
    @SerializedName("data") val data: MyCarResponse,
    @SerializedName("status") val status: Boolean
)

data class CarInfoRequest(
    @SerializedName("supporters") val supporters: Int,
    @SerializedName("is_foreign_car") val isForeignCar: Int,
    @SerializedName("brand") val brand: String,
    @SerializedName("vehicle_model_name") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("owner_relationship") val ownerRelationship: String
)

data class CarInfoChanged(
        @SerializedName("data") val data: CarInfoChangedData,
        @SerializedName("status") val status: Boolean
)
data class CarInfoChangedData(
        @SerializedName("double_check_number") val doubleCheckNumber: Boolean,
        @SerializedName("target_vehicle") val targetVehicle: Boolean
)

data class CarRegisterRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("supporters") val supporters: Int,
    @SerializedName("is_foreign_car") val isForeignCar: Int,
    @SerializedName("brand") val brand: String,
    @SerializedName("vehicle_model_name") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("owner_relationship") val ownerRelationship: String
)

data class CarRegisterResponse(
    @SerializedName("data") val data: CarRegisterResponseData,
    @SerializedName("status") val status: Boolean
)
data class CarRegisterResponseData(
    @SerializedName("double_check_number") val doubleCheckNumber: Boolean,
    @SerializedName("register") val register: Boolean,
    @SerializedName("user") val user: Boolean,
    @SerializedName("vehicle_information") val carInfo: MyCarResponseForRegister
)
data class MyCarResponseForRegister(
        @SerializedName("brand") val brand: String,
        @SerializedName("car_number") val carNumber: String,
        @SerializedName("is_foreign_car") val isForeignCar: Int,
        @SerializedName("supporters") val supporters: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("vehicle_model_name") val model: String,
        @SerializedName("year") val year: Int,
)

data class MyCarDeleteResponse(
        @SerializedName("data") val data: Boolean,
        @SerializedName("status") val status: Boolean
)