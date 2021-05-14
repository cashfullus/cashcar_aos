package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityEmailLoginBinding
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.util.UserManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EmailLoginActivity : BaseActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@EmailLoginActivity) }
    private val binding by binding<ActivityEmailLoginBinding>(R.layout.activity_email_login)
    private val viewModel: EmailLoginViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@EmailLoginActivity
            viewModel = this@EmailLoginActivity.viewModel
        }

        /** Toolbar 클릭 이벤트 */
        binding.toolbarEmailLogin.setLeftOnClick {
            finish()
        }

        /** 로그인 버튼 클릭 시 */
        binding.btnLoginPassword.setOnClickListener {
            viewModel.loginPW()
        }

        binding.btnLoginFindPW.setOnClickListener {
            showToast("준비중인 기능입니다.")
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                // 로그인 성공 : Main으로 이동
                val mainIntent = Intent(this@EmailLoginActivity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                Log.d("Cashcarplus", "name : "+UserManager.name+"")
                startActivity(mainIntent)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            binding.tvLoginError.visibility = View.VISIBLE
        })
    }
}