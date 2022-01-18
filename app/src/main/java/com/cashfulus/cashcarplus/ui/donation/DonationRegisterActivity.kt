package com.cashfulus.cashcarplus.ui.donation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityDonationRegisterBinding
import com.cashfulus.cashcarplus.ui.adapter.SpinnerWithHintAdapter
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.ui.login.CluaseWebviewActivity
import com.cashfulus.cashcarplus.util.isValidName
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class DonationRegisterActivity : BaseActivity(), PopupDialogClickListener {
    private val binding by binding<ActivityDonationRegisterBinding>(R.layout.activity_donation_register)
    private val viewModel: DonationRegisterViewModel by viewModel { parametersOf() }

    var organizationId = -1;
    var currentPoint = 0
    val numFormat = DecimalFormat("###,###")

    var isPointFocused: Boolean = false
    var isNameFocused: Boolean = false
    val isAllValid = MutableLiveData<Boolean>(false)

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

        /** 현재 포인트 정보를 얻어옴 */
        currentPoint = intent.getIntExtra("point", -1)
        binding.tvDonationAllPoint.text = numFormat.format(currentPoint)+"   P"

        /** 현재 단체 정보를 얻어옴 */
        organizationId = intent.getIntExtra("id", -1)
        if(organizationId == -1) {
            showToast("오류가 발생했습니다. 다시 시도해 주세요.")
            finish()
        }
        binding.etDonationRName.getEditText().setText(intent.getStringExtra("name"))

        /** '전액' 버튼 */
        binding.btnDonationRAllPoint.setOnClickListener {
            viewModel.point.postValue(currentPoint.toString())
        }

        /** Validation */
        binding.etDonationPoint.hasError = true
        binding.etDonationPoint.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPointFocused) {
                isPointFocused = true
                binding.etDonationPoint.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        // 포인트 입력 조건 : 10000포인트 이상, 1000 단위
                        if(binding.etDonationPoint.getEditText().text.isNullOrBlank() || !binding.etDonationPoint.getEditText().text.isDigitsOnly() ||
                                binding.etDonationPoint.getEditText().text.toString().toInt() < 10000 || binding.etDonationPoint.getEditText().text.toString().toInt() > currentPoint || binding.etDonationPoint.getEditText().text.toString().toInt() % 1000 != 0) {
                            binding.etDonationPoint.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        }
                        else {
                            binding.etDonationPoint.setSuccessWithoutMsg()

                            if(!binding.etDonationUserName.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etDonationUserName.hasError = true
        binding.etDonationUserName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNameFocused) {
                isNameFocused = true
                binding.etDonationUserName.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!binding.etDonationUserName.getEditText().text.toString().isValidName()) {
                            binding.etDonationUserName.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        }
                        else {
                            binding.etDonationUserName.setSuccessWithoutMsg()

                            if(!binding.etDonationPoint.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.tvDonationRTitle8.setOnClickListener {
            val intent = Intent(this@DonationRegisterActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("title", "개인정보 제3자 제공")
            intent.putExtra("url", getString(R.string.url_provide_policy))
            startActivity(intent)
        }

        binding.btnDonationR.setOnClickListener {
            val dialog = PopupDialog("기부를 신청한 후에는 금액의 변경 또는\n" + "취소가 불가능합니다.", "확인", "취소")
            dialog.show(supportFragmentManager, "Donation")
        }

        /** LiveData */
        viewModel.isSucceed.observe(binding.lifecycleOwner!!, {
            if(it) {
                setResult(RESULT_OK)
                finish()
            }
        })
    }

    override fun onPositive() {
        viewModel.registerDonation(organizationId)
    }

    override fun onNegative() {}
}