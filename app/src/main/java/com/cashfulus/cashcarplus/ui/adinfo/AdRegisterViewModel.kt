package com.cashfulus.cashcarplus.ui.adinfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdRegisterViewModel(private val missionRepository: MissionRepository): BaseViewModel() {
    /** 입력 폼에서 받아오는 정보들 */
    val mainAddress = MutableLiveData<String>()
    val detailAddress = MutableLiveData<String>()
    val callNumber = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    /** 웹을 통해서 받아오는 값들 */
    val cars = MutableLiveData<List<VehicleInformation>>()
    val userInfo = MutableLiveData<UserInformation>()
    val response = MutableLiveData<ApplyResponseData>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun getCarInfo(adId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val registerResponse = missionRepository.applyAdGet(UserManager.userId!!, adId, UserManager.jwtToken!!)

                if (registerResponse.isSucceed) {
                    hideLoadingDialog()
                    cars.postValue(registerResponse.contents!!.data.vehicle_information)
                    userInfo.postValue(registerResponse.contents!!.data.user_information)
                } else {
                    hideLoadingDialog()
                    error.postValue(registerResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, "/ad/apply"))
        }
    }

    fun registerAd(adId: Int, vehicleId: Int) {
        //Log.d("CashcarPlus", mainAddress.value!!+" "+detailAddress.value!!+" "+callNumber.value!!+" "+name.value!!+" "+UserManager.userId!!+" "+adId+" "+UserManager.jwtToken!!)

        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val registerResponse = missionRepository.applyAd(mainAddress.value!!, detailAddress.value!!, callNumber.value!!.replace("-", ""), name.value!!, UserManager.userId!!, adId, vehicleId, UserManager.jwtToken!!)

                if (registerResponse.isSucceed) {
                    hideLoadingDialog()
                    response.postValue(registerResponse.contents!!)
                } else {
                    hideLoadingDialog()
                    error.postValue(registerResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, "/ad/apply"))
        }
    }
}