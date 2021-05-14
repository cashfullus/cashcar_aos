package com.cashfulus.cashcarplus.ui.adinfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdInfoViewModel(private val missionRepository: MissionRepository): ViewModel() {
    val response = MutableLiveData<AdInfoResponse>()
    val loading = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loadData(adId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = missionRepository.getAd(adId, UserManager.jwtToken!!)

                if(result.isSucceed) {
                    response.postValue(result.contents!!.data)
                    Log.d("CashcarPlus", result.contents!!.data.toString())
                } else {
                    error.postValue(result.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}