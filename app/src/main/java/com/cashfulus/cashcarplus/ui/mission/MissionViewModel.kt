package com.cashfulus.cashcarplus.ui.mission

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.MISSION_STATUS_SUCCESS
import com.cashfulus.cashcarplus.data.repository.MissionRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import com.cashfulus.cashcarplus.util.mappingDateLength
import com.cashfulus.cashcarplus.util.mappingDatePercent
import com.cashfulus.cashcarplus.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MissionViewModel(private val missionRepository: MissionRepository): BaseViewModel() {
    val response = MutableLiveData<AdMissionResponseForView>()
    val error = SingleLiveEvent<ErrorResponse>()

    /** 미션 타입 */
    val IMPORTANT = 0
    val ADDITIONAL = 1
    val DRIVING = 2

    /** onResume마다 호출되기 때문에, init에서 호출할 필요 없음. */
    fun loadData() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                showLoadingDialog()
                val result = missionRepository.getMissions(UserManager.userId!!, UserManager.jwtToken!!)

                if(result.isSucceed) {
                    Log.d("CashcarPlus", result.contents!!.toString())

                    /** View에 제공해야 하는 추가 데이터 목록 */
                    // 1. 미션 시작 여부 : "activity_end_date": "0000-00-00 00:00:00"이고, "activity_start_date": "0000-00-00 00:00:00"인 경우 미션 시작하지 않음.
                    val isMissionStart = result.contents.adUserInformation.activityStartDate != "0000-00-00 00:00:00" && result.contents.adUserInformation.activityStartDate != ""
                    // 5. mission_information 배열 -> 필수미션/추가미션/드라이빙의 3개 ArrayList로 분할하기
                    val important = ArrayList<MissionImportant>()
                    val additional = ArrayList<MissionAdditional>()
                    val driving = ArrayList<Driving>()
                    var additionalPoint = 0
                    for(item in result.contents.missionInformation) {
                        if(item.status == MISSION_STATUS_SUCCESS)
                            additionalPoint += item.additionalPoint

                        when(item.missionType) {
                            IMPORTANT -> important.add(MissionImportant(item.status, item.missionEndDate, item.adMissionCardUserId, item.order))
                            ADDITIONAL -> additional.add(MissionAdditional(item.status, item.missionEndDate, item.missionName, item.additionalPoint, item.adMissionCardUserId))
                            DRIVING -> driving.add(Driving(item.status, item.missionEndDate, item.missionName, item.additionalPoint, item.adMissionCardUserId))
                        }
                    }

                    hideLoadingDialog()
                    response.postValue(AdMissionResponseForView(isMissionStart, result.contents.dayPercent, result.contents.adUserInformation, result.contents.images, important, additional, driving))
                } else {
                    hideLoadingDialog()
                    error.postValue(result.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}