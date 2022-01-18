package com.cashfulus.cashcarplus.ui.car

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.CarRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val MODIFY_SUCCESS = 917
const val DELETE_SUCCESS = 156

class CarInfoViewModel(private val repository: CarRepository): BaseViewModel() {
    // 초기에 정보를 받아오는 LiveData
    val myCarInfo = SingleLiveEvent<MyCarInfoRef>()

    // 입력 폼 관련 LiveData
    val isKorean = MutableLiveData<Boolean>(true)
    val company = MutableLiveData<String>()
    val modelName = MutableLiveData<String>()
    val year = MutableLiveData<String>()
    val carNumber = MutableLiveData<String>()
    val isSupporters = MutableLiveData<Boolean>(true)
    val owner = MutableLiveData<String>("];9(")
    var isDeletable: Boolean? = null

    // API 호출 결과
    val response = SingleLiveEvent<Int>()
    val error = SingleLiveEvent<ErrorResponse>()

    // 자동차 정보를 얻음
    fun getCarInfo(vehicleId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val carResponse = repository.carInfo(UserManager.userId!!, vehicleId, UserManager.jwtToken!!)

                if (carResponse.isSucceed) {
                    myCarInfo.postValue(carResponse.contents!!)
                    hideLoadingDialog()
                } else {
                    hideLoadingDialog()
                    // 소유한 차량이 존재하지 않을때
                    if(carResponse.error!!.status == 404) {
                        error.postValue(makeCustomErrorResponse(404, "해당 차량정보가 존재하지 않습니다.", "/vehicle/information"))
                    // 진짜로 오류가 발생한 경우
                    } else {
                        error.postValue(carResponse.error!!)
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    // 자동차 정보 삭제
    fun deleteCarInfo(vehicleId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val carResponse = repository.deleteCarInfo(UserManager.userId!!, vehicleId, UserManager.jwtToken!!)

                if (carResponse.isSucceed) {
                    hideLoadingDialog()
                    response.postValue(DELETE_SUCCESS)
                } else {
                    // 소유한 차량이 존재하지 않을때
                    hideLoadingDialog()
                    if(carResponse.error!!.status == 404) {
                        error.postValue(makeCustomErrorResponse(404, "해당 차량정보가 존재하지 않습니다.", "/vehicle/information"))
                    // 진짜로 오류가 발생한 경우
                    } else {
                        error.postValue(carResponse.error!!)
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    // 자동차 정보 수정
    fun modifyCarInfo(vehicleId: Int) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val carResponse = repository.modifyCarInfo(if(isSupporters.value!!) 1 else 0, if(isKorean.value!!) 0 else 1,
                        company.value!!, modelName.value!!, year.value!!.toInt(), carNumber.value!!, owner.value!!, UserManager.userId!!, vehicleId, UserManager.jwtToken!!)

                if (carResponse.isSucceed) {
                    hideLoadingDialog()
                    response.postValue(MODIFY_SUCCESS)
                } else {
                    hideLoadingDialog()

                    // 소유한 차량이 존재하지 않을때
                    if(carResponse.error!!.status == 404) {
                        error.postValue(makeCustomErrorResponse(404, "해당 차량정보가 존재하지 않습니다.", "/vehicle/information"))
                    // 차량 번호가 이미 등록된 번호일 경우(현재 업데이트할 차량 제외)
                    } else if(carResponse.error!!.status == 409) {
                        error.postValue(makeCustomErrorResponse(409, "이미 등록된 번호입니다.", "/vehicle/information"))
                    // 진짜로 오류가 발생한 경우
                    } else {
                        error.postValue(carResponse.error!!)
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}