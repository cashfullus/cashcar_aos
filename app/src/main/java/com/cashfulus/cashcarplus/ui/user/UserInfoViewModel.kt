package com.cashfulus.cashcarplus.ui.user

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserInfoViewModel(private val repository: UserRepository): BaseViewModel() {
    val originImg = SingleLiveEvent<String>()
    val profileImg = MutableLiveData<Uri>()
    val profileImgAnd11 = MutableLiveData<String>()
    val nickname = MutableLiveData<String>("")
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val gender = MutableLiveData<String>("")
    val birth = MutableLiveData<String>("")
    val receiveAlarm = MutableLiveData<Boolean>()
    val receiveMarketing = MutableLiveData<Boolean>()

    val response = SingleLiveEvent<Boolean>()
    val error = MutableLiveData<ErrorResponse>()

    init {
        receiveAlarm.postValue(UserManager.alarm == 1)
        receiveMarketing.postValue(UserManager.marketing == 1)

        /** 유저 정보가 이미 기입되어 있다면 셋팅. */
        if(UserManager.nickName != null)
            nickname.postValue(UserManager.nickName)
        if(UserManager.name != null)
            name.postValue(UserManager.name)
        if(UserManager.email != null)
            email.postValue(UserManager.email)
        if(UserManager.callNumber != null)
            phone.postValue(UserManager.callNumber)
        if(UserManager.dateBirth != null)
            birth.postValue(UserManager.dateBirth)
        if(UserManager.profileImage != null && !UserManager.profileImage.isNullOrBlank())
            originImg.postValue(UserManager.profileImage!!)
        if(UserManager.gender != null && UserManager.gender!!.isNotBlank())
            gender.postValue(UserManager.gender)
        else
            gender.postValue("-1")
    }

    fun updateUserInfo() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.jwtToken == null || UserManager.userId == null) {
                    response.postValue(false)
                } else {
                    if(Build.VERSION.SDK_INT < 30 && profileImg.value != null) {
                        showLoadingDialog()
                        val updateResponse = repository.updateUserInfo(UserManager.jwtToken!!, UserManager.userId!!, nickname.value!!, email.value!!,
                            name.value!!, phone.value!!.replace("-", ""), gender.value!!, birth.value!!, if (receiveAlarm.value!!) 1 else 0,
                            if (receiveMarketing.value!!) 1 else 0, profileImg.value!!)

                        if (updateResponse.isSucceed && updateResponse.contents!!.status) {
                            UserManager.alarm = if(receiveAlarm.value!!) 1 else 0
                            UserManager.callNumber = phone.value!!
                            UserManager.dateBirth = birth.value!!
                            UserManager.gender = gender.value!!
                            UserManager.marketing = if(receiveMarketing.value!!) 1 else 0
                            UserManager.nickName = nickname.value!!
                            UserManager.name = name.value!!
                            UserManager.profileImage = updateResponse.contents.image

                            hideLoadingDialog()
                            response.postValue(true)
                        } else {
                            hideLoadingDialog()
                            // 404 : 해당 계정이 없음 -> 로그아웃 처리
                            if(updateResponse.error!!.status == 404) {
                                response.postValue(false)
                            } else {
                                error.postValue(updateResponse.error!!)
                            }
                        }
                    } else if(Build.VERSION.SDK_INT >= 30 && profileImgAnd11.value != null) {
                        showLoadingDialog()
                        val updateResponse = repository.updateUserInfo(UserManager.jwtToken!!, UserManager.userId!!, nickname.value!!, email.value!!,
                                name.value!!, phone.value!!.replace("-", ""), gender.value!!, birth.value!!, if (receiveAlarm.value!!) 1 else 0,
                                if (receiveMarketing.value!!) 1 else 0, profileImgAnd11.value!!)

                        if (updateResponse.isSucceed && updateResponse.contents!!.status) {
                            UserManager.alarm = if(receiveAlarm.value!!) 1 else 0
                            UserManager.callNumber = phone.value!!
                            UserManager.dateBirth = birth.value!!
                            UserManager.gender = gender.value!!
                            UserManager.marketing = if(receiveMarketing.value!!) 1 else 0
                            UserManager.nickName = nickname.value!!
                            UserManager.name = name.value!!
                            UserManager.profileImage = updateResponse.contents.image

                            hideLoadingDialog()
                            response.postValue(true)
                        } else {
                            hideLoadingDialog()
                            // 404 : 해당 계정이 없음 -> 로그아웃 처리
                            if(updateResponse.error!!.status == 404) {
                                response.postValue(false)
                            } else {
                                error.postValue(updateResponse.error!!)
                            }
                        }
                    } else {
                        showLoadingDialog()
                        val updateResponse = repository.updateUserInfo(UserManager.jwtToken!!, UserManager.userId!!, nickname.value!!, email.value!!,
                            name.value!!, phone.value!!.replace("-", ""), gender.value!!, birth.value!!,
                            if (receiveAlarm.value!!) 1 else 0, if (receiveMarketing.value!!) 1 else 0)

                        if (updateResponse.isSucceed && updateResponse.contents!!.status) {
                            UserManager.alarm = if(receiveAlarm.value!!) 1 else 0
                            UserManager.callNumber = phone.value!!
                            UserManager.dateBirth = birth.value!!
                            UserManager.gender = gender.value!!
                            UserManager.marketing = if(receiveMarketing.value!!) 1 else 0
                            UserManager.nickName = nickname.value!!
                            UserManager.name = name.value!!
                            if(originImg.value == null)
                                UserManager.profileImage = ""
                            else
                                UserManager.profileImage = originImg.value!!

                            hideLoadingDialog()
                            response.postValue(true)
                        } else {
                            hideLoadingDialog()

                            // 404 : 해당 계정이 없음 -> 로그아웃 처리
                            if(updateResponse.error!!.status == 404) {
                                response.postValue(false)
                            } else {
                                error.postValue(updateResponse.error!!)
                            }
                        }
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, "/user/profile"))
        }
    }
}