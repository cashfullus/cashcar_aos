package com.cashfulus.cashcarplus.ui.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.CarRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeCustomErrorResponse
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCarViewModel(private val repository: CarRepository): BaseViewModel() {
    // 입력 폼 관련 LiveData
    val isKorean = MutableLiveData<Boolean>(true)
    val company = MutableLiveData<String>()
    val modelName = MutableLiveData<String>()
    val year = MutableLiveData<String>()
    val carNumber = MutableLiveData<String>()
    val isSupporters = MutableLiveData<Boolean>(true)
    val owner = MutableLiveData<String>()

    // API 호출 결과
    val response = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    // 자동차 등록
    fun registerCar() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val registerResponse = repository.registerCarInfo(UserManager.userId!!, if(isSupporters.value!!) 1 else 0,
                        if(isKorean.value!!) 0 else 1, company.value!!, modelName.value!!, year.value!!.toInt(), carNumber.value!!, owner.value!!, UserManager.jwtToken!!)

                if (registerResponse.isSucceed) {
                    hideLoadingDialog()
                    response.postValue(registerResponse.contents!!.status)
                } else {
                    // 최대 허용된 차량 개수 초과
                    hideLoadingDialog()
                    if(registerResponse.error!!.status == 405) {
                        error.postValue(makeCustomErrorResponse(405, "차량은 최대 3대까지 등록 가능합니다.", "/vehicle/register"))
                    }
                    // 차량번호 중복확인
                    else if(registerResponse.error!!.status == 409) {
                        error.postValue(makeCustomErrorResponse(409, "이미 등록된 차량입니다. 다른 차량을 입력해주세요.", "/vehicle/register"))
                    }
                    // 진짜로 오류가 발생한 경우
                    else {
                        error.postValue(registerResponse.error!!)
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}