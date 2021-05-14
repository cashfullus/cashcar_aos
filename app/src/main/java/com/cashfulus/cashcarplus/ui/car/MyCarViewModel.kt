package com.cashfulus.cashcarplus.ui.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.CarRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.MyCarResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCarViewModel(private val repository: CarRepository): ViewModel() {
    val loading = SingleLiveEvent<Boolean>()
    val response = MutableLiveData<ArrayList<MyCarResponse>>()
    val empty = SingleLiveEvent<Boolean>()
    val error = MutableLiveData<ErrorResponse>()

    init {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val carListResponse = repository.carList(UserManager.userId!!, UserManager.jwtToken!!)

                if (carListResponse.isSucceed) {
                    response.postValue(carListResponse.contents!!.data)
                } else {
                    // 소유한 차량이 존재하지 않을때
                    if(carListResponse.error!!.status == 201)
                        empty.postValue(true)
                    // 진짜로 오류가 발생한 경우
                    else
                        error.postValue(carListResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun refresh() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val carListResponse = repository.carList(UserManager.userId!!, UserManager.jwtToken!!)

                if (carListResponse.isSucceed) {
                    response.postValue(carListResponse.contents!!.data)
                } else {
                    // 소유한 차량이 존재하지 않을때
                    if(carListResponse.error!!.status == 201)
                        empty.postValue(true)
                    // 진짜로 오류가 발생한 경우
                    else
                        error.postValue(carListResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}