package com.cashfulus.cashcarplus.ui.point

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMyActivitiesBinding
import com.cashfulus.cashcarplus.databinding.ActivityPointBinding
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.ui.myactivities.MyActivitiesViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PointActivity : BaseActivity() {
    val loadingDialog: LoadingDialog by inject { parametersOf(this@PointActivity) }
    private val binding by binding<ActivityPointBinding>(R.layout.activity_point)
    private val viewModel: PointViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@PointActivity
            viewModel = this@PointActivity.viewModel
        }
    }
}