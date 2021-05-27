package com.cashfulus.cashcarplus.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.data.service.API_CONNECT_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.VERSION_ERROR_CODE
import com.cashfulus.cashcarplus.databinding.ActivitySplashBinding
import com.cashfulus.cashcarplus.ui.howtouse.HowToUseActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.util.UserManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SplashActivity : BaseActivity() {
    //private lateinit var auth: FirebaseAuth

    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivitySplashBinding>(R.layout.activity_splash)
    private val viewModel: SplashViewModel by viewModel { parametersOf(this@SplashActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@SplashActivity
            viewModel = this@SplashActivity.viewModel
        }

        /** 카카오 로그인 상태 확인 */
        val hd = Handler()
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        //로그인 필요
                        hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 3000)
                    }
                    else {
                        //기타 에러
                        showToast("오류가 발생했습니다. 다시 로그인해주세요.")
                        hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 3000)
                    }
                }
                else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    //이메일 가져옴 -> API 로그인
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            showToast("카카오 로그인 오류 : " + error.message)
                            hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 3000)
                        } else if (user != null) {
                            if(user.kakaoAccount != null && user.kakaoAccount!!.email != null) {
                                viewModel.versionCheckWithAPI(user.kakaoAccount!!.email!!, "kakao")
                            } else {
                                showToast("이메일을 확인할 수 없습니다. 다시 로그인해주세요.")
                                hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 3000)
                            }
                        }
                    }
                }
            }
        }
        else {
            // 이메일 로그인인지 확인
            val userData = getSharedPreferences("userData", MODE_PRIVATE)
            val userId = userData.getInt("userId", -1)
            val jwtToken = userData.getString("jwtToken", "")
            if(userId == -1 || jwtToken == null || jwtToken == "") {
                if(getSharedPreferences("init", MODE_PRIVATE).getBoolean("isFirst", true))
                    hd.postDelayed(splashHandler(Intent(this@SplashActivity, HowToUseActivity::class.java)), 3000)
                else
                    hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 3000)
            } else {
                UserManager.isLogined = true
                UserManager.jwtToken = jwtToken
                UserManager.userId = userId
                viewModel.versionCheckWithEmail(userId, jwtToken)
            }
        }

        viewModel.isLogined.observe(this@SplashActivity, {
            if (it) {
                // 로그인 성공
                hd.postDelayed(splashHandler(Intent(this@SplashActivity, MainActivity::class.java)), 2000)
            } else {
                // 로그인 실패
                hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 2000)
            }
        })

        viewModel.error.observe(this@SplashActivity, {
            showToast(it.message)
            if (it.status == NO_INTERNET_ERROR_CODE || it.status == API_CONNECT_ERROR_CODE || it.status == VERSION_ERROR_CODE)
                finish()
            if(it.status == VERSION_ERROR_CODE) run {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.cashfulus.cashcarplus")))
                } catch (_: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cashfulus.cashcarplus")))
                }
            }
            else
                hd.postDelayed(splashHandler(Intent(this@SplashActivity, LoginActivity::class.java)), 2000)
        })

        // Firebase 관련 설정
        /*auth = Firebase.auth
        auth.setLanguageCode("kr")

        /** Permission 체크 **/
        val permissionListener = object: PermissionListener {
            override fun onPermissionGranted() {
                // 로그인 진행
                viewModel.startLogin("token tmp")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요합니다.")
            .setDeniedMessage("권한이 비허용되었습니다.\n[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()

        /** ViewModel 갱신 시 처리 */
        viewModel.loading.observe(this@SplashActivity, Observer {
            if(it)
                loadingDialog.show()
            else
                loadingDialog.dismiss()
        })

        viewModel.error.observe(this@SplashActivity, Observer {
            Toast.makeText(this@SplashActivity, it.message, Toast.LENGTH_LONG).show()
        })*/
    }

    inner class splashHandler(val intent: Intent): Runnable {
        override fun run() {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@SplashActivity.finish()
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 키 동작 방지
    }
}