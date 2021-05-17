package com.cashfulus.cashcarplus.ui.myactivities

import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMyActivitiesBinding
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyActivitiesActivity : BaseActivity() {
    val loadingDialog: LoadingDialog by inject { parametersOf(this@MyActivitiesActivity) }
    private val binding by binding<ActivityMyActivitiesBinding>(R.layout.activity_my_activities)
    private val viewModel: MyActivitiesViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MyActivitiesActivity
            viewModel = this@MyActivitiesActivity.viewModel
        }
    }
}