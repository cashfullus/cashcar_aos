package com.cashfulus.cashcarplus.ui.mission

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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


class MissionViewModel(private val missionRepository: MissionRepository): ViewModel() {
    val response = MutableLiveData<AdMissionResponseForView>()
    val loading = SingleLiveEvent<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    /** 미션 타입 */
    val IMPORTANT = 0
    val ADDITIONAL = 1
    val DRIVING = 2

    /** onResume마다 호출되기 때문에, init에서 호출할 필요 없음. */
    fun loadData() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                loading.postValue(true)
                val result = missionRepository.getMissions(UserManager.userId!!, UserManager.jwtToken!!)

                if(result.isSucceed) {
                    Log.d("CashcarPlus", result.contents!!.toString())

                    /** View에 제공해야 하는 추가 데이터 목록 */
                    // 1. 미션 시작 여부 : "activity_end_date": "0000-00-00 00:00:00"이고, "activity_start_date": "0000-00-00 00:00:00"인 경우 미션 시작하지 않음.
                    val isMissionStart = result.contents.adUserInformation.activityStartDate != "0000-00-00 00:00:00"
                    // 2. 미션 기간 : activity_end_date - activity_start_date
                    val missionLength = if(isMissionStart) mappingDateLength(result.contents.adUserInformation.activityStartDate, result.contents.adUserInformation.activityEndDate) else null
                    // 3. 현재 미션 진행 기간 : '현재 날짜'-activity_start_date
                    val currentDate = if(isMissionStart) mappingDatePercent(result.contents.adUserInformation.activityStartDate) else null
                    // 4. 총 획득 예상 포인트 : 기본 포인트 + 추가 미션에서 획득 성공한 포인트 + 드라이빙에서 획득 성공한 포인트.
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

                    loading.postValue(false)
                    response.postValue(AdMissionResponseForView(isMissionStart, missionLength, currentDate, result.contents.adUserInformation, result.contents.images, important, additional, driving, result.contents.adUserInformation.totalPoint+additionalPoint))
                } else {
                    loading.postValue(false)
                    error.postValue(result.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}