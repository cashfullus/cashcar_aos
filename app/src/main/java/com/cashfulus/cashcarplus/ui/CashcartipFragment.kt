package com.cashfulus.cashcarplus.ui

import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.databinding.FragmentCashcartipBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CashcartipFragment : BaseFragment<FragmentCashcartipBinding, CashcartipViewModel>(R.layout.fragment_cashcartip) {
    override val viewModel: CashcartipViewModel by viewModel { parametersOf() }

    override fun init() {

    }
}