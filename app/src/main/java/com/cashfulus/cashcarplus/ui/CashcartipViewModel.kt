package com.cashfulus.cashcarplus.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.CashcarTipRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.CashcartipList
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashcartipViewModel(private val repository: CashcarTipRepository): ViewModel() {
    val loading = SingleLiveEvent<Boolean>()
    val firstData = MutableLiveData<CashcartipList>()
    val additionalData = MutableLiveData<CashcartipList>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loadCashcarTipList(page: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                loading.postValue(true)
                val apiResponse = repository.getCashcarTipList(page, UserManager.userId!!, UserManager.jwtToken!!)
                loading.postValue(false)

                if(apiResponse.isSucceed) {
                    firstData.postValue(apiResponse.contents!!)
                } else {
                    error.postValue(apiResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}