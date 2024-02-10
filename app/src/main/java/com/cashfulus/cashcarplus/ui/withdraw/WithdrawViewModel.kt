package com.cashfulus.cashcarplus.ui.withdraw

import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.PointRepository
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.BankInfo
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WithdrawViewModel(private val repository: PointRepository) : BaseViewModel() {
    val point = MutableLiveData<String>()
    val bank = MutableLiveData<String>()
    val account = MutableLiveData<String>()
    val accountResident = MutableLiveData<String>()
    val accountResidentBack = MutableLiveData<String>()
    val name = MutableLiveData<String>(UserManager.name!!)
    val isMain = MutableLiveData<Boolean>(false)

    val bankData = MutableLiveData<BankInfo>()
    val isSucceed = MutableLiveData<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.getBankInfo(UserManager.userId!!, UserManager.jwtToken!!)

                    if (apiRespose.isSucceed)
                        bankData.postValue(apiRespose.contents!!)
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

    fun registerWithdraw() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.registerWithdraw(name.value!!, bank.value!!, account.value!!, accountResident.value!!, accountResidentBack.value!!,
                            if(isMain.value!!) 1 else 0, point.value!!.toInt(), UserManager.userId!!, UserManager.jwtToken!!)

                    if (apiRespose.isSucceed)
                        isSucceed.postValue(true)
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