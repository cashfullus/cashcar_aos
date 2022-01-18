package com.cashfulus.cashcarplus.ui

import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.AdRepository
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.repository.PointRepository
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(private val repository: PointRepository): BaseViewModel() {
    val pointData = MutableLiveData<PointResponse>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loadPointInfo() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.getPointInfoAll(1, UserManager.userId!!, UserManager.jwtToken!!)

                    if (apiRespose.isSucceed)
                        pointData.postValue(apiRespose.contents!!)
                    else {
                        error.postValue(apiRespose.error!!)
                    }
                } else {
                    error.postValue(makeErrorResponseFromStatusCode(LOST_USER_INFO_ERROR_CODE, ""))
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}