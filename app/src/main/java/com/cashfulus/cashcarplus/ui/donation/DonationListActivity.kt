package com.cashfulus.cashcarplus.ui.donation

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityDonationListBinding
import com.cashfulus.cashcarplus.ui.adapter.DonationRecyclerAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DonationListActivity : BaseActivity() {

    val loadingDialog: LoadingDialog by inject { parametersOf(this@DonationListActivity) }
    private val binding by binding<ActivityDonationListBinding>(R.layout.activity_donation_list)
    private val viewModel: DonationListViewModel by viewModel { parametersOf() }

    val donationId = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DonationListActivity
            viewModel = this@DonationListActivity.viewModel
        }

        /** Toolbar 설정 */
        binding.toolbarDonationList.setLeftOnClick {
            finish()
        }

        /** liveData 설정 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            binding.rvDonationList.adapter = DonationRecyclerAdapter(this@DonationListActivity, it.data, supportFragmentManager, donationId)
            binding.rvDonationList.layoutManager = LinearLayoutManager(this@DonationListActivity)
        })

        donationId.observe(binding.lifecycleOwner!!, {
            if(donationId.value != null) {
                binding.btnDonate.isSelected = true
                binding.btnDonate.isEnabled = true
                binding.btnDonate.isFocusable = true
                binding.btnDonate.isClickable = true
                binding.btnDonate.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnDonate.typeface = ResourcesCompat.getFont(this@DonationListActivity, R.font.notosanskr_bold)
            }
        })

        /** '기부 신청하기' 버튼 클릭 시 */
        binding.btnDonate.setOnClickListener {
            showToast("제작 중입니다. id : "+donationId.value!!.toString())
        }
    }
}