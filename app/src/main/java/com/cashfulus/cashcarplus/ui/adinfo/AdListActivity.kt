package com.cashfulus.cashcarplus.ui.adinfo

import android.os.Bundle
import androidx.lifecycle.Observer
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragmentActivity
import com.cashfulus.cashcarplus.databinding.ActivityAdListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AdListActivity : BaseFragmentActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityAdListBinding>(R.layout.activity_ad_list)
    private val viewModel: AdListViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AdListActivity
            viewModel = this@AdListActivity.viewModel
        }

        // 뒤로가기 버튼 처리
        binding.btnAdListBack.setOnClickListener {
            finish()
        }

        /** LiveData에 대한 반응 셋팅 */
        viewModel.adList.observe(binding.lifecycleOwner!!, Observer {
            /** 광고 리스트의 TabLayout 제목 셋팅 */
            /*binding.vpAdList.adapter = AdListViewPagerAdapter2(this@AdListActivity, it)

            TabLayoutMediator(binding.tlAdList, binding.vpAdList) { tab, position ->
                when(position) {
                    0 -> tab.text = "모집중"
                    1 -> tab.text = "모집예정"
                    2 -> tab.text = "종료"
                    else -> tab.text = ""
                }
            }.attach()*/
        })
    }
}