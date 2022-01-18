package com.cashfulus.cashcarplus.ui.point

import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.PointRepository
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PointViewModel(private val repository: PointRepository) : BaseViewModel() {
    val pointData = MutableLiveData<PointResponse>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        loadPointInfoAll(1)
    }

    fun loadPointInfoAll(page: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.getPointInfoAll(page, UserManager.userId!!, UserManager.jwtToken!!)

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

    fun loadPointInfoCategory(category: String, page: Int) { // 'donate', 'positive', 'negative'
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.getPointInfoCategory(category, page, UserManager.userId!!, UserManager.jwtToken!!)

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