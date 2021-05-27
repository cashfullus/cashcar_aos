package com.cashfulus.cashcarplus.ui.donation

import android.content.Intent
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityDonationRegisterBinding
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DonationRegisterActivity : BaseActivity(), PopupDialogClickListener {
    private val binding by binding<ActivityDonationRegisterBinding>(R.layout.activity_donation_register)
    private val viewModel: DonationRegisterViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DonationRegisterActivity
            viewModel = this@DonationRegisterActivity.viewModel
        }

        /** Toolbar 설정 */
        binding.toolbarDonationRegister.setLeftOnClick {
            finish()
        }

        binding.tvDonationRTitle8.setOnClickListener {
            startActivity(Intent(this@DonationRegisterActivity, DonationPrivacyActivity::class.java))
        }

        binding.btnDonationR.setOnClickListener {
            val dialog = PopupDialog("기부를 신청한 후에는 금액의 변경 또는\n" + "취소가 불가능합니다.", "확인", "취소")
            dialog.show(supportFragmentManager, "Donation")
        }
    }

    override fun onPositive() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onNegative() {}
}