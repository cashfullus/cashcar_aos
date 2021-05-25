package com.cashfulus.cashcarplus.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.model.makeErrorResponseFromMessage
import com.cashfulus.cashcarplus.model.makeErrorResponseFromStatusCode
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterBasicViewModel(private val repository: UserRepository): ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val loading = SingleLiveEvent<Boolean>()
    val response = MutableLiveData<Boolean>()
    val error = SingleLiveEvent<ErrorResponse>()

    fun register(method: String, alarm: Boolean, marketing: Boolean) {
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
                        loading.postValue(true)
                        val registerResponse = repository.registerPW(token, email.value!!, password.value!!, alarm, marketing, method)

                        if (registerResponse.isSucceed && registerResponse.contents!!.status) {
                            UserManager.jwtToken = registerResponse.contents!!.data.jwt_token
                            UserManager.userId = registerResponse.contents!!.data.user_id
                            UserManager.email = email.value!!
                            loading.postValue(false)
                            response.postValue(true)
                        } else {
                            loading.postValue(false)
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
}