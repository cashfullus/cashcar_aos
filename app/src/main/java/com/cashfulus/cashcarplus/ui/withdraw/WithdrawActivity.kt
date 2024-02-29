package com.cashfulus.cashcarplus.ui.withdraw

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.databinding.ActivityWithdrawBinding
import com.cashfulus.cashcarplus.ui.adapter.SpinnerWithHintAdapter
import com.cashfulus.cashcarplus.ui.adinfo.AddressActivity
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.ui.login.CluaseWebviewActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.util.UserManager
import com.cashfulus.cashcarplus.util.isValidAccount
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class WithdrawActivity : BaseActivity(), PopupDialogClickListener {
    private val binding by binding<ActivityWithdrawBinding>(R.layout.activity_withdraw)
    private val viewModel: WithdrawViewModel by viewModel { parametersOf() }

    var isPointFocused: Boolean = false
    var isAccountFocused: Boolean = false
    var isResidentFocused: Boolean = false
    var isResidentBackFocused: Boolean = false
    var isMainAddressValid = false
    var isDetailAddressFocused = false
    val isAllValid = MutableLiveData<Boolean>(false)

    var allPoint = 0
    val numFormat = DecimalFormat("###,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@WithdrawActivity
            viewModel = this@WithdrawActivity.viewModel
        }

        /** Toolbar 셋팅 */
        binding.toolbarWithdraw.setLeftOnClick {
            finish()
        }

        /** '전액' 버튼 클릭 시 */
        binding.btnWithdrawAllPoint.setOnClickListener {
            binding.etWithdrawPoint.getEditText().setText(allPoint.toString())
            binding.etWithdrawPoint.hasError = false

            if(!binding.etWithdrawAccount.hasError && viewModel.bank.value != null && viewModel.name.value != null && binding.cbWithdrawClause.isChecked)
                isAllValid.postValue(true)
        }

        /** '개인정보 수집 및 이용 동의'로 이동 */
        binding.btnWithdrawClause.setOnClickListener {
            val intent = Intent(this@WithdrawActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("title", "개인정보 수집 및 이용 동의")
            intent.putExtra("url", getString(R.string.url_terms_of_service))
            startActivity(intent)
        }

        /** 이름값 지정 */
        viewModel.name.postValue(UserManager.name!!)

        /** Validation */
        binding.etWithdrawPoint.hasError = true
        binding.etWithdrawPoint.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPointFocused) {
                isPointFocused = true
                binding.etWithdrawPoint.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        // 포인트 입력 조건 : 10000포인트 이상, 1000 단위
                        if(binding.etWithdrawPoint.getEditText().text.isNullOrBlank() || !binding.etWithdrawPoint.getEditText().text.isDigitsOnly() ||
                                binding.etWithdrawPoint.getEditText().text.toString().toInt() < 10000 || binding.etWithdrawPoint.getEditText().text.toString().toInt() > allPoint || binding.etWithdrawPoint.getEditText().text.toString().toInt() % 1000 != 0) {
                            binding.etWithdrawPoint.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        }
                        else {
                            binding.etWithdrawPoint.setSuccessWithoutMsg()

                            if(!binding.etWithdrawAccount.hasError && viewModel.bank.value != null && viewModel.name.value != null && binding.cbWithdrawClause.isChecked)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etWithdrawAccount.hasError = true
        binding.etWithdrawAccount.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isAccountFocused) {
                isAccountFocused = true
                binding.etWithdrawAccount.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!binding.etWithdrawAccount.getEditText().text.toString().isValidAccount()) {
                            binding.etWithdrawAccount.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        }
                        else {
                            binding.etWithdrawAccount.setSuccessWithoutMsg()

                            if(!binding.etWithdrawPoint.hasError && viewModel.bank.value != null && viewModel.name.value != null && binding.cbWithdrawClause.isChecked)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etWithdrawResident.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isResidentFocused) {
                isResidentFocused = true
                binding.etWithdrawResident.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!isValidResidentNumber(binding.etWithdrawResident.getEditText().text.toString())) {
                            binding.etWithdrawResident.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        } else {
                            binding.etWithdrawResident.setSuccessWithoutMsg()

                            if (!binding.etWithdrawResident.hasError && viewModel.bank.value != null && viewModel.name.value != null
                                && binding.cbWithdrawClause.isChecked && viewModel.accountResident.value != null && viewModel.accountResidentBack.value != null
                                && viewModel.mainAddress.value != null && viewModel.detailAddress.value != null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etWithdrawResidentBack.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isResidentBackFocused) {
                isResidentBackFocused = true
                binding.etWithdrawResidentBack.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(viewModel.accountResident.value != binding.etWithdrawResidentBack.getEditText().text.toString()) {
                            binding.etWithdrawResidentBack.setErrorWithoutMsg()
                            isAllValid.postValue(false)
                        } else {
                            binding.etWithdrawResidentBack.setSuccessWithoutMsg()

                            if (!binding.etWithdrawResidentBack.hasError && viewModel.bank.value != null && viewModel.name.value != null
                                && binding.cbWithdrawClause.isChecked && viewModel.accountResident.value != null && viewModel.accountResidentBack.value != null
                                && viewModel.mainAddress.value != null && viewModel.detailAddress.value != null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        /** 주소 검색 버튼 */
        binding.etWithdrawDetailAddress.isEnabled = false
        val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if(activityResult.resultCode == RESULT_OK) {
                isMainAddressValid = true
                binding.tvAddressAddress1.text = activityResult.data!!.getStringExtra("address")
                viewModel.mainAddress.postValue(activityResult.data!!.getStringExtra("address"))
                binding.etWithdrawDetailAddress.isEnabled = true
            }
        }
        binding.etWithdrawMainAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            requestActivity.launch(intent)
        }

        binding.etWithdrawDetailAddress.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isDetailAddressFocused) {
                isDetailAddressFocused = true
                binding.etWithdrawDetailAddress.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(binding.etWithdrawDetailAddress.getEditText().text.length < 2) {
                            binding.etWithdrawDetailAddress.setError("상세 주소를 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etWithdrawDetailAddress.setSuccess("주소 입력 완료")
                            if (!binding.etWithdrawResidentBack.hasError && viewModel.bank.value != null && viewModel.name.value != null
                                && binding.cbWithdrawClause.isChecked && viewModel.accountResident.value != null && viewModel.accountResidentBack.value != null
                                && viewModel.mainAddress.value != null && viewModel.detailAddress.value != null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.cbWithdrawClause.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                if(!binding.etWithdrawPoint.hasError && !binding.etWithdrawAccount.hasError && viewModel.bank.value != null && viewModel.name.value != null)
                    isAllValid.postValue(true)
            } else {
                isAllValid.postValue(false)
            }
        }

        /** Spinner */
        val spBankAdapter = SpinnerWithHintAdapter(this, resources.getStringArray(R.array.bank).toList())
        spBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spWithdrawBank.adapter = spBankAdapter
        binding.spWithdrawBank.setSelection(spBankAdapter.count)
        binding.spWithdrawBank.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.bank.postValue("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.bank.postValue(resources.getStringArray(R.array.bank)[position])
            }
        }

        /** '출금 신청하기' 버튼 */
        isAllValid.observe(binding.lifecycleOwner!!) {
            binding.btnWithdraw.isSelected = it
            binding.btnWithdraw.isEnabled = it
            binding.btnWithdraw.isFocusable = it
            binding.btnWithdraw.isClickable = it

            if (it) {
                binding.btnWithdraw.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnWithdraw.typeface =
                    ResourcesCompat.getFont(this@WithdrawActivity, R.font.notosanskr_bold)
            } else {
                binding.btnWithdraw.setTextColor(getColor(R.color.grayscale_400))
                binding.btnWithdraw.typeface =
                    ResourcesCompat.getFont(this@WithdrawActivity, R.font.notosanskr_medium)
            }
        }
        binding.btnWithdraw.setOnClickListener {
            val dialog = PopupDialog("출금을 신청한 후에는 금액의 변경 또는\n" + "취소가 불가능합니다. ", "확인", "취소")
            dialog.show(supportFragmentManager, "Withdraw")
        }

        /** LiveData 처리 */
        viewModel.bankData.observe(binding.lifecycleOwner!!) {
            allPoint = it.deposit
            binding.tvWithdrawAllPoint.text = numFormat.format(allPoint) + " P"

            if (it.accountBank != "" && it.account != "") {
                binding.etWithdrawAccount.hasError = false

                viewModel.bank.postValue(it.accountBank)
                viewModel.account.postValue(it.account)
                viewModel.name.postValue(it.name)

                when (it.accountBank) {
                    "경남은행" -> {
                        binding.spWithdrawBank.setSelection(0)
                    }
                    "광주은행" -> {
                        binding.spWithdrawBank.setSelection(1)
                    }
                    "국민은행" -> {
                        binding.spWithdrawBank.setSelection(2)
                    }
                    "기업은행" -> {
                        binding.spWithdrawBank.setSelection(3)
                    }
                    "농협은행" -> {
                        binding.spWithdrawBank.setSelection(4)
                    }
                    "대구은행" -> {
                        binding.spWithdrawBank.setSelection(5)
                    }
                    "부산은행" -> {
                        binding.spWithdrawBank.setSelection(6)
                    }
                    "산림조합중앙회" -> {
                        binding.spWithdrawBank.setSelection(7)
                    }
                    "산업은행" -> {
                        binding.spWithdrawBank.setSelection(8)
                    }
                    "새마을금고" -> {
                        binding.spWithdrawBank.setSelection(9)
                    }
                    "수협은행" -> {
                        binding.spWithdrawBank.setSelection(10)
                    }
                    "신한은행" -> {
                        binding.spWithdrawBank.setSelection(11)
                    }
                    "신협중앙회" -> {
                        binding.spWithdrawBank.setSelection(12)
                    }
                    "우리은행" -> {
                        binding.spWithdrawBank.setSelection(13)
                    }
                    "우체국" -> {
                        binding.spWithdrawBank.setSelection(14)
                    }
                    "저축은행" -> {
                        binding.spWithdrawBank.setSelection(15)
                    }
                    "전북은행" -> {
                        binding.spWithdrawBank.setSelection(16)
                    }
                    "제주은행" -> {
                        binding.spWithdrawBank.setSelection(17)
                    }
                    "카카오뱅크" -> {
                        binding.spWithdrawBank.setSelection(18)
                    }
                    "케이뱅크" -> {
                        binding.spWithdrawBank.setSelection(19)
                    }
                    "하나은행" -> {
                        binding.spWithdrawBank.setSelection(20)
                    }
                    "한국씨티은행" -> {
                        binding.spWithdrawBank.setSelection(21)
                    }
                    "SC제일은행" -> {
                        binding.spWithdrawBank.setSelection(22)
                    }
                }
            }
        }

        viewModel.isSucceed.observe(binding.lifecycleOwner!!) {
            if (it) {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.error.observe(binding.lifecycleOwner!!) {
            showToast(it.message)

            if (it.status == LOST_USER_INFO_ERROR_CODE) {
                val intent = Intent(this@WithdrawActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@WithdrawActivity.finish()
            } else if (it.status == NO_INTERNET_ERROR_CODE) {
                finish()
            }
        }
    }

    fun isValidResidentNumber(residentNumber: String): Boolean {
        val regex = Regex("""^\d{6}-\d{7}$""")

        if (!regex.matches(residentNumber)) {
            return false
        }

        val cleanedResidentNumber = residentNumber.replace("-", "")

        // 총 길이 확인
        if (cleanedResidentNumber.length != 13) {
            return false
        }

        val birthDate = cleanedResidentNumber.substring(0, 6)
        val personalCode = cleanedResidentNumber.substring(6, 13)

        // 생년월일 유효성 검사
        if (!isValidDate(birthDate)) {
            return false
        }

        // 성별 유효성 검사
        val genderDigit = cleanedResidentNumber[6].toString().toInt()
        if (genderDigit !in listOf(1, 2, 3, 4)) {
            return false
        }

        // 검증 숫자 확인
        val checkDigit = cleanedResidentNumber[12].toString().toInt()
        if (!isValidCheckDigit(cleanedResidentNumber.substring(0, 12), checkDigit)) {
            return false
        }

        return true
    }

    fun isValidDate(birthDate: String): Boolean {
        val year = birthDate.substring(0, 2).toInt()
        val month = birthDate.substring(2, 4).toInt()
        val day = birthDate.substring(4, 6).toInt()

        // 간단한 윤년 검사 (1900년부터 2099년까지만 고려)
        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

        if (month < 1 || month > 12 || day < 1 || day > 31) {
            return false
        }

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return day <= 30
        } else if (month == 2) {
            return if (isLeapYear) day <= 29 else day <= 28
        }

        return true
    }

    // 검증 숫자 확인
    fun isValidCheckDigit(baseNumber: String, checkDigit: Int): Boolean {
        val weights = intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5)
        var sum = 0

        for (i in 0 until baseNumber.length) {
            sum += baseNumber[i].toString().toInt() * weights[i]
        }

        val remainder = sum % 11
        val result = if (remainder == 0) 1 else 11 - remainder

        return result == checkDigit
    }

    override fun onPositive() {
        viewModel.registerWithdraw()
    }

    override fun onNegative() {}
}