package com.cashfulus.cashcarplus.ui.login

import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository): BaseViewModel() {
    val profileImg = MutableLiveData<Uri>()
    val nickname = MutableLiveData<String>("")
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val gender = MutableLiveData<String>("")
    val birth = MutableLiveData<String>("")

    val response = SingleLiveEvent<Boolean>()
    val error = MutableLiveData<ErrorResponse>()

    fun registerKakao(alarm: Boolean, marketing: Boolean) {
        if(NetworkManager().checkNetworkState()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    error.postValue(makeErrorResponseFromMessage("FCM 토큰이 존재하지 않습니다. 다시 시도해 주세요.", "/user/fcm"))
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                if(token != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        showLoadingDialog()
                        val registerResponse = repository.register(token, email.value!!, alarm, marketing, "kakao")

                        if (registerResponse.isSucceed && registerResponse.contents!!.status) {
                            UserManager.jwtToken = registerResponse.contents!!.data.jwt_token
                            UserManager.userId = registerResponse.contents!!.data.user_id
                            UserManager.email = email.value!!
                            hideLoadingDialog()
                            updateUserInfo(alarm, marketing)
                        } else {
                            hideLoadingDialog()
                            error.postValue(registerResponse.error!!)
                        }
                    }
                } else {
                    error.postValue(makeErrorResponseFromMessage("FCM 토큰이 존재하지 않습니다. 다시 시도해 주세요.", "/user/fcm"))
                }
            })
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, "/user/profile"))
        }
    }

    fun updateUserInfo(receiveAlarm: Boolean, receiveMarketing: Boolean) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(UserManager.jwtToken == null || UserManager.userId == null) {
                    response.postValue(false)
                } else {
                    if(profileImg.value != null) {
                        showLoadingDialog()
                        val updateResponse = repository.updateUserInfo(UserManager.jwtToken!!, UserManager.userId!!, nickname.value!!, email.value!!,
                            name.value!!, phone.value!!.replace("-", ""), gender.value!!, birth.value!!, if (receiveAlarm) 1 else 0,
                            if (receiveMarketing) 1 else 0, profileImg.value!!)

                        if (updateResponse.isSucceed && updateResponse.contents!!.status) {
                            UserManager.alarm = if(receiveAlarm) 1 else 0
                            UserManager.callNumber = phone.value!!
                            UserManager.dateBirth = birth.value!!
                            UserManager.gender = gender.value!!
                            UserManager.marketing = if(receiveMarketing) 1 else 0
                            UserManager.nickName = nickname.value!!
                            UserManager.name = name.value!!
                            UserManager.profileImage = updateResponse.contents.image

                            val userData = App().context(). getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
                            val editor = userData.edit()
                            editor.putString("jwtToken", UserManager.jwtToken)
                            editor.putInt("userId", UserManager.userId!!)
                            editor.apply()

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
                            if (receiveAlarm) 1 else 0, if (receiveMarketing) 1 else 0)

                        if (updateResponse.isSucceed && updateResponse.contents!!.status) {
                            UserManager.alarm = if(receiveAlarm) 1 else 0
                            UserManager.callNumber = phone.value!!
                            UserManager.dateBirth = birth.value!!
                            UserManager.gender = gender.value!!
                            UserManager.marketing = if(receiveMarketing) 1 else 0
                            UserManager.nickName = nickname.value!!
                            UserManager.name = name.value!!
                            UserManager.profileImage = ""

                            val userData = App().context().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
                            val userDataEditer = userData.edit()
                            userDataEditer.putString("jwtToken", UserManager.jwtToken)
                            userDataEditer.putInt("userId", UserManager.userId!!)
                            userDataEditer.apply()

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