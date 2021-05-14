package com.cashfulus.cashcarplus.ui

import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.databinding.FragmentDrivingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DrivingFragment : BaseFragment<FragmentDrivingBinding, DrivingViewModel>(R.layout.fragment_driving) {
    override val viewModel: DrivingViewModel by viewModel { parametersOf() }

    override fun init() {

    }
}