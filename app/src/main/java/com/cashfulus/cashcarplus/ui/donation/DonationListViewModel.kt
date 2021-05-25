package com.cashfulus.cashcarplus.ui.donation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.DonationRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.DonationListResponseData
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DonationListViewModel(private val repository: DonationRepository): ViewModel() {
    val loading = SingleLiveEvent<Boolean>()
    val response = MutableLiveData<DonationListResponseData>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        loadDonationList()
    }

    fun loadDonationList() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                loading.postValue(true)
                val apiResponse = repository.getDonationList(UserManager.userId!!, UserManager.jwtToken!!)
                loading.postValue(false)

                if(apiResponse.isSucceed) {
                    response.postValue(apiResponse.contents!!)
                } else {
                    error.postValue(apiResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}