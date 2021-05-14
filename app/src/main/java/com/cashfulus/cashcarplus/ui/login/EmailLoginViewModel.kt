package com.cashfulus.cashcarplus.ui.login

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromMessage
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailLoginViewModel(private val repository: UserRepository): ViewModel() {
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    val loading = SingleLiveEvent<Boolean>()
    val response = MutableLiveData<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun loginPW() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val loginResponse = repository.loginPW(email.value!!, password.value!!, "normal")
                Log.d("Cashcar", loginResponse.toString())

                if (loginResponse.isSucceed) {
                    UserManager.isLogined = true
                    UserManager.email = email.value!!
                    UserManager.jwtToken = loginResponse.contents!!.data!!.jwt_token
                    UserManager.userId = loginResponse.contents!!.data!!.user_id
                    getUserInfo(loginResponse.contents!!.data!!.user_id, loginResponse.contents!!.data!!.jwt_token)
                } else {
                    error.postValue(loginResponse.error!!)
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
                    if(loginResponse.contents!!.data.name.isNotBlank()) {
                        UserManager.alarm = loginResponse.contents!!.data.alarm
                        UserManager.callNumber = loginResponse.contents!!.data.call_number
                        UserManager.dateBirth = loginResponse.contents!!.data.date_of_birth
                        UserManager.gender = loginResponse.contents!!.data.gender
                        UserManager.marketing = loginResponse.contents!!.data.marketing
                        UserManager.nickName = loginResponse.contents!!.data.nick_name
                        UserManager.name = loginResponse.contents!!.data.name
                        UserManager.profileImage = loginResponse.contents!!.data.profileImage
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