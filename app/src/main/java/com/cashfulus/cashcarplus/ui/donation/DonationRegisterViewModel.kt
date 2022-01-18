package com.cashfulus.cashcarplus.ui.donation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.DonationRepository
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
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

class DonationRegisterViewModel(private val repository: DonationRepository): BaseViewModel() {
    val point = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val needReceipt = MutableLiveData<Boolean>(false)

    val isSucceed = MutableLiveData<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun registerDonation(organizationId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.userId != null && UserManager.jwtToken != null) {
                    val apiRespose = repository.registerDonation(point.value!!.toInt(), needReceipt.value!!, name.value!!, organizationId, UserManager.userId!!, UserManager.jwtToken!!)

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