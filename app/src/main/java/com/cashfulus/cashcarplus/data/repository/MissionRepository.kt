package com.cashfulus.cashcarplus.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.kakao.sdk.common.util.KakaoJson.fromJson
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.data.remote.RemoteMissionSource
import com.cashfulus.cashcarplus.model.*
import com.kakao.ad.common.json.SignUp
import com.kakao.ad.tracker.send
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
main/my_ad 에서 apply_status 가 stand_by 일 경우 mission_status 는 ""
apply_status 가 accept 일 경우 stand_by(미션 대기),
review(제출후 승인대기), reject(미션 실패), re_review(재 승인), success(성공), fail(실패) 이렇게 되겠습니다!
 * */

// apply_status 상수
const val APPLY_STATUS_STAND_BY = "stand_by"
const val APPLY_STATUS_ACCEPT = "accept"
// mission_status 상수
const val MISSION_STATUS_NONE = ""
const val MISSION_STATUS_ON_GOING = "ongoing"
const val MISSION_STATUS_STAND_BY = "stand_by"
const val MISSION_STATUS_REVIEW = "review"
const val MISSION_STATUS_REJECT = "reject"
const val MISSION_STATUS_REAUTH = "re_review"
const val MISSION_STATUS_SUCCESS = "success"
const val MISSION_STATUS_FAIL = "fail"

interface MissionRepository {
    suspend fun getMyMission(userId: Int, token: String): ApiResponse<MyAdResponse>
    suspend fun deleteMyMission(missionId: Int, userId: Int, token: String): ApiResponse<Void>
    suspend fun getAdList(category: String, page: Int, token: String): ApiResponse<AdResponseData>
    suspend fun getAd(adId: Int, token: String): ApiResponse<AdInfoResponseData>
    suspend fun getAd(adId: Int, token: String, version: String): ApiResponse<AdInfoResponseData>
    suspend fun applyAd(mainAddress: String, detailAddress: String, callNumber: String, name: String, userId: Int, adId: Int, vehicleId: Int, token: String): ApiResponse<ApplyResponseData>
    suspend fun applyAdGet(userId: Int, adId: Int, token: String): ApiResponse<AdApplyInfoResponseData>
    suspend fun getMissions(userId: Int, token: String): ApiResponse<AdMissionResponse>
    suspend fun postMission(sideImg: Bitmap, backImg: Bitmap, gaugeImg: Bitmap, gaugeKm: String, missionId: Int, userId: Int, token: String): ApiResponse<AdMissionPost>
    suspend fun postMission(sideImg: Bitmap, backImg: Bitmap, missionId: Int, userId: Int, token: String): ApiResponse<AdMissionPost>

    suspend fun popupRead(reasonId: Int): ApiResponse<Void>
}

class MissionRepositoryImpl(private val remoteMissionSource: RemoteMissionSource) : MissionRepository {
    override suspend fun getMyMission(userId: Int, token: String): ApiResponse<MyAdResponse> {
        val apiResult: Response<String> = remoteMissionSource.getMyMission(userId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, MyAdResponse::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/main/my-ad"))
        }
    }

    override suspend fun deleteMyMission(missionId: Int, userId: Int, token: String): ApiResponse<Void> {
        val apiResult: Response<String> = remoteMissionSource.deleteMyMission(missionId, userId, "Bearer "+token)

        return when(apiResult.code()) {
            200 -> ApiResponse(true, null, null)
            403 -> ApiResponse(false, null, makeCustomErrorResponse(403, "신청한 지 1시간 이상 경과한 미션은 취소가 불가능합니다.", "/main/my-ad")) // 시간 초과
            404 -> ApiResponse(false, null, makeCustomErrorResponse(404, "이미 취소한 미션입니다.", "/main/my-ad")) // 이미 취소된 정보
            else -> ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/main/my-ad"))
        }
    }

    override suspend fun getAdList(category: String, page: Int, token: String): ApiResponse<AdResponseData> {
        val apiResult: Response<String> = remoteMissionSource.getAdList(category, page, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/list"))
        }
    }

    override suspend fun getAd(adId: Int, token: String): ApiResponse<AdInfoResponseData> {
        val apiResult: Response<String> = remoteMissionSource.getAd(adId, "Bearer "+token)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdInfoResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad"))
        }
    }

    override suspend fun getAd(adId: Int, token: String, version: String): ApiResponse<AdInfoResponseData> {
        val apiResult: Response<String> = remoteMissionSource.getAd(adId, "Bearer "+token, version)

        if (apiResult.code() == 200) {
            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdInfoResponseData::class.java), null)
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad"))
        }
    }

    override suspend fun applyAd(mainAddress: String, detailAddress: String, callNumber: String, name: String, userId: Int, adId: Int, vehicleId: Int, token: String): ApiResponse<ApplyResponseData> {
        val apiResult: Response<String> = remoteMissionSource.applyAd(ApplyRequest(mainAddress, detailAddress, callNumber, name), userId, adId, vehicleId,"Bearer "+token)

        if (apiResult.code() == 200) {
            // 광고신청 이벤트 전송
            val event = SignUp()
            event.tag = "SignUp"
            event.send()

            return ApiResponse(true, Gson().fromJson(apiResult.body()!!, ApplyResponseData::class.java), null)
        } else if (apiResult.code() == 404) {
            val result = Gson().fromJson(apiResult.errorBody()!!.string(), ApplyResponseData::class.java)

            if(!result.data.area) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "입력해주신 주소는 브랜드가 요청한 지역에 해당되지 않기 때문에 신청할 수 없습니다.", "/ad/apply"))
            } else if(!result.data.vehicle) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "차량 정보가 없습니다. 다시 시도해 주세요.", "/ad/apply"))
            } else if(!result.data.adInformation) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "잘못된 서포터즈 정보입니다.", "/ad/apply"))
            } else if(!result.data.alreadyApply) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "신청했던 광고는 재신청 할 수 없습니다.", "/ad/apply"))
            } else if(!result.data.userInformation) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "유저 정보가 잘못되었습니다. 입력하지 않은 곳이 있는지 확인해주세요.", "/ad/apply"))
            } else if(!result.data.rejectApply) {
                return ApiResponse(false, null, makeCustomErrorResponse(apiResult.code(), "조건이 맞지 않아서 거절된 광고입니다.", "/ad/apply"))
            } else {
                return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/apply"))
            }
        } else {
            return ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/apply"))
        }
    }

    override suspend fun applyAdGet(userId: Int, adId: Int, token: String): ApiResponse<AdApplyInfoResponseData> {
        val apiResult: Response<String> = remoteMissionSource.applyAdGet(userId, adId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdApplyInfoResponseData::class.java), null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/apply"))
        }
    }

    override suspend fun getMissions(userId: Int, token: String): ApiResponse<AdMissionResponse> {
        val apiResult: Response<String> = remoteMissionSource.getMissions(userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdMissionResponseData::class.java).data, null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/mission"))
        }
    }

    override suspend fun postMission(sideImg: Bitmap, backImg: Bitmap, gaugeImg: Bitmap, gaugeKm: String, missionId: Int, userId: Int, token: String): ApiResponse<AdMissionPost> {
        val apiResult: Response<String> = remoteMissionSource.postMission(convertBitmapToFile(sideImg, "1"), convertBitmapToFile(backImg, "2"), convertBitmapToFile(gaugeImg, "3"), gaugeKm, missionId, userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdMissionPostData::class.java).data, null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/mission"))
        }
    }

    override suspend fun postMission(sideImg: Bitmap, backImg: Bitmap, missionId: Int, userId: Int, token: String): ApiResponse<AdMissionPost> {
        val apiResult: Response<String> = remoteMissionSource.postMission(convertBitmapToFile(sideImg, "1"), convertBitmapToFile(backImg, "2"), missionId, userId, "Bearer "+token)

        return if (apiResult.code() == 200) {
            ApiResponse(true, Gson().fromJson(apiResult.body()!!, AdMissionPostData::class.java).data, null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/ad/mission"))
        }
    }

    override suspend fun popupRead(reasonId: Int): ApiResponse<Void> {
        val apiResult: Response<String> = remoteMissionSource.popupRead(reasonId)

        return if (apiResult.code() == 200) {
            ApiResponse(true, null, null)
        } else {
            ApiResponse(false, null, makeErrorResponseFromStatusCode(apiResult.code(), "/user/is-read"))
        }
    }

    private fun convertBitmapToFile(bitmap: Bitmap, name: String): File {
        //create a file to write bitmap data
        //val file = File(App().context().cacheDir, "FILE_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+"_")
        val file = File(App().context().cacheDir, name)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}