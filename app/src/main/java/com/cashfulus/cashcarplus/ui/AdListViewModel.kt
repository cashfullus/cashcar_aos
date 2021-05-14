package com.cashfulus.cashcarplus.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.AdResponse
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdListViewModel(private val missionRepository: MissionRepository): ViewModel() {
    // 광고 리스트
    val adList = MutableLiveData<ArrayList<AdResponse>>()
    // 로딩 및 오류
    val loading = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loadAdList(category: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = missionRepository.getAdList(category, 0, UserManager.jwtToken!!)

                if(response.isSucceed) {
                    adList.postValue(response.contents!!.data)
                    Log.d("CashcarPlus", response.contents!!.data.toString())
                } else {
                    error.postValue(response.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}