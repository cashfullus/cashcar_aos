package com.cashfulus.cashcarplus.ui.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.AlarmRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.AlarmResponse
//import com.cashfulus.cashcarplus.model.AlarmResponse
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel (private val repository: AlarmRepository): ViewModel() {
    val loading = SingleLiveEvent<Boolean>()
    val alarmList = MutableLiveData<ArrayList<AlarmResponse>>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        getAlarmList()
    }

    fun getAlarmList() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val apiResponse = repository.getAlarmApi(UserManager.userId!!, 1, UserManager.jwtToken!!)

                if(apiResponse.isSucceed)
                    alarmList.postValue(apiResponse.contents!!.data)
                else
                    error.postValue(apiResponse.error!!)

                loading.postValue(false)
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}