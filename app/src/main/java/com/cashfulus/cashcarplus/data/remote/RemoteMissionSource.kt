package com.cashfulus.cashcarplus.data.remote

import com.cashfulus.cashcarplus.data.service.Api
import com.cashfulus.cashcarplus.model.ApplyRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File


interface RemoteMissionSource {
    suspend fun getAdList(category: String, page: Int, authorization: String): Response<String>
    suspend fun getAd(ad_id: Int, authorization: String): Response<String>
    suspend fun getMyMission(user_id: Int, authorization: String): Response<String>
    suspend fun deleteMyMission(ad_user_apply_id: Int, user_id: Int, authorization: String): Response<String>
    suspend fun applyAd(applyRequest: ApplyRequest, userId: Int, adId: Int, vehicle_id: Int, authorization: String): Response<String>
    suspend fun applyAdGet(userId: Int, adId: Int, authorization: String): Response<String>
    suspend fun getMissions(user_id: Int, authorization: String): Response<String>
    suspend fun postMission(side_image: File, back_image: File, instrument_panel_image: File, travelled_distance: String, mission_id: Int, user_id: Int, authorization: String): Response<String>
    suspend fun postMission(side_image: File, back_image: File, mission_id: Int, user_id: Int, authorization: String): Response<String>
    //suspend fun postMission(side_image: File, back_image: File, instrument_panel_image: File, travelled_distance: String, latitude: String, longitude: String, mission_id: Int, user_id: Int, authorization: String): Response<String>
    //suspend fun postMission(side_image: File, back_image: File, latitude: String, longitude: String, mission_id: Int, user_id: Int, authorization: String): Response<String>

    suspend fun popupRead(reason_id: Int): Response<String>
}

class RemoteMissionSourceImpl(private val service: Api) : RemoteMissionSource {
    override suspend fun getAdList(category: String, page: Int, authorization: String) = service.getAdList(category, page, authorization)
    override suspend fun getAd(ad_id: Int, authorization: String) = service.getAd(ad_id, authorization)
    override suspend fun getMyMission(user_id: Int, authorization: String) = service.getMyMission(user_id, authorization)
    override suspend fun deleteMyMission(ad_user_apply_id: Int, user_id: Int, authorization: String) = service.deleteMyMission(ad_user_apply_id, user_id, authorization)
    override suspend fun applyAd(applyRequest: ApplyRequest, userId: Int, adId: Int, vehicle_id: Int, authorization: String) = service.applyAd(applyRequest, userId, adId, vehicle_id, authorization)
    override suspend fun applyAdGet(userId: Int, adId: Int, authorization: String) = service.applyAdGet(userId, adId, authorization)
    override suspend fun getMissions(user_id: Int, authorization: String) = service.getMissions(user_id, authorization)
    override suspend fun popupRead(reason_id: Int) = service.popupRead(reason_id)

    override suspend fun postMission(side_image: File, back_image: File, instrument_panel_image: File, travelled_distance: String, mission_id: Int, user_id: Int, authorization: String): Response<String> {
        val sideImgFile = side_image.asRequestBody("image/*".toMediaTypeOrNull())
        val sideImgFileBody: MultipartBody.Part = createFormData("side_image", side_image.name+".jpg", sideImgFile)

        val backImgFile = back_image.asRequestBody("image/*".toMediaTypeOrNull())
        val backImgFileBody: MultipartBody.Part = createFormData("back_image", back_image.name+".jpg", backImgFile)

        val panelImgFile = instrument_panel_image.asRequestBody("image/*".toMediaTypeOrNull())
        val panelImgFileBody: MultipartBody.Part = createFormData("instrument_panel_image", instrument_panel_image.name+".jpg", panelImgFile)

        val distanceStr = travelled_distance.toRequestBody("text/plain".toMediaTypeOrNull())

        return service.postMissions(sideImgFileBody, backImgFileBody, panelImgFileBody, distanceStr, mission_id, user_id, authorization)
    }

    override suspend fun postMission(side_image: File, back_image: File, mission_id: Int, user_id: Int, authorization: String): Response<String> {
        val sideImgFile = side_image.asRequestBody("image/*".toMediaTypeOrNull())
        val sideImgFileBody: MultipartBody.Part = createFormData("side_image", side_image.name+".jpg", sideImgFile)

        val backImgFile = back_image.asRequestBody("image/*".toMediaTypeOrNull())
        val backImgFileBody: MultipartBody.Part = createFormData("back_image", back_image.name+".jpg", backImgFile)

        return service.postMissions(sideImgFileBody, backImgFileBody, mission_id, user_id, authorization)
    }
}
    /*override suspend fun postMission(side_image: File, back_image: File, instrument_panel_image: File, latitude: String, longitude: String, travelled_distance: String, mission_id: Int, user_id: Int, authorization: String): Response<String> {
        val sideImgFile = side_image.asRequestBody("image/*".toMediaTypeOrNull())
        val sideImgFileBody: MultipartBody.Part = createFormData("side_image", side_image.name+".jpg", sideImgFile)

        val backImgFile = back_image.asRequestBody("image/*".toMediaTypeOrNull())
        val backImgFileBody: MultipartBody.Part = createFormData("back_image", back_image.name+".jpg", backImgFile)

        val panelImgFile = instrument_panel_image.asRequestBody("image/*".toMediaTypeOrNull())
        val panelImgFileBody: MultipartBody.Part = createFormData("instrument_panel_image", instrument_panel_image.name+".jpg", panelImgFile)

        val distanceStr = travelled_distance.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudeStr = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudeStr = longitude.toRequestBody("text/plain".toMediaTypeOrNull())

        return service.postMissions(sideImgFileBody, backImgFileBody, panelImgFileBody, distanceStr, latitudeStr, longitudeStr, mission_id, user_id, authorization)
    }

    override suspend fun postMission(side_image: File, back_image: File, latitude: String, longitude: String, mission_id: Int, user_id: Int, authorization: String): Response<String> {
        val sideImgFile = side_image.asRequestBody("image/*".toMediaTypeOrNull())
        val sideImgFileBody: MultipartBody.Part = createFormData("side_image", side_image.name+".jpg", sideImgFile)

        val backImgFile = back_image.asRequestBody("image/*".toMediaTypeOrNull())
        val backImgFileBody: MultipartBody.Part = createFormData("back_image", back_image.name+".jpg", backImgFile)

        val latitudeStr = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudeStr = longitude.toRequestBody("text/plain".toMediaTypeOrNull())

        return service.postMissions(sideImgFileBody, backImgFileBody, latitudeStr, longitudeStr, mission_id, user_id, authorization)
    }*/