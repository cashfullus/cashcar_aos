package com.cashfulus.cashcarplus.base

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.databinding.ActivityTestBinding
import com.cashfulus.cashcarplus.ui.dialog.MissionDialog
import com.cashfulus.cashcarplus.ui.dialog.SpinnerDialog
import com.cashfulus.cashcarplus.ui.dialog.SpinnerDialogClickListener
import com.cashfulus.cashcarplus.ui.user.UserInfoViewModel
import com.cashfulus.cashcarplus.util.GPSUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TestActivity : BaseActivity() {
    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityTestBinding>(R.layout.activity_test)
    private val viewModel: UserInfoViewModel by viewModel { parametersOf() }

    val data = MutableLiveData<String>("TTTTT")

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
            data.postValue("Checked!!!!")
        }

        data.observe(this@TestActivity, {
            showToast(it)

            if(binding.cardCurrentMission.visibility == View.VISIBLE) {
                binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        56f,
                        resources.displayMetrics
                ).toInt()

                binding.cardCurrentMission.visibility = View.GONE
                binding.cardNoneMission.visibility = View.VISIBLE
            } else {
                binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        152f,
                        resources.displayMetrics
                ).toInt()

                binding.cardCurrentMission.visibility = View.VISIBLE
                binding.cardNoneMission.visibility = View.GONE
            }

            binding.usvHome.run {
                header = binding.tlHomeAd
                binding.usvHome.onGlobalLayout()
            }

            // 미션 상태 변경 시 최상단으로 스크롤.
            binding.usvHome.smoothScrollTo(0, 0)
        })
    }
}