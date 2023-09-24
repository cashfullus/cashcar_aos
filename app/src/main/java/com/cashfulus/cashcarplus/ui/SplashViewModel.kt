package com.cashfulus.cashcarplus.ui

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.App
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.cashfulus.cashcarplus.data.repository.UserRepository
import com.cashfulus.cashcarplus.data.repository.VersionRepository
import com.cashfulus.cashcarplus.data.service.API_CONNECT_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.VERSION_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 로그인 함수 호출 순서
 *  Kakao : API Login -> versionCheckWithAPI(email, type) -> loginToServer(email, type) -> getUserInfo(userId, token) -> updateToken() -> isLogined update
 *  Email : SharedPreference -> versionCheck() -> getUserInfo(userId, token) -> updateToken() -> isLogined update
 */

class SplashViewModel(private val repository: UserRepository, private val versionRepo: VersionRepository) : ViewModel() {
    val error = SingleLiveEvent<ErrorResponse>()
    val isLogined = SingleLiveEvent<Boolean>()

    fun versionCheckWithAPI(email: String, type: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val versionRecent = versionRepo.versionApi()

                try {
                    val pInfo = App().context().packageManager.getPackageInfo(App().context().packageName, PackageManager.GET_META_DATA)
                    val versionCurrent = (pInfo.versionName[0].toInt()-48)*10000 + (pInfo.versionName[2].toInt()-48)*100 + (pInfo.versionName[4].toInt()-48)

                    if(versionRecent.isSucceed) {
                        if(versionRecent.contents!!.version <= versionCurrent)
                            loginToServer(email, type)
                        else
                            error.postValue(makeCustomErrorResponse(VERSION_ERROR_CODE, "새 버전으로 업데이트해주세요.", ""))
                    } else {
                        error.postValue(versionRecent.error!!)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    fun versionCheckWithEmail(userId: Int, token: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val versionRecent = versionRepo.versionApi()

                try {
                    val pInfo = App().context().packageManager.getPackageInfo(App().context().packageName, PackageManager.GET_META_DATA)
                    val versionCurrent = (pInfo.versionName[0].toInt()-48)*10000 + (pInfo.versionName[2].toInt()-48)*100 + (pInfo.versionName[4].toInt()-48)

                    if(versionRecent.isSucceed) {
                        if(versionRecent.contents!!.version <= versionCurrent)
                            getUserInfo(userId, token)
                        else
                            error.postValue(makeCustomErrorResponse(VERSION_ERROR_CODE, "새 버전으로 업데이트해주세요.", ""))
                    } else {
                        error.postValue(versionRecent.error!!)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }

    private fun loginToServer(email: String, type: String) {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val loginResponse = repository.login(email, type)

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
                            isLogined.postValue(false)
                        } else {
                            error.postValue(loginResponse.error!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TagTagTag", "Failed!!!")
                    error.postValue(makeErrorResponseFromStatusCode(API_CONNECT_ERROR_CODE, ""))
                    e.printStackTrace()
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
                    UserManager.alarm = loginResponse.contents!!.data.alarm
                    UserManager.marketing = loginResponse.contents!!.data.marketing
                    if(loginResponse.contents!!.data.call_number.isNotBlank())
                        UserManager.callNumber = loginResponse.contents!!.data.call_number
                    if(loginResponse.contents!!.data.date_of_birth.isNotBlank())
                        UserManager.dateBirth = loginResponse.contents!!.data.date_of_birth
                    if(loginResponse.contents!!.data.gender.isNotBlank())
                        UserManager.gender = loginResponse.contents!!.data.gender
                    if(loginResponse.contents!!.data.nick_name.isNotBlank())
                        UserManager.nickName = loginResponse.contents!!.data.nick_name
                    if(loginResponse.contents!!.data.name.isNotBlank())
                        UserManager.name = loginResponse.contents!!.data.name
                    if(!loginResponse.contents!!.data.profileImage.isNullOrBlank())
                        UserManager.profileImage = loginResponse.contents!!.data.profileImage
                    if(loginResponse.contents!!.data.email.isNotBlank())
                        UserManager.email = loginResponse.contents!!.data.email

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
                            isLogined.postValue(true)
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

    fun loginTest1() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val loginResponse = repository.loginTmp()

                    if (loginResponse.isSucceed) {
                        UserManager.isLogined = true
                        UserManager.email = "hanyang@hyu.ac.kr"
                        UserManager.jwtToken = loginResponse.contents!!.data!!.jwt_token
                        UserManager.userId = loginResponse.contents!!.data!!.user_id
                        getUserInfo(loginResponse.contents!!.data!!.user_id, loginResponse.contents!!.data!!.jwt_token)
                    } else {
                        // 404 : 해당 계정이 없음
                        if(loginResponse.error!!.status == 404) {
                            UserManager.email = "hanyang@hyu.ac.kr"
                            isLogined.postValue(false)
                        } else {
                            error.postValue(loginResponse.error!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TagTagTag", "Failed!!!")
                    error.postValue(makeErrorResponseFromStatusCode(API_CONNECT_ERROR_CODE, ""))
                    e.printStackTrace()
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}