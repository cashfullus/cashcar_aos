package com.cashfulus.cashcarplus.ui.donation

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityDonationListBinding
import com.cashfulus.cashcarplus.ui.adapter.DonationRecyclerAdapter
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.WithdrawingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DonationListActivity : BaseActivity() {
    private val binding by binding<ActivityDonationListBinding>(R.layout.activity_donation_list)
    private val viewModel: DonationListViewModel by viewModel { parametersOf() }

    val donationId = MutableLiveData<Int>()
    val donationMap = mutableMapOf<Int, String>()
    var point = -1
    var donationStatus = ""

    val donationActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        point = intent.getIntExtra("point", -1)
        donationStatus = intent.getStringExtra("status")!!

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DonationListActivity
            viewModel = this@DonationListActivity.viewModel
        }

        /** 토스트 안내 문구 */
        showToast("이미지를 터치하면 상세 설명이 보입니다")

        /** Toolbar 설정 */
        binding.toolbarDonationList.setLeftOnClick {
            finish()
        }

        /** liveData 설정 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            donationMap.clear()
            for(i in 0 until it.data.size)
                donationMap[it.data[i].id] = it.data[i].name

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
            if(donationStatus == "stand_by") { // 기부신청
                val dialog = WithdrawingDialog(true, false)
                dialog.show(supportFragmentManager, "withdrawing")
            } else if(donationStatus == "confirm") { // 기부중
                val dialog = WithdrawingDialog(true, true)
                dialog.show(supportFragmentManager, "withdrawing")
            } else if(point != -1 && point < 10000) {
                val dialog = PopupDialog("10,000포인트부터 기부가 가능합니다.", "확인", null)
                dialog.show(supportFragmentManager, "Point")
            } else if(point != -1) {
                val intent = Intent(this@DonationListActivity, DonationRegisterActivity::class.java)
                intent.putExtra("point", point)
                intent.putExtra("id", donationId.value!!)
                intent.putExtra("name", donationMap[donationId.value!!])
                donationActivity.launch(intent)
            } else {
                showToast("오류가 발생했습니다. 다시 시도해 주세요.")
                finish()
            }
        }
    }
}