package com.cashfulus.cashcarplus.ui.point

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.databinding.ActivityPointBinding
import com.cashfulus.cashcarplus.model.PointHistoryRow
import com.cashfulus.cashcarplus.ui.adapter.PointHistoryAdapter
import com.cashfulus.cashcarplus.ui.dialog.*
import com.cashfulus.cashcarplus.ui.donation.DonationListActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.ui.withdraw.WithdrawActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class PointActivity : BaseActivity(), PointBottomDialogClickListener, PopupDialogClickListener {
    private val binding by binding<ActivityPointBinding>(R.layout.activity_point)
    private val viewModel: PointViewModel by viewModel { parametersOf() }

    val numFormat = DecimalFormat("###,###")
    var currentPoint = 0
    var withdrawStatus = ""
    var donationStatus = ""

    val withdrawActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        //viewModel.loadPointInfoAll(1)
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
        binding.btnPointWithdraw.setOnClickListener {
            // "is_ongoing_point": "stand_by"인 경우 출금이 진행중인 상황.
            if(withdrawStatus == "stand_by") { //출금신청
                val dialog = WithdrawingDialog(false, false)
                dialog.show(supportFragmentManager, "withdrawing")
            } else if(withdrawStatus == "confirm") { //출금중
                val dialog = WithdrawingDialog(false, true)
                dialog.show(supportFragmentManager, "withdrawing")
            } else if(currentPoint >= 10000) { //출금가능
                val intent = Intent(this@PointActivity, WithdrawActivity::class.java)
                intent.putExtra("point", currentPoint)
                withdrawActivity.launch(intent)
            } else { //출금 불가능
                val dialog = PopupDialog("10,000포인트부터 출금이 가능합니다.", "확인", null)
                dialog.show(supportFragmentManager, "Point")
            }
        }

        binding.btnPointDonation.setOnClickListener {
            val intent = Intent(this@PointActivity, DonationListActivity::class.java)
            intent.putExtra("status", donationStatus)
            intent.putExtra("point", currentPoint)
            withdrawActivity.launch(intent)
        }

        binding.spPointHistory.setOnClickListener {
            val pointBottomDialog = PointBottomDialog()
            pointBottomDialog.show(supportFragmentManager, "Point")
        }

        /** LiveData 셋팅 */
        viewModel.pointData.observe(binding.lifecycleOwner!!, {
            currentPoint = it.userPoint

            binding.tvPointAll.text = numFormat.format(it.userPoint) + " P"
            binding.tvPointExpected.text = numFormat.format(it.scheduledPoint) + " P"

            binding.rvPointHistory.adapter = PointHistoryAdapter(this@PointActivity, it.pointHistory)
            binding.rvPointHistory.layoutManager = LinearLayoutManager(this@PointActivity)

            // "is_ongoing_point": "stand_by"인 경우 출금이 진행중인 상황. 반대로 ""인 경우 출금이 진행중이지 않거나 완료된 것.
            withdrawStatus = it.isOngoingPoint
            donationStatus = it.isOngoingDonate
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)

            // 유저 정보 손실 시 : 로그인 화면으로 보냄, Intent Stack 초기화
            if(it.status == LOST_USER_INFO_ERROR_CODE) {
                val intent = Intent(this@PointActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@PointActivity.finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPointInfoAll(1)
    }

    override fun onClick(category: String) {
        when(category) {
            "all" -> {
                binding.spPointHistory.text = "전체"
            }
            "positive" -> {
                binding.spPointHistory.text = "적립"
            }
            "negative" -> {
                binding.spPointHistory.text = "출금"
            }
            "donate" -> {
                binding.spPointHistory.text = "기부"
            }
        }

        if(category == "all")
            viewModel.loadPointInfoAll(1)
        else
            viewModel.loadPointInfoCategory(category, 1)
    }

    override fun onPositive() {}
    override fun onNegative() {}
}