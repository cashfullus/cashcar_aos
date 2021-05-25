package com.cashfulus.cashcarplus.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.*
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val HOME_STATE_NO_CAR = 170
const val HOME_STATE_NO_AD = 171
const val HOME_STATE_AD_WAITING = 172
const val HOME_STATE_AD_WAITING_CANNOT_DELETABLE = 179
const val HOME_STATE_AD_REGISTER_FAILED = 181
const val HOME_STATE_STAND_BY = 173
const val HOME_STATE_REVIEW = 174
const val HOME_STATE_REJECT = 175
const val HOME_STATE_REJECT_ADDITIONAL = 194
const val HOME_STATE_REAUTH = 176
const val HOME_STATE_SUCCESS = 177
const val HOME_STATE_FAIL = 178
const val HOME_STATE_CURRENT_NO_MISSION = 180

// 메세지 타입
const val MISSION_MESSAGE_REGISTER_FAILED = "apply_reject"
const val MISSION_MESSAGE_APPLY_FAIL = "apply_fail"
const val MISSION_MESSAGE_APPLY_SUCCESS = "apply_success"
const val MISSION_MESSAGE_MISSION_FAIL = "mission_fail"

class HomeViewModel(private val missionRepository: MissionRepository): ViewModel() {
    // 현재 진행중인 광고 & 미션
    val currentMission = MutableLiveData<MyAdResponseWrapper>()
    // 로딩 및 오류
    val loading = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()
    // 미션 삭제 성공 시 알림
    val missionDeleted = SingleLiveEvent<Boolean>()

    // 현재 미션 id
    var UserApplyId: Int? = null

    fun loadData() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                loading.postValue(true)
                val response = missionRepository.getMyMission(UserManager.userId!!, UserManager.jwtToken!!)

                if(response.isSucceed) {
                    UserApplyId = response.contents!!.data.adInformation.adUserApplyId

                    lateinit var result: MyAdResponseWrapper
                    if(response.contents!!.data.adInformation.adId == -1) {
                        if(response.contents.data.vehicleInformation.isEmpty())
                            result = MyAdResponseWrapper(HOME_STATE_NO_CAR, response.contents)
                        else if(response.contents.data.message.message_type == "")
                            result = MyAdResponseWrapper(HOME_STATE_NO_AD, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_REGISTER_FAILED)
                            result = MyAdResponseWrapper(HOME_STATE_AD_REGISTER_FAILED, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_APPLY_FAIL)
                            result = MyAdResponseWrapper(HOME_STATE_FAIL, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_APPLY_SUCCESS)
                            result = MyAdResponseWrapper(HOME_STATE_SUCCESS, response.contents)
                        else //message_type == MISSION_MESSAGE_MISSION_FAIL
                            if(response.contents.data.adInformation.missionType == 1)
                                result = MyAdResponseWrapper(HOME_STATE_REJECT_ADDITIONAL, response.contents)
                            else
                                result = MyAdResponseWrapper(HOME_STATE_REJECT, response.contents)
                    } else if(response.contents.data.adInformation.applyStatus == APPLY_STATUS_STAND_BY && response.contents.data.isDeletable)
                        result = MyAdResponseWrapper(HOME_STATE_AD_WAITING, response.contents)
                    else if(response.contents.data.adInformation.applyStatus == APPLY_STATUS_STAND_BY)
                        result = MyAdResponseWrapper(HOME_STATE_AD_WAITING_CANNOT_DELETABLE, response.contents)
                    else {
                        if(response.contents.data.message.message_type == MISSION_MESSAGE_REGISTER_FAILED)
                            result = MyAdResponseWrapper(HOME_STATE_AD_REGISTER_FAILED, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_APPLY_FAIL)
                            result = MyAdResponseWrapper(HOME_STATE_FAIL, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_APPLY_SUCCESS)
                            result = MyAdResponseWrapper(HOME_STATE_SUCCESS, response.contents)
                        else if(response.contents.data.message.message_type == MISSION_MESSAGE_MISSION_FAIL)
                            if(response.contents.data.adInformation.missionType == 1)
                                result = MyAdResponseWrapper(HOME_STATE_REJECT_ADDITIONAL, response.contents)
                            else
                                result = MyAdResponseWrapper(HOME_STATE_REJECT, response.contents)
                        else {
                            when (response.contents.data.adInformation.missionStatus) {
                                MISSION_STATUS_STAND_BY -> result = MyAdResponseWrapper(HOME_STATE_CURRENT_NO_MISSION, response.contents)
                                MISSION_STATUS_ON_GOING -> result = MyAdResponseWrapper(HOME_STATE_STAND_BY, response.contents)
                                MISSION_STATUS_REVIEW -> result = MyAdResponseWrapper(HOME_STATE_REVIEW, response.contents)
                                MISSION_STATUS_REJECT -> {
                                    if(response.contents.data.adInformation.missionType == 1)
                                        result = MyAdResponseWrapper(HOME_STATE_REJECT_ADDITIONAL, response.contents)
                                    else
                                        result = MyAdResponseWrapper(HOME_STATE_REJECT, response.contents)
                                }
                                MISSION_STATUS_REAUTH -> result = MyAdResponseWrapper(HOME_STATE_REAUTH, response.contents)
                                MISSION_STATUS_SUCCESS -> result = MyAdResponseWrapper(HOME_STATE_SUCCESS, response.contents)
                                MISSION_STATUS_FAIL -> result = MyAdResponseWrapper(HOME_STATE_FAIL, response.contents)
                            }
                        }
                    }

                    currentMission.postValue(result)
                    loading.postValue(false)
                } else {
                    error.postValue(response.error!!)
                    loading.postValue(false)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun deleteMyMission() {
        if(NetworkManager().checkNetworkState() && UserApplyId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = missionRepository.deleteMyMission(UserApplyId!!, UserManager.userId!!, UserManager.jwtToken!!)

                if(response.isSucceed) {
                    missionDeleted.postValue(true)
                } else {
                    error.postValue(response.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun messageRead(reasonId: Int) {
        if(NetworkManager().checkNetworkState() && UserApplyId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                missionRepository.popupRead(reasonId)
                loadData()
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    /*fun refreshHome() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val missionResponse = missionRepository.getMissionInfo()

                if (missionResponse.isSucceed) {
                    val adListResponse = adRepository.getAdList()
                    if(adListResponse.isSucceed) {
                        currentMission.postValue(missionResponse.contents!!)
                        adList.postValue(adListResponse.contents!!)
                    } else {
                        error.postValue(adListResponse.error!!)
                    }
                }
                else {
                    error.postValue(missionResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }*/
}