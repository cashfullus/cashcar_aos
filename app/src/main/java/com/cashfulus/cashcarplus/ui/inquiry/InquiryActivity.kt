package com.cashfulus.cashcarplus.ui.inquiry

import android.content.DialogInterface
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityInquiryBinding
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.util.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InquiryActivity : BaseActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@InquiryActivity) }
    private val binding by binding<ActivityInquiryBinding>(R.layout.activity_inquiry)
    private val viewModel: InquiryViewModel by viewModel { parametersOf() }

    // 해당 EditText가 Focus된 적이 있는지 여부.
    var isCompanyFocused = false
    var isStaffNameFocused = false
    var isPhoneFocused = false
    var isEmailFocused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@InquiryActivity
            viewModel = this@InquiryActivity.viewModel
        }

        /** Toolbar 처리 */
        binding.toolbarInquiry.setLeftOnClick {
            finish()
        }

        /** LiveData 처리 **/
        viewModel.response.observe(binding.lifecycleOwner!!, Observer {
            if(it)
                showToast("전송에 성공했습니다. 빠르게 처리 후 답변드리겠습니다!!")
            else
                showToast("원인 불명의 오류가 발생했습니다. 다시 시도해 주세요.")
            finish()
        })

        viewModel.error.observe(binding.lifecycleOwner!!, Observer {
            showToast(it.message)
        })

        /*viewModel.loading.observe(binding.lifecycleOwner!!, Observer {
            if(it)
                loadingDialog.show()
            else
                loadingDialog.dismiss()
        })*/

        /** Validation 처리 (여기서는 오류가 발생해도 오류 메세지는 출력하지 않음. 왜냐면 오류 메세지 출력 시 아래에 나오는 오류메세지 때문에 UI가 깨질 수 있기 때문.) */
        binding.etInquiryCompany.hasError = true
        binding.etInquiryCompany.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isCompanyFocused) {
                isCompanyFocused = true
                binding.etInquiryCompany.validateWithoutMsg { s -> s.isValidBrand() }
            }
        }

        binding.etInquiryStaffName.hasError = true
        binding.etInquiryStaffName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isStaffNameFocused) {
                isStaffNameFocused = true
                binding.etInquiryStaffName.validateWithoutMsg { s -> s.isValidName() }
            }
        }

        binding.etInquiryPhone.hasError = true
        binding.etInquiryPhone.getEditText().addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.etInquiryPhone.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPhoneFocused) {
                isPhoneFocused = true
                binding.etInquiryPhone.validateWithoutMsg { s -> s.isValidPhone() }
            }
        }

        binding.etInquiryEmail.hasError = true
        binding.etInquiryEmail.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isEmailFocused) {
                isEmailFocused = true
                binding.etInquiryEmail.validateWithoutMsg { s -> s.isValidEmail() }
            }
        }

        /** '문의하기' 버튼 클릭 시 */
        binding.btnInquirySubmit.setOnClickListener {
            if(binding.etInquiryCompany.hasError)
                showToast("업체명은 2글자 이상 20자 이하로 입력해주세요.")
            else if(binding.etInquiryStaffName.hasError)
                showToast("담당자 성함은 2글자 이상 20자 이하로 입력해주세요.")
            else if(binding.etInquiryPhone.hasError)
                showToast("연락처를 입력해주세요.")
            else if(binding.etInquiryEmail.hasError)
                showToast("이메일을 입력해주세요.")
            else if(binding.etInquiryContents.text.length < 10)
                showToast("광고 문의 내용을 10글자 이상 입력해주세요.")
            else {
                val builder = AlertDialog.Builder(this@InquiryActivity)
                builder.setMessage("문의 하시겠습니까?")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int -> viewModel.submitInquiry() }
                builder.setNegativeButton("취소", null)
                builder.create().show()
            }
        }
    }
}