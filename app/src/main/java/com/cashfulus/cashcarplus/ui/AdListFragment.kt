package com.cashfulus.cashcarplus.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.databinding.FragmentAdListBinding
import com.cashfulus.cashcarplus.ui.adapter.AdRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARG_AD_LIST = "tabState"

class AdListFragment : BaseFragment<FragmentAdListBinding, AdListViewModel>(R.layout.fragment_ad_list) {
    private var tabState: String? = null
    override val viewModel: AdListViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            tabState = it.getString(ARG_AD_LIST)
        }
    }

    override fun init() {
        binding.rvAdList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = AdRecyclerAdapter(requireActivity())
        binding.rvAdList.adapter = adapter

        viewModel.loadAdList(tabState!!)
        viewModel.adList.observe(binding.lifecycleOwner!!, {
            if(it.size == 0) {
                binding.ivAdListEmpty.visibility = View.VISIBLE
                binding.rvAdList.visibility = View.GONE
            } else {
                binding.ivAdListEmpty.visibility = View.GONE
                binding.rvAdList.visibility = View.VISIBLE
                adapter.refresh(it, tabState!!)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(tabState: String) =
                AdListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_AD_LIST, tabState)
                    }
                }
    }
}