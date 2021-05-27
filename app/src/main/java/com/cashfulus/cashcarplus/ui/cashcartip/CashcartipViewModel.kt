package com.cashfulus.cashcarplus.ui.cashcartip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.CashcarTipRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.CashcartipPost
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashcartipViewModel(private val repository: CashcarTipRepository): BaseViewModel() {
    val response = MutableLiveData<CashcartipPost>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loadCashcarTipPost(postId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val apiResponse = repository.getCashcarTipPost(postId, UserManager.userId!!, UserManager.jwtToken!!)

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