package com.cashfulus.cashcarplus.ui.adinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.AdRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.AdResponse
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdListViewModel(private val repository: AdRepository): ViewModel() {
    // 광고 리스트
    val adList = MutableLiveData<ArrayList<AdResponse>>()
    // 로딩 및 오류
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        //loadingAdList()
    }

    /*fun loadingAdList() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val adListResponse = repository.getAdList()

                if(adListResponse.isSucceed) {
                    adList.postValue(adListResponse.contents!!)
                } else {
                    error.postValue(adListResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }*/
}