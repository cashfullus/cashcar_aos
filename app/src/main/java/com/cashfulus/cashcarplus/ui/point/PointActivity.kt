package com.cashfulus.cashcarplus.ui.point

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityPointBinding
import com.cashfulus.cashcarplus.ui.adapter.PointHistoryAdapter
import com.cashfulus.cashcarplus.ui.adapter.TmpData
import com.cashfulus.cashcarplus.ui.donation.DonationListActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PointActivity : BaseActivity() {
    private val binding by binding<ActivityPointBinding>(R.layout.activity_point)
    private val viewModel: PointViewModel by viewModel { parametersOf() }

    val donationActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            val arrayList = ArrayList<TmpData>()
            arrayList.add(TmpData("초록우산 어린이재단 기부", "2021.05.26 11:28:08", -30000))

            binding.rvPointHistory.adapter = PointHistoryAdapter(this@PointActivity, arrayList)
            binding.rvPointHistory.layoutManager = LinearLayoutManager(this@PointActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@PointActivity
            viewModel = this@PointActivity.viewModel
        }

        /** Toolbar 셋팅 */
        binding.toolbarPoint.setLeftOnClick {
            finish()
        }

        /** View 셋팅 */
        binding.btnPointDonation.setOnClickListener {
            donationActivity.launch(Intent(this@PointActivity, DonationListActivity::class.java))
        }
    }
}