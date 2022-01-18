package com.cashfulus.cashcarplus.ui.user

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityUserAddressBinding
import com.cashfulus.cashcarplus.ui.adinfo.AddressActivity
import com.cashfulus.cashcarplus.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserAddressActivity : BaseActivity() {
    private val binding by binding<ActivityUserAddressBinding>(R.layout.activity_user_address)
    private val viewModel: UserAddressViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isNameFocused = false
    var isPhoneFocused = false
    var isAddress2Focused = false
    // '신청하기' 버튼 활성화 여부
    var isAddress1Valid = false
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@UserAddressActivity
            viewModel = this@UserAddressActivity.viewModel
        }

        /** 툴바 버튼 이벤트 */
        binding.toolbarAddress.setLeftOnClick {
            finish()
        }

        /** 배송지 주소 검색 버튼 */
        binding.etAddressAddress2.isEnabled = false
        val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if(activityResult.resultCode == RESULT_OK) {
                isAddress1Valid = true
                binding.tvAddressAddress1.text = activityResult.data!!.getStringExtra("address")
                viewModel.mainAddress.postValue(activityResult.data!!.getStringExtra("address"))
                binding.etAddressAddress2.isEnabled = true
            }
        }
        binding.etAddressAddress1.setOnClickListener {
            val intent = Intent(this@UserAddressActivity, AddressActivity::class.java)
            requestActivity.launch(intent)
        }

        /** EditText 정규식 설정 (최초 Focus 시 정규식을 지정함) */
        binding.etAddressName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNameFocused) {
                isNameFocused = true
                binding.etAddressName.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidName()) {
                            binding.etAddressName.setError("2자-20자 사이의 한글/영어 조합으로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etAddressName.setSuccess("수령인 입력 완료")
                            if(isAddress1Valid && !binding.etAddressName.hasError && !binding.etAddressPhone.hasError && !binding.etAddressAddress2.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etAddressPhone.getEditText().addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.etAddressPhone.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPhoneFocused) {
                isPhoneFocused = true
                binding.etAddressPhone.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidPhone()) {
                            binding.etAddressPhone.setError("올바른 전화번호 형식으로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etAddressPhone.setSuccess("연락처 입력 완료")
                            if(isAddress1Valid && !binding.etAddressName.hasError && !binding.etAddressPhone.hasError && !binding.etAddressAddress2.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etAddressAddress2.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isAddress2Focused) {
                isAddress2Focused = true
                binding.etAddressAddress2.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(binding.etAddressAddress2.getEditText().text.length < 2) {
                            binding.etAddressAddress2.setError("상세 주소를 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etAddressAddress2.setSuccess("주소 입력 완료")
                            if(isAddress1Valid && !binding.etAddressName.hasError && !binding.etAddressPhone.hasError && !binding.etAddressAddress2.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        /** LiveData 처리 */
        viewModel.data.observe(binding.lifecycleOwner!!, {
            if(it.name.isNotBlank())
                binding.etAddressName.getEditText().setText(it.name)
            else
                binding.etAddressName.hasError = true

            if(it.callNumber.isNotBlank())
                binding.etAddressPhone.getEditText().setText(it.callNumber)
            else
                binding.etAddressPhone.hasError = true

            if(it.mainAddress.isNotBlank()) {
                binding.tvAddressAddress1.text = it.mainAddress
                binding.etAddressAddress2.isEnabled = true
                binding.etAddressAddress2.getEditText().setText(it.detailAddress)

                isAddress1Valid = true
                isAllValid.postValue(true)
                binding.btnAddress.text = "수정하기"
            } else {
                isAddress1Valid = false
                binding.etAddressAddress2.hasError = true
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                /*val logoutDialog = AlertDialog.Builder(this)
                logoutDialog.setMessage("배송지 등록이 완료되었습니다.")
                logoutDialog.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }

                logoutDialog.show()
                binding.btnAddress.text = "수정하기"*/
                finish()
                showToast("배송지 정보를 수정했습니다.")
            }
        })

        /** 등록하기 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnAddress.isSelected = it
            binding.btnAddress.isEnabled = it
            binding.btnAddress.isFocusable = it
            binding.btnAddress.isClickable = it

            if(it) {
                binding.btnAddress.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnAddress.typeface = ResourcesCompat.getFont(this@UserAddressActivity, R.font.notosanskr_bold)
            } else {
                binding.btnAddress.setTextColor(getColor(R.color.grayscale_400))
                binding.btnAddress.typeface = ResourcesCompat.getFont(this@UserAddressActivity, R.font.notosanskr_medium)
            }
        })

        /** 등록하기 버튼 이벤트 */
        binding.btnAddress.setOnClickListener {
            viewModel.postAddressInfo()
        }
    }
}