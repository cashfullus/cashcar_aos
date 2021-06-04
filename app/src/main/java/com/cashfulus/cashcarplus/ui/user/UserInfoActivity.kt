package com.cashfulus.cashcarplus.ui.user

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityUserInfoBinding
import com.cashfulus.cashcarplus.ui.adapter.SpinnerWithHintAdapter
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.ui.dialog.ProfileImageDialogClickListener
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


/** 이름/이메일/연락처 3개만 필수!!! 나머지는 ""으로 전송해도 문제없다. */

class UserInfoActivity : BaseActivity(), ProfileImageDialogClickListener, PopupDialogClickListener {
    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityUserInfoBinding>(R.layout.activity_user_info)
    private val viewModel: UserInfoViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isNicknameFocused = false
    var isNameFocused = false
    var isPhoneFocused = false
    var isBirthFocused = false
    // '신청하기' 버튼 활성화 여부
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@UserInfoActivity
            viewModel = this@UserInfoActivity.viewModel
        }

        /** 권한 확인 */
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                showToast("카메라 관련 권한이 거부되었습니다.")
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("필수 권한 거부 시 앱 이용이 어려울 수 있습니다.\n\n[설정] > [권한]에서 필수 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) //, Manifest.permission.READ_PHONE_STATE
                .check()

        /** 툴바 버튼 클릭 이벤트 */
        binding.btnUserInfoBack.setOnClickListener {
            finish()
        }
        binding.btnUserInfoSubmit.setOnClickListener {
            viewModel.updateUserInfo()
            //showToast(viewModel.nickname.value!!+ "\n" + viewModel.name.value!! + "\n" + viewModel.email.value!! + "\n" + viewModel.phone.value!! + "\n" + viewModel.gender.value!! + "\n" + viewModel.birth.value!! + "\n" + viewModel.receiveAlarm.value!!.toString() + " " + viewModel.receiveMarketing.value!!.toString())
        }
        /** 툴바 체크버튼 Validation */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnUserInfoSubmit.isSelected = it
            binding.btnUserInfoSubmit.isEnabled = it
            binding.btnUserInfoSubmit.isFocusable = it
            binding.btnUserInfoSubmit.isClickable = it
        })

        /** 성별 Spinner 설정 */
        val spGenderAdapter = SpinnerWithHintAdapter(this, resources.getStringArray(R.array.gender).toList())
        spGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spUserInfoGender.adapter = spGenderAdapter
        binding.spUserInfoGender.setSelection(spGenderAdapter.count)
        /** 데이터 받아오기 */
        viewModel.gender.observe(binding.lifecycleOwner!!, {
            when (it) {
                GENDER_MALE -> binding.spUserInfoGender.setSelection(0)
                GENDER_FEMALE -> binding.spUserInfoGender.setSelection(1)
            }
        })
        viewModel.originImg.observe(binding.lifecycleOwner!!, {
            Glide.with(this@UserInfoActivity).load(it).into(binding.ivUserInfo)
        })

        /** Form Validation + 이메일 변경 가능 여부 체크 */
        binding.etUserInfoNickname.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNicknameFocused) {
                isNicknameFocused = true
                binding.etUserInfoNickname.getEditText().addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.toString().isValidNickname()) {
                            binding.etUserInfoNickname.setError("2자~20자 길이로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etUserInfoNickname.setSuccess("별명 입력 완료")

                            if (!binding.etUserInfoNickname.hasError && !binding.etUserInfoName.hasError && !binding.etUserInfoPhone.hasError && !binding.etUserInfoBirth.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etUserInfoName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNameFocused) {
                isNameFocused = true
                binding.etUserInfoName.getEditText().addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.toString().isValidName()) {
                            binding.etUserInfoName.setError("2자~20자 사이의 한글/영어로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etUserInfoName.setSuccess("이름 입력 완료")

                            if (!binding.etUserInfoNickname.hasError && !binding.etUserInfoName.hasError && !binding.etUserInfoPhone.hasError && !binding.etUserInfoBirth.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etUserInfoPhone.getEditText().addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.etUserInfoPhone.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPhoneFocused) {
                isPhoneFocused = true
                binding.etUserInfoPhone.getEditText().addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.toString().isValidPhone()) {
                            binding.etUserInfoPhone.setError("잘못된 전화번호 형식입니다.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etUserInfoPhone.setSuccess("연락처 입력 완료")

                            if (!binding.etUserInfoNickname.hasError && !binding.etUserInfoName.hasError && !binding.etUserInfoPhone.hasError && !binding.etUserInfoBirth.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etUserInfoBirth.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isBirthFocused) {
                isBirthFocused = true
                binding.etUserInfoBirth.getEditText().addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.toString().isValidBirth()) {
                            binding.etUserInfoBirth.setError("6글자의 날짜 형식으로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etUserInfoBirth.setSuccess("생년월일 입력 완료")

                            if (!binding.etUserInfoNickname.hasError && !binding.etUserInfoName.hasError && !binding.etUserInfoPhone.hasError && !binding.etUserInfoBirth.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }
        binding.etUserInfoEmail.isEnabled = false

        /** Spinner Control */
        binding.spUserInfoGender.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.gender.postValue("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0)
                    viewModel.gender.postValue(GENDER_MALE)
                else if(position == 1)
                    viewModel.gender.postValue(GENDER_FEMALE)
            }
        }

        /** 프로필 사진 변경 */
        binding.cvUserInfo.setOnClickListener {
            //val profileImageDialog = ProfileImageDialog()
            //profileImageDialog.show(supportFragmentManager, "Image")
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if (it) {
                showToast("유저 정보를 변경했습니다.")
                finish()
            } else {
                showToast("유저 정보를 업데이트하는 도중 오류가 발생했습니다. 다시 로그인해 주세요.")
                val intent = Intent(this@UserInfoActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        /** 비밀번호 변경 */
        binding.llUserInfoChangePW.setOnClickListener {
            showToast("추후 업데이트될 예정입니다.")
        }

        /** 로그아웃 */
        val logoutDialog = PopupDialog("로그아웃하시겠습니까?", "로그아웃", "취소")
        binding.llUserInfoLogout.setOnClickListener {
            logoutDialog.show(supportFragmentManager, "Logout")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val resultUri: Uri = result!!.uriContent!!

            Glide.with(this@UserInfoActivity).load(resultUri).into(binding.ivUserInfo)
            viewModel.profileImg.postValue(resultUri)

            if(!binding.etUserInfoNickname.hasError && !binding.etUserInfoName.hasError && !binding.etUserInfoPhone.hasError && !binding.etUserInfoBirth.hasError)
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
    }

    /** 사진 촬영 후 profileImageDialog의 interface의 콜백 함수 부분 */
    override fun onClick(image: Bitmap) = binding.ivUserInfo.setImageBitmap(image)
    override fun onError(msg: String) = showToast(msg)

    /** 로그아웃 PopupDialog의 interface의 콜백 함수 부분 */
    override fun onPositive() {
        logout()
    }
    override fun onNegative() {}

    // 로그아웃
    private fun logout() {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.logout {
                showToast("성공적으로 로그아웃했습니다.")

                val userData = getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
                val userDataEditor = userData.edit()
                userDataEditor.remove("jwtToken")
                userDataEditor.remove("userId")
                userDataEditor.apply()
                val initSP = App().context().getSharedPreferences("started", MODE_PRIVATE)
                initSP.edit().remove("started").apply()

                val logoutIntent = Intent(this@UserInfoActivity, LoginActivity::class.java)
                logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logoutIntent)
            }
        }
        else {
            showToast("성공적으로 로그아웃했습니다.")

            val userData = getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
            val userDataEditor = userData.edit()
            userDataEditor.remove("jwtToken")
            userDataEditor.remove("userId")
            userDataEditor.apply()
            val initSP = App().context().getSharedPreferences("started", MODE_PRIVATE)
            initSP.edit().remove("started").apply()

            val logoutIntent = Intent(this@UserInfoActivity, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
    }
}