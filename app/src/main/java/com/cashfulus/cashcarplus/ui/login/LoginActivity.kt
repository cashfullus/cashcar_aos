package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityLoginBinding
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.util.UserManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LoginActivity : BaseActivity() {
    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityLoginBinding>(R.layout.activity_login)
    private val viewModel: LoginViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@LoginActivity
            viewModel = this@LoginActivity.viewModel
        }

        // OnClick Event
        binding.btnLoginLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity, EmailLoginActivity::class.java))
        }

        binding.btnLoginSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, ClauseActivity::class.java)
            intent.putExtra("method", "normal")
            startActivity(intent)
        }

        binding.btnLoginKakao.setOnClickListener {
            showToast("준비중입니다.")
            //viewModel.kakaoLogin(this@LoginActivity)
        }

        binding.btnLoginApple.setOnClickListener {
            showToast("준비중입니다.")
        }

        binding.btnLoginAskAd.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.cashcar_kakao_channel))))
        }

        /** LiveData 셋팅 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                // 로그인 성공 : Main으로 이동
                val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
            } else {
                // 로그인 실패 : 약관창으로 이동
                val intent = Intent(this@LoginActivity, ClauseActivity::class.java)
                intent.putExtra("method", "kakao")
                intent.putExtra("email", UserManager.email!!)
                startActivity(intent)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })
    }
}