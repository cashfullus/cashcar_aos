package com.cashfulus.cashcarplus.ui.mission

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MissionCertViewModel(private val repository: MissionRepository): BaseViewModel() {
    val sideImg = MutableLiveData<Bitmap>()
    val rearImg = MutableLiveData<Bitmap>()
    val gaugeImg = MutableLiveData<Bitmap>()
    val gaugeKm = MutableLiveData<String>()
    val gaugeError = MutableLiveData<Boolean>(true)

    val response = MutableLiveData<AdMissionPost>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun uploadMission(missionId: Int, isAdditional: Boolean) {
        // 모든 값이 정상적으로 들어와있는지 검사
        if(!isAdditional) {
            if(sideImg.value == null)
                error.postValue(makeErrorResponseFromMessage("측면 사진을 촬영해주세요.", "/ad/mission"))
            else if(rearImg.value == null)
                error.postValue(makeErrorResponseFromMessage("후면 사진을 촬영해주세요.", "/ad/mission"))
            else if(gaugeImg.value == null)
                error.postValue(makeErrorResponseFromMessage("계기판 사진을 촬영해주세요.", "/ad/mission"))
            else if(gaugeKm.value == null || gaugeError.value!!)
                error.postValue(makeErrorResponseFromMessage("주행거리를 올바른 값으로 입력해주세요.", "/ad/mission"))
            else {
                if(NetworkManager().checkNetworkState()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        showLoadingDialog()
                        val apiRespose = repository.postMission(sideImg.value!!, rearImg.value!!, gaugeImg.value!!, gaugeKm.value!!, missionId, UserManager.userId!!, UserManager.jwtToken!!)

                        if (apiRespose.isSucceed) {
                            hideLoadingDialog()
                            response.postValue(apiRespose.contents!!)
                        } else {
                            hideLoadingDialog()
                            error.postValue(apiRespose.error!!)
                        }
                    }
                } else {
                    error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
                }
            }
        } else {
            if(sideImg.value == null)
                error.postValue(makeErrorResponseFromMessage("측면 사진을 촬영해주세요.", "/ad/mission"))
            else if(rearImg.value == null)
                error.postValue(makeErrorResponseFromMessage("후면 사진을 촬영해주세요.", "/ad/mission"))
            else {
                if(NetworkManager().checkNetworkState()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        showLoadingDialog()
                        val apiRespose = repository.postMission(sideImg.value!!, rearImg.value!!, missionId, UserManager.userId!!, UserManager.jwtToken!!)

                        if (apiRespose.isSucceed) {
                            hideLoadingDialog()
                            response.postValue(apiRespose.contents!!)
                        } else {
                            hideLoadingDialog()
                            error.postValue(apiRespose.error!!)
                        }
                    }
                } else {
                    error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
                }
            }
        }
    }
}