package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityRegisterBinding
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.adapter.SpinnerWithHintAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.ui.dialog.ProfileImageDialogClickListener
import com.cashfulus.cashcarplus.util.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/** 이름/이메일/연락처 3개만 필수!!! 나머지는 ""으로 전송해도 문제없다. */

class RegisterActivity : BaseActivity(), ProfileImageDialogClickListener {

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@RegisterActivity) }
    private val binding by binding<ActivityRegisterBinding>(R.layout.activity_register)
    private val viewModel: RegisterViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isNicknameFocused = false
    var isNameFocused = false
    var isEmailFocused = false
    var isPhoneFocused = false
    var isBirthFocused = false
    // Button Validation
    private val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@RegisterActivity
            viewModel = this@RegisterActivity.viewModel
        }

        // 로그인 방식 : kakao, email
        val isAlarmReceive = intent.getBooleanExtra("alarm", false)
        val isMarketingReceive = intent.getBooleanExtra("marketing", false)

        /** Form Validation + 이메일 변경 가능 여부 체크 */
        binding.etRegisterName.hasError = true
        binding.etRegisterPhone.hasError = true
        binding.etRegisterEmail.hasError = true

        binding.etRegisterNickname.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNicknameFocused) {
                isNicknameFocused = true
                binding.etRegisterNickname.getEditText().addTextChangedListener(object:
                    TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s.toString().isEmpty()) {
                            binding.etRegisterNickname.setNoError()
                        } else if(!s.toString().isValidNickname()) {
                            binding.etRegisterNickname.setError("2자~20자 길이로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterNickname.setSuccess("별명 입력 완료")

                            if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError && !binding.etRegisterEmail.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etRegisterName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNameFocused) {
                isNameFocused = true
                binding.etRegisterName.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidName()) {
                            binding.etRegisterName.setError("2자~20자 사이의 한글/영어로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterName.setSuccess("이름 입력 완료")

                            if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError && !binding.etRegisterEmail.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        // Email Form은 정보 미리 넣어두고, 비활성화.
        if(UserManager.email != null) {
            viewModel.email.postValue(UserManager.email)
            binding.etRegisterEmail.isEnabled = false
            binding.etRegisterEmail.hasError = false
        } else {
            binding.etRegisterEmail.isEnabled = true

            binding.etRegisterEmail.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isEmailFocused) {
                    isEmailFocused = true
                    binding.etRegisterEmail.getEditText().addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if(!s.toString().isValidEmail()) {
                                binding.etRegisterEmail.setError("잘못된 이메일 형식입니다.")
                                isAllValid.postValue(false)
                            } else {
                                binding.etRegisterEmail.setSuccess("이메일 입력 완료")

                                if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError)
                                    isAllValid.postValue(true)
                            }
                        }
                    })
                }
            }
        }

        binding.etRegisterPhone.getEditText().addTextChangedListener(
            PhoneNumberFormattingTextWatcher()
        )
        binding.etRegisterPhone.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPhoneFocused) {
                isPhoneFocused = true
                binding.etRegisterPhone.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidPhone()) {
                            binding.etRegisterPhone.setError("잘못된 전화번호 형식입니다.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterPhone.setSuccess("연락처 입력 완료")

                            if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError && !binding.etRegisterEmail.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etRegisterBirth.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isBirthFocused) {
                isBirthFocused = true
                binding.etRegisterBirth.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s.toString().isEmpty()) {
                            binding.etRegisterBirth.setNoError()
                        } else if(!s.toString().isValidBirth()) {
                            binding.etRegisterBirth.setError("6글자의 날짜 형식으로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterBirth.setSuccess("생년월일 입력 완료")

                            if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError && !binding.etRegisterEmail.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        /** Spinner Control */
        val spGenderAdapter = SpinnerWithHintAdapter(this, resources.getStringArray(R.array.gender).toList())
        spGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRegisterGender.adapter = spGenderAdapter
        binding.spRegisterGender.setSelection(spGenderAdapter.count)
        binding.spRegisterGender.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.gender.postValue("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0)
                    viewModel.gender.postValue(GENDER_MALE)
                else
                    viewModel.gender.postValue(GENDER_FEMALE)
            }
        }

        /** 프로필 사진 변경 */
        binding.cvRegister.setOnClickListener {
            //val profileImageDialog = ProfileImageDialog()
            //profileImageDialog.show(supportFragmentManager, "Image")
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                val mainIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
            } else {
                showToast("유저 정보를 업데이트하는 도중 오류가 발생했습니다. 다시 시도해 주세요.")
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        viewModel.loading.observe(binding.lifecycleOwner!!, {
            if(it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        })

        /** '시작하기' 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnRegister.isEnabled = it
            binding.btnRegister.isClickable = it
            binding.btnRegister.isFocusable = it
            binding.btnRegister.isSelected = it
            if(it) {
                binding.btnRegister.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnRegister.typeface = ResourcesCompat.getFont(this@RegisterActivity, R.font.notosanskr_bold)
            } else {
                binding.btnRegister.setTextColor(getColor(R.color.grayscale_400))
                binding.btnRegister.typeface = ResourcesCompat.getFont(this@RegisterActivity, R.font.notosanskr_medium)
            }
        })
        /** '시작하기' 버튼 클릭 이벤트 */
        binding.btnRegister.setOnClickListener {
            if(intent.getStringExtra("method") == "kakao")
                viewModel.registerKakao(isAlarmReceive, isMarketingReceive)
            else
                viewModel.updateUserInfo(isAlarmReceive, isMarketingReceive)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val resultUri: Uri = result.uri

            Glide.with(this@RegisterActivity).load(resultUri).into(binding.ivRegister)
            viewModel.profileImg.postValue(resultUri)

            if(!binding.etRegisterName.hasError && !binding.etRegisterPhone.hasError && !binding.etRegisterEmail.hasError)
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
    }

    /** 사진 촬영 후 profileImageDialog의 interface의 콜백 함수 부분 */
    override fun onClick(image: Bitmap) = binding.ivRegister.setImageBitmap(image)
    override fun onError(msg: String) = showToast(msg)
}