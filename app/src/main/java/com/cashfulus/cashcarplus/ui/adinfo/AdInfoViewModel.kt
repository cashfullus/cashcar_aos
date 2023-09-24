package com.cashfulus.cashcarplus.ui.adinfo

import android.util.Log
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

class AdInfoViewModel(private val missionRepository: MissionRepository): BaseViewModel() {
    val response = MutableLiveData<AdInfoResponse>()
    val error = SingleLiveEvent<ErrorResponse>()

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
}