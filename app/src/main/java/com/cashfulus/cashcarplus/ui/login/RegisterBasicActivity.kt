package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityRegisterBasicBinding
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.util.isValidEmail
import com.cashfulus.cashcarplus.util.isValidNickname
import com.cashfulus.cashcarplus.util.isValidPassword
import com.cashfulus.cashcarplus.util.isValidPhone
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RegisterBasicActivity : BaseActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@RegisterBasicActivity) }
    private val binding by binding<ActivityRegisterBasicBinding>(R.layout.activity_register_basic)
    private val viewModel: RegisterBasicViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isEmailFocused = false
    var isPasswordFocused = false
    var isPasswordCheckFocused = false
    // '신청하기' 버튼 활성화 여부
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@RegisterBasicActivity
            viewModel = this@RegisterBasicActivity.viewModel
        }

        /** Intent(From ClauseActivity) 정보 받아와서 셋팅 */
        val isAlarmSet = intent.getBooleanExtra("alarm", false)
        val isMarketingSet = intent.getBooleanExtra("marketing", false)

        /** Toolbar 설정 */
        binding.toolbarRegisterBasic.setLeftOnClick {
            finish()
        }

        /** Form Validation + 이메일 변경 가능 여부 체크 */
        binding.etRegisterEmail.hasError = true
        binding.etRegisterPassword.hasError = true
        binding.etRegisterPasswordCheck.hasError = true
        binding.etRegisterEmail.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isEmailFocused) {
                isEmailFocused = true
                binding.etRegisterEmail.getEditText().addTextChangedListener(object:
                    TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidEmail()) {
                            binding.etRegisterEmail.setError("잘못된 이메일 형식입니다.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterEmail.setSuccess("이메일 입력 완료")

                            if(!binding.etRegisterEmail.hasError && !binding.etRegisterPassword.hasError && !binding.etRegisterPasswordCheck.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etRegisterPassword.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPasswordFocused) {
                isPasswordFocused = true
                binding.etRegisterPassword.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidPassword()) {
                            binding.etRegisterPassword.setError("비밀번호는 영문/숫자 조합 8자 이상이어야 합니다.")
                            isAllValid.postValue(false)
                            if(s.toString() != binding.etRegisterPasswordCheck.getEditText().text.toString())
                                binding.etRegisterPasswordCheck.setError("비밀번호 확인이 일치하지 않습니다")
                        } else {
                            binding.etRegisterPassword.setSuccess("비밀번호 입력 완료")
                            if(s.toString() != binding.etRegisterPasswordCheck.getEditText().text.toString()) {
                                binding.etRegisterPasswordCheck.setError("비밀번호 확인이 일치하지 않습니다")
                                isAllValid.postValue(false)
                            } else if(!binding.etRegisterEmail.hasError && !binding.etRegisterPassword.hasError && !binding.etRegisterPasswordCheck.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        binding.etRegisterPasswordCheck.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPasswordCheckFocused) {
                isPasswordCheckFocused = true
                binding.etRegisterPasswordCheck.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s.toString() != binding.etRegisterPassword.getEditText().text.toString()) {
                            binding.etRegisterPasswordCheck.setError("비밀번호 확인이 일치하지 않습니다")
                            isAllValid.postValue(false)
                        } else {
                            binding.etRegisterPasswordCheck.setSuccess("비밀번호 확인")

                            if(!binding.etRegisterEmail.hasError && !binding.etRegisterPassword.hasError && !binding.etRegisterPasswordCheck.hasError)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                val intent = Intent(this@RegisterBasicActivity, RegisterActivity::class.java)
                intent.putExtra("alarm", isAlarmSet)
                intent.putExtra("marketing", isMarketingSet)
                intent.putExtra("method", "normal")
                startActivity(intent)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            if(it.status == 409) { // 중복된 이메일
                binding.etRegisterEmail.setError("이미 등록되어 있는 계정입니다")
            } else {
                showToast(it.message)
            }
        })

        /** LoadingDialog 추가 시 오류 나는 것 같아서 일단 비활성화. */
        /*viewModel.loading.observe(binding.lifecycleOwner!!, {
            if(it)
                loadingDialog.show()
            else
                loadingDialog.dismiss()
        })*/

        /** '시작하기' 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnRegisterBasic.isEnabled = it
            binding.btnRegisterBasic.isClickable = it
            binding.btnRegisterBasic.isFocusable = it
            binding.btnRegisterBasic.isSelected = it
            if(it) {
                binding.btnRegisterBasic.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnRegisterBasic.typeface = ResourcesCompat.getFont(this@RegisterBasicActivity, R.font.notosanskr_bold)
            } else {
                binding.btnRegisterBasic.setTextColor(getColor(R.color.grayscale_400))
                binding.btnRegisterBasic.typeface = ResourcesCompat.getFont(this@RegisterBasicActivity, R.font.notosanskr_medium)
            }
        })
        /** '시작하기' 버튼 클릭 이벤트 */
        binding.btnRegisterBasic.setOnClickListener {
            viewModel.register("normal", isAlarmSet, isMarketingSet)
        }
    }
}