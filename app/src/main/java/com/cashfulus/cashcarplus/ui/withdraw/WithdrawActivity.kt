package com.cashfulus.cashcarplus.ui.withdraw

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.databinding.ActivityWithdrawBinding
import com.cashfulus.cashcarplus.ui.adapter.SpinnerWithHintAdapter
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.ui.login.CluaseWebviewActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.util.GENDER_FEMALE
import com.cashfulus.cashcarplus.util.GENDER_MALE
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

        /** Toolbar ?????? */
        binding.toolbarWithdraw.setLeftOnClick {
            finish()
        }

        /** '??????' ?????? ?????? ??? */
        binding.btnWithdrawAllPoint.setOnClickListener {
            binding.etWithdrawPoint.getEditText().setText(allPoint.toString())
            binding.etWithdrawPoint.hasError = false

            if(!binding.etWithdrawAccount.hasError && viewModel.bank.value != null && viewModel.name.value != null && binding.cbWithdrawClause.isChecked)
                isAllValid.postValue(true)
        }

        /** '???????????? ?????? ??? ?????? ??????'??? ?????? */
        binding.btnWithdrawClause.setOnClickListener {
            val intent = Intent(this@WithdrawActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("title", "???????????? ?????? ??? ?????? ??????")
            intent.putExtra("url", getString(R.string.url_terms_of_service))
            startActivity(intent)
        }

        /** ????????? ?????? */
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
                        // ????????? ?????? ?????? : 10000????????? ??????, 1000 ??????
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

        /** '?????? ????????????' ?????? */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnWithdraw.isSelected = it
            binding.btnWithdraw.isEnabled = it
            binding.btnWithdraw.isFocusable = it
            binding.btnWithdraw.isClickable = it

            if(it) {
                binding.btnWithdraw.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnWithdraw.typeface = ResourcesCompat.getFont(this@WithdrawActivity, R.font.notosanskr_bold)
            } else {
                binding.btnWithdraw.setTextColor(getColor(R.color.grayscale_400))
                binding.btnWithdraw.typeface = ResourcesCompat.getFont(this@WithdrawActivity, R.font.notosanskr_medium)
            }
        })
        binding.btnWithdraw.setOnClickListener {
            val dialog = PopupDialog("????????? ????????? ????????? ????????? ?????? ??????\n" + "????????? ??????????????????. ", "??????", "??????")
            dialog.show(supportFragmentManager, "Withdraw")
        }

        /** LiveData ?????? */
        viewModel.bankData.observe(binding.lifecycleOwner!!, {
            allPoint = it.deposit
            binding.tvWithdrawAllPoint.text = numFormat.format(allPoint) + " P"

            if(it.accountBank != "" && it.account != "") {
                binding.etWithdrawAccount.hasError = false

                viewModel.bank.postValue(it.accountBank)
                viewModel.account.postValue(it.account)
                viewModel.name.postValue(it.name)

                when (it.accountBank) {
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(0)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(1)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(2)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(3)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(4)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(5)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(6)
                    }
                    "?????????????????????" -> {
                        binding.spWithdrawBank.setSelection(7)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(8)
                    }
                    "???????????????" -> {
                        binding.spWithdrawBank.setSelection(9)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(10)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(11)
                    }
                    "???????????????" -> {
                        binding.spWithdrawBank.setSelection(12)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(13)
                    }
                    "?????????" -> {
                        binding.spWithdrawBank.setSelection(14)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(15)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(16)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(17)
                    }
                    "???????????????" -> {
                        binding.spWithdrawBank.setSelection(18)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(19)
                    }
                    "????????????" -> {
                        binding.spWithdrawBank.setSelection(20)
                    }
                    "??????????????????" -> {
                        binding.spWithdrawBank.setSelection(21)
                    }
                    "SC????????????" -> {
                        binding.spWithdrawBank.setSelection(22)
                    }
                }
            }
        })

        viewModel.isSucceed.observe(binding.lifecycleOwner!!, {
            if(it) {
                setResult(RESULT_OK)
                finish()
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)

            if(it.status == LOST_USER_INFO_ERROR_CODE) {
                val intent = Intent(this@WithdrawActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@WithdrawActivity.finish()
            } else if(it.status == NO_INTERNET_ERROR_CODE) {
                finish()
            }
        })
    }

    override fun onPositive() {
        viewModel.registerWithdraw()
    }

    override fun onNegative() {}
}