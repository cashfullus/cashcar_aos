package com.cashfulus.cashcarplus.data.service

import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.TestAuth1
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("login")
    suspend fun loginApi(@Body loginData: LoginRequest): Response<String>
    @POST("login")
    suspend fun loginPWApi(@Body loginData: LoginPWRequest): Response<String>

    @POST("register")
    suspend fun registerApi(@Body registerRequest: RegisterRequest): Response<String>
    @POST("register")
    suspend fun registerApiPW(@Body registerPWRequest: RegisterPWRequest): Response<String>

    @GET("user/profile")
    suspend fun getUserInfo(@Query("user_id") userId: Int, @Header("Authorization") authorization: String): Response<String>
    @Multipart
    @POST("user/profile")
    suspend fun postUserInfo(@Query("user_id") user_id: Int, @Part("nickname") nickname: RequestBody, @Part("email") email: RequestBody, @Part("name") name: RequestBody, @Part("call_number") callNumber: RequestBody, @Part("gender") gender: RequestBody, @Part("date_of_birth") birth: RequestBody, @Part("alarm") alarm: RequestBody, @Part("marketing") marketing: RequestBody, @Part profile_image: MultipartBody.Part, @Header("Authorization") authorization: String): Response<String>
    @Multipart
    @POST("user/profile")
    suspend fun postUserInfo(@Query("user_id") usepr_id: Int, @Part("nickname") nickname: RequestBody, @Part("email") email: RequestBody, @Part("name") name: RequestBody, @Part("call_number") callNumber: RequestBody, @Part("gender") gender: RequestBody, @Part("date_of_birth") birth: RequestBody, @Part("alarm") alarm: RequestBody, @Part("marketing") marketing: RequestBody, @Header("Authorization") authorization: String): Response<String>

    @GET("user/alarm/history")
    suspend fun getAlarmHistory(@Query("user_id") user_id: Int, @Query("page") page: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("user/set/address")
    suspend fun getUserAddress(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("user/set/address")
    suspend fun postUserAddress(@Query("user_id") user_id: Int, @Body userAddressRequest: UserAddress, @Header("Authorization") authorization: String): Response<String>

    @POST("user/fcm")
    suspend fun updateFcm(@Body userFcmUpdateRequest: UserFcmUpdateRequest): Response<String>

    @GET("vehicle/list")
    suspend fun getVehicleList(@Query("user_id") userId: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("vehicle/information")
    suspend fun getVehicleInfo(@Query("user_id") userId: Int, @Query("vehicle_id") vehicleId: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("vehicle/information")
    suspend fun postVehicleInfo(@Body carInfoRequest: CarInfoRequest, @Query("user_id") userId: Int, @Query("vehicle_id") vehicleId: Int, @Header("Authorization") authorization: String): Response<String>
    @DELETE("vehicle/information")
    suspend fun deleteVehicle(@Query("user_id") userId: Int, @Query("vehicle_id") vehicleId: Int, @Header("Authorization") authorization: String): Response<String>

    @POST("vehicle/register")
    suspend fun registerVehicle(@Body carRegisterRequest: CarRegisterRequest, @Header("Authorization") authorization: String): Response<String>

    @GET("ad/list")
    suspend fun getAdList(@Query("category") category: String, @Query("page") page: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("ad")
    suspend fun getAd(@Query("ad_id") ad_id: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("ad/apply")
    suspend fun applyAdGet(@Query("user_id") user_id: Int, @Query("ad_id") ad_id: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("ad/apply")
    suspend fun applyAd(@Body applyRequest: ApplyRequest, @Query("user_id") user_id: Int, @Query("ad_id") ad_id: Int, @Query("vehicle_id") vehicle_id: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("user/is-read")
    suspend fun popupRead(@Query("reason_id") reason_id: Int): Response<String>

    @GET("main/my-ad")
    suspend fun getMyMission(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @DELETE("main/my-ad")
    suspend fun deleteMyMission(@Query("ad_user_apply_id") ad_user_apply_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("ad/mission")
    suspend fun getMissions(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @Multipart
    @POST("ad/mission")
    suspend fun postMissions(@Part side_image: MultipartBody.Part, @Part back_image: MultipartBody.Part, @Part instrument_panel_image: MultipartBody.Part, @Part("travelled_distance") travelled_distance: RequestBody, @Query("ad_mission_card_user_id") mission_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @Multipart
    @POST("ad/mission")
    suspend fun postMissions(@Part side_image: MultipartBody.Part, @Part back_image: MultipartBody.Part, @Query("ad_mission_card_user_id") mission_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    /*@Multipart
    @POST("ad/mission")
    suspend fun postMissions(@Part side_image: MultipartBody.Part, @Part back_image: MultipartBody.Part, @Part instrument_panel_image: MultipartBody.Part, @Part("travelled_distance") travelled_distance: RequestBody, @Part("latitude") latitude: RequestBody, @Part("longitude") longitude: RequestBody, @Query("ad_mission_card_user_id") mission_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @Multipart
    @POST("ad/mission")
    suspend fun postMissions(@Part side_image: MultipartBody.Part, @Part back_image: MultipartBody.Part, @Part("latitude") latitude: RequestBody, @Part("longitude") longitude: RequestBody, @Query("ad_mission_card_user_id") mission_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>*/

    @POST("inquiry")
    suspend fun sendInquiry(@Body inquiry: InquiryRequest, @Header("Authorization") authorization: String): Response<String>

    @POST("login")
    suspend fun loginTmp(@Body loginInfo: TestAuth1): Response<String>

    @GET("notice/list")
    suspend fun getNotices(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("faq/list")
    suspend fun getFaqs(): Response<String>

    @GET("app/version?device=android")
    suspend fun getVersion(): Response<String>

    @GET("user/cash-car-tip/list")
    suspend fun getCashcarTipList(@Query("page") page: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("user/cash-car-tip")
    suspend fun getCashcarTipPost(@Query("tip_id") tip_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("/user/information/point")
    suspend fun getPointInfo(@Query("page") page: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("/user/information/point")
    suspend fun getPointInfo(@Query("q") q: String, @Query("page") page: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("/user/withdrawal/point")
    suspend fun getBankInfo(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("/user/withdrawal/point")
    suspend fun registerWithdraw(@Body withdrawRequest: WithdrawRequest, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @GET("user/donate/list")
    suspend fun getDonationList(@Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @GET("user/donate/list")
    suspend fun getDonationList(@Query("count") count: Int, @Query("page") page: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @POST("/user/withdrawal/donate")
    suspend fun registerDonation(@Body donationRequest: DonationRequest, @Query("donation_organization_id") donation_organization_id: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>

    @POST("user/alarm") //1=True, 0=False
    suspend fun postUserAlarm(@Query("is_on") is_on: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>
    @POST("user/marketing") //1=True, 0=False
    suspend fun postMarketingAlarm(@Query("is_on") is_on: Int, @Query("user_id") user_id: Int, @Header("Authorization") authorization: String): Response<String>


}

// API 접속 과정에서 오류가 발생한 경우 Status Code. (즉 인터넷 연결이 끊기거나, 서버가 죽은 경우 등의 이유로 인해, 서버로부터 응답도 받지 못한 상태로 발생한 오류.)
const val API_CONNECT_ERROR_CODE = 999
// 인터넷 없음 오류
const val NO_INTERNET_ERROR_CODE = 1395
// 버전 오류
const val VERSION_ERROR_CODE = -1
// 유저 정보 유실
const val LOST_USER_INFO_ERROR_CODE = 93015