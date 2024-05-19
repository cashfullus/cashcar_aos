package com.cashfulus.cashcarplus.ui.adinfo

import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdInfoViewModel(private val missionRepository: MissionRepository): BaseViewModel() {
    val response = MutableLiveData<AdInfoResponse>()
    val response2 = MutableLiveData<AdCodeResponse>()
    val error = SingleLiveEvent<ErrorResponse>()
    val error2 = SingleLiveEvent<ErrorResponse>()

    fun loadData(adId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val result = missionRepository.getAd(adId, UserManager.jwtToken!!, "1")

                if(result.isSucceed) {
                    hideLoadingDialog()
                    response.postValue(result.contents!!.data)
                } else {
                    hideLoadingDialog()
                    error.postValue(result.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun codeApply(adId: Int, code: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val result = missionRepository.applyAdCode(code, UserManager.userId!!, adId, UserManager.jwtToken!!)

                if(result.isSucceed) {
                    hideLoadingDialog()
                    response2.postValue(result.contents!!.data)
                } else {
                    hideLoadingDialog()
                    error2.postValue(result.error!!)
                }
            }
        } else {
            error2.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

}