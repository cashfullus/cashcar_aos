package com.cashfulus.cashcarplus.ui.faq

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.FaqRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.FaqResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FaqViewModel(private val repository: FaqRepository): BaseViewModel() {
    val faqList = MutableLiveData<ArrayList<FaqResponse>>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        loadFaqList()
    }

    fun loadFaqList() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val apiRespose = repository.getFaqApi()

                if (apiRespose.isSucceed)
                    faqList.postValue(apiRespose.contents!!.data)
                else {
                    error.postValue(apiRespose.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}