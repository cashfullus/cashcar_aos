package com.cashfulus.cashcarplus.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.databinding.FragmentCashcartipBinding
import com.cashfulus.cashcarplus.ui.adapter.CashcartipRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CashcartipFragment : BaseFragment<FragmentCashcartipBinding, CashcartipViewModel>(R.layout.fragment_cashcartip) {
    override val viewModel: CashcartipViewModel by viewModel { parametersOf() }

    override fun init() {
        /** 최초 데이터 로딩 실행 */
        viewModel.loadCashcarTipList(1) // default 1

        /** LiveData 처리 */
        viewModel.firstData.observe(binding.lifecycleOwner!!, {
            val adapter = CashcartipRecyclerAdapter(requireActivity(), it)
            binding.rvCashcartipList.adapter = adapter
            binding.rvCashcartipList.layoutManager = LinearLayoutManager(requireActivity())
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        /** 전체 리스트 Refresh */
        binding.srlCashcartip.setOnRefreshListener {
            viewModel.loadCashcarTipList(1)
            binding.srlCashcartip.isRefreshing = false
        }
    }
}