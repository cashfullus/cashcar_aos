package com.cashfulus.cashcarplus.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAddressViewModel(private val repository: UserRepository): ViewModel() {
    val name = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val mainAddress = MutableLiveData<String>()
    val detailAddress = MutableLiveData<String>()

    val data = SingleLiveEvent<UserAddress>()
    val response = SingleLiveEvent<Boolean>()
    val error = MutableLiveData<ErrorResponse>()

    init {
        getAddressInfo()
    }

    fun getAddressInfo() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val addressResponse = repository.getUserAddress(UserManager.userId!!, UserManager.jwtToken!!)

                if (addressResponse.isSucceed) {
                    data.postValue(addressResponse.contents!!)
                } else {
                    error.postValue(addressResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun postAddressInfo() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val addressResponse = repository.postUserAddress(UserManager.userId!!, phone.value!!, detailAddress.value!!, mainAddress.value!!, name.value!!, UserManager.jwtToken!!)

                if (addressResponse.isSucceed && addressResponse.contents!!.isUpdate) {
                    response.postValue(true)
                } else if(addressResponse.isSucceed && !addressResponse.contents!!.isUpdate) {
                    error.postValue(makeErrorResponseFromMessage("오류가 발생해서, 배송지 정보를 업데이트하지 못했습니다.", "/user/set/address"))
                } else {
                    error.postValue(addressResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}