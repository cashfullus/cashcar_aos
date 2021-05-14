package com.cashfulus.cashcarplus.ui.inquiry

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.InquiryRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InquiryViewModel(private val repository: InquiryRepository): ViewModel() {

    // UI Field에서 직접 입력받는 값들
    val company = MutableLiveData<String>()
    val staffName = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val contents = MutableLiveData<String>()

    // 결과값 처리 관련 값들
    val loading = SingleLiveEvent<Boolean>()
    val response = MutableLiveData<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun submitInquiry() {
        Log.d("CashcarPlus", UserManager.userId!!.toString()+" "+company.value!!+" "+staffName.value!!+" "+phone.value!!+" "+email.value!!+" "+contents.value!!+" "+UserManager.jwtToken!!)
        /*if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                loading.postValue(true)
                val apiRespose = repository.sendInquiry(UserManager.userId!!, company.value!!, staffName.value!!, phone.value!!, email.value!!, contents.value!!, UserManager.jwtToken!!)

                if (apiRespose.isSucceed) {
                    response.postValue(true)
                    loading.postValue(false)
                } else {
                    error.postValue(apiRespose.error!!)
                    loading.postValue(false)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }*/
    }
}