package com.cashfulus.cashcarplus.ui.login

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository): BaseViewModel() {
    val response = MutableLiveData<Boolean>()
    val error = MutableLiveData<ErrorResponse>()

    /** 로그인 실패 시, 꼭! 로그아웃 시킬 것!!! */
    fun kakaoLogin(context: Context) {
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, kakaoErr ->
                if(kakaoErr != null) {
                    error.postValue(makeErrorResponseFromMessage(kakaoErr.localizedMessage, "/login"))
                } else if(token != null) {
                    UserApiClient.instance.me { user, error2 ->
                        if(error2 != null) {
                            error.postValue(makeErrorResponseFromMessage(error2.localizedMessage, "/login"))
                        } else if(user != null) {
                            if(user.kakaoAccount != null && user.kakaoAccount!!.email != null) {
                                loginToServer(user.kakaoAccount!!.email!!, "kakao")
                            } else {
                                error.postValue(makeErrorResponseFromMessage("카카오에서 이메일을 가져올 수 없습니다. 카카오계정에 이메일을 등록해주세요.", "/login"))
                            }
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, kakaoErr ->
                if(kakaoErr != null) {
                    error.postValue(makeErrorResponseFromMessage(kakaoErr.localizedMessage, "/login"))
                } else if(token != null) {
                    UserApiClient.instance.me { user, error2 ->
                        if(error2 != null) {
                            error.postValue(makeErrorResponseFromMessage(error2.localizedMessage, "/login"))
                        } else if(user != null) {
                            if(user.kakaoAccount != null && user.kakaoAccount!!.email != null) {
                                loginToServer(user.kakaoAccount!!.email!!, "kakao")
                            } else {
                                error.postValue(makeErrorResponseFromMessage("카카오에서 이메일을 가져올 수 없습니다.", "/login"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginToServer(email: String, type: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val loginResponse = repository.login(email, type)
                Log.d("Cashcar", loginResponse.toString())

                if (loginResponse.isSucceed) {
                    UserManager.isLogined = true
                    UserManager.email = email
                    UserManager.jwtToken = loginResponse.contents!!.data!!.jwt_token
                    UserManager.userId = loginResponse.contents!!.data!!.user_id
                    getUserInfo(loginResponse.contents!!.data!!.user_id, loginResponse.contents!!.data!!.jwt_token)
                } else {
                    // 404 : 해당 계정이 없음
                    if(loginResponse.error!!.status == 404) {
                        UserManager.email = email
                        Log.d("Cashcar", email)
                        Log.d("Cashcar", UserManager.email!!)
                        response.postValue(false)
                    } else {
                        error.postValue(loginResponse.error!!)
                    }
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    private fun getUserInfo(userId: Int, token: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val loginResponse = repository.getUserInfo(userId, token)

                if (loginResponse.isSucceed) {
                    if(loginResponse.contents!!.data.nick_name.isNotBlank()) {
                        UserManager.alarm = loginResponse.contents!!.data.alarm
                        UserManager.callNumber = loginResponse.contents!!.data.call_number
                        UserManager.dateBirth = loginResponse.contents!!.data.date_of_birth
                        UserManager.gender = loginResponse.contents!!.data.gender
                        UserManager.marketing = loginResponse.contents!!.data.marketing
                        UserManager.nickName = loginResponse.contents!!.data.nick_name
                    }
                    updateToken()
                } else {
                    error.postValue(loginResponse.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    private fun updateToken() {
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
                        val loginResponse = repository.updateFcm(UserManager.userId!!, token)

                        if (loginResponse.isSucceed) {
                            val userData = App().context().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
                            val userDataEditer = userData.edit()
                            userDataEditer.putString("jwtToken", UserManager.jwtToken)
                            userDataEditer.putInt("userId", UserManager.userId!!)
                            userDataEditer.apply()

                            response.postValue(true)
                        } else {
                            error.postValue(loginResponse.error!!)
                        }
                    }
                } else {
                    error.postValue(makeErrorResponseFromMessage("FCM 토큰이 존재하지 않습니다. 다시 시도해 주세요.", "/user/fcm"))
                }
            })
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}