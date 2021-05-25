package com.cashfulus.cashcarplus.base

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.databinding.ActivityTestBinding
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.ui.dialog.MissionDialog
import com.cashfulus.cashcarplus.ui.dialog.SpinnerDialog
import com.cashfulus.cashcarplus.ui.dialog.SpinnerDialogClickListener
import com.cashfulus.cashcarplus.ui.user.UserInfoViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TestActivity : BaseActivity(), SpinnerDialogClickListener {
    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@TestActivity) }
    private val binding by binding<ActivityTestBinding>(R.layout.activity_test)
    private val viewModel: UserInfoViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@TestActivity
            viewModel = this@TestActivity.viewModel
        }

        binding.btnTest.setOnClickListener {
            val spinnerDialog = SpinnerDialog(resources.getStringArray(R.array.korean_company))
            spinnerDialog.show(supportFragmentManager, "")
        }

        binding.spTest.setOnClickListener {
            val spinnerDialog = SpinnerDialog(resources.getStringArray(R.array.foreign_company))
            spinnerDialog.show(supportFragmentManager, "")
        }
    }

    private val mCountDown: CountDownTimer = object : CountDownTimer(5250, 500) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            loadingDialog.dismiss()
        }
    }

    override fun onSelected(str: String) {
        binding.spTest.text = str
    }
}