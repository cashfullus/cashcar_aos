package com.cashfulus.cashcarplus.ui.car

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
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

const val MODIFY_SUCCESS = 917
const val DELETE_SUCCESS = 156

class CarInfoViewModel(private val repository: CarRepository): BaseViewModel() {
    // 입력 폼 관련 LiveData
    val isKorean = MutableLiveData<Boolean>(true)
    val company = MutableLiveData<String>()
    val modelName = MutableLiveData<String>()
    val year = MutableLiveData<String>()
    val carNumber = MutableLiveData<String>()
    val isSupporters = MutableLiveData<Boolean>(true)
    val owner = MutableLiveData<String>("];9(")
    var isDeletable: Boolean? = null

    // 받아온 제조사 정보를 Spinner에 보내기 위한 LiveData
    val companyIndex = SingleLiveEvent<Int>()

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
                    isKorean.postValue(carResponse.contents!!.data.isForeignCar == 0)
                    company.postValue(carResponse.contents!!.data.brand)
                    modelName.postValue(carResponse.contents!!.data.model)
                    year.postValue(carResponse.contents!!.data.year.toString())
                    carNumber.postValue(carResponse.contents!!.data.carNumber)
                    isSupporters.postValue(carResponse.contents!!.data.supporters == 1)
                    owner.postValue(carResponse.contents!!.data.ownerRelationship)
                    isDeletable = carResponse.contents!!.data.isDelete

                    when(carResponse.contents!!.data.brand) {
                        "현대" -> { companyIndex.postValue(0) }
                        "제네시스" -> { companyIndex.postValue(1) }
                        "기아" -> { companyIndex.postValue(2) }
                        "한국GM" -> { companyIndex.postValue(3) }
                        "르노삼성" -> { companyIndex.postValue(4) }
                        "쌍용" -> { companyIndex.postValue(5) }

                        "벤츠" -> { companyIndex.postValue(0) }
                        "BMW" -> { companyIndex.postValue(1) }
                        "아우디" -> { companyIndex.postValue(2) }
                        "폭스바겐" -> { companyIndex.postValue(3) }
                        "볼보" -> { companyIndex.postValue(4) }
                        "쉐보레" -> { companyIndex.postValue(5) }
                        "테슬라" -> { companyIndex.postValue(6) }
                        "미니" -> { companyIndex.postValue(7) }
                        "렉서스" -> { companyIndex.postValue(8) }
                        "닛산" -> { companyIndex.postValue(9) }
                        "도요타" -> { companyIndex.postValue(10) }
                        "혼다" -> { companyIndex.postValue(11) }
                        "인피니티" -> { companyIndex.postValue(12) }
                        "마세라티" -> { companyIndex.postValue(13) }
                        "람보르기니" -> { companyIndex.postValue(14) }
                        "포드" -> { companyIndex.postValue(15) }
                        "랜드로버" -> { companyIndex.postValue(16) }
                        "링컨" -> { companyIndex.postValue(17) }
                        "재규어" -> { companyIndex.postValue(18) }
                        "지프" -> { companyIndex.postValue(19) }
                        "페라리" -> { companyIndex.postValue(20) }
                        "포르쉐" -> { companyIndex.postValue(21) }
                        "푸조" -> { companyIndex.postValue(22) }

                        else  -> { companyIndex.postValue(-1) }
                    }

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