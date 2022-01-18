package com.cashfulus.cashcarplus.ui.adinfo

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityAdRegisterBinding
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.car.AddCarActivity
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.util.*
import kotlinx.android.synthetic.main.widget_upgraded_edittext.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class AdRegisterActivity : BaseActivity(), PopupDialogClickListener {
    val numFormat = DecimalFormat("###,###")

    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityAdRegisterBinding>(R.layout.activity_ad_register)
    private val viewModel: AdRegisterViewModel by viewModel { parametersOf(this@AdRegisterActivity) }

    // 선택된 차량 id
    var vehicleId: Int? = null

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isNameFocused = false
    var isPhoneFocused = false
    var isAddress2Focused = false
    // 각 UpgradedEditText가 Valid한지 체크
    var isCarValid = false
    var isNameValid = false
    var isPhoneValid = false
    var isAddress1Valid = false
    var isAddress2Valid = false
    // '신청하기' 버튼 활성화 여부
    val isAllValid = MutableLiveData<Boolean>(false)

    /** 차량 추가 후 할 작업. */
    val addcarActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            viewModel.getCarInfo(intent.getIntExtra("adId", -1))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AdRegisterActivity
            viewModel = this@AdRegisterActivity.viewModel
        }

        /** 정보 가져오기 */
        if(intent.getIntExtra("adId", -1) != -1) {
            Glide.with(this@AdRegisterActivity).load(intent.getStringExtra("image")).into(binding.ivAdRegisterTitle)
            binding.tvAdRegisterAdTitle.text = intent.getStringExtra("title")
            binding.tvAdRegisterPoint.text = numFormat.format(intent.getIntExtra("point", 0))

            viewModel.getCarInfo(intent.getIntExtra("adId", -1))
        }

        /** Toolbar 설정 */
        binding.toolbarAdRegister.setLeftOnClick {
            finish()
        }

        /** LiveData 관련 설정 */
        viewModel.cars.observe(binding.lifecycleOwner!!, {
            when(it.size) {
                0 -> {
                    isCarValid = false

                    binding.btnAdRegisterCar1.visibility = View.GONE
                    binding.btnAdRegisterCar2.visibility = View.GONE
                    binding.btnAdRegisterCar3.visibility = View.GONE

                    binding.cardAdRegisterNone.visibility = View.VISIBLE
                    binding.cardAdRegisterCar.visibility = View.GONE

                    binding.cardAdRegisterNone.setOnClickListener {
                        val intent = Intent(this@AdRegisterActivity, AddCarActivity::class.java)
                        intent.putExtra("isNewCar", true)
                        addcarActivity.launch(intent)
                    }
                }
                1 -> {
                    isCarValid = true

                    binding.btnAdRegisterCar1.visibility = View.GONE
                    binding.btnAdRegisterCar2.visibility = View.GONE
                    binding.btnAdRegisterCar3.visibility = View.GONE

                    binding.cardAdRegisterNone.visibility = View.GONE
                    binding.cardAdRegisterCar.visibility = View.VISIBLE

                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = it[0].carNumber
                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = it[0].brand + " " + it[0].vehicleModelName
                    binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)

                    vehicleId = it[0].vehicleId
                }
                2 -> {
                    isCarValid = true
                    val result = it

                    binding.btnAdRegisterCar1.visibility = View.GONE
                    binding.btnAdRegisterCar2.visibility = View.VISIBLE
                    binding.btnAdRegisterCar3.visibility = View.VISIBLE

                    binding.btnAdRegisterCar2.text = "1"
                    binding.btnAdRegisterCar3.text = "2"
                    binding.btnAdRegisterCar2.isSelected = true
                    binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_wt))
                    binding.btnAdRegisterCar3.isSelected = false
                    binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_400))

                    binding.cardAdRegisterNone.visibility = View.GONE
                    binding.cardAdRegisterCar.visibility = View.VISIBLE

                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[0].carNumber
                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[0].brand + " " + result[0].vehicleModelName
                    vehicleId = result[0].vehicleId
                    if(result[0].supporters == 1)
                        binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                    else
                        binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                    binding.btnAdRegisterCar2.setOnClickListener {
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[0].carNumber
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[0].brand + " " + result[0].vehicleModelName
                        vehicleId = result[0].vehicleId
                        if(result[0].supporters == 1)
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                        else
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                        binding.btnAdRegisterCar2.isSelected = true
                        binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_wt))
                        binding.btnAdRegisterCar3.isSelected = false
                        binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_400))
                    }
                    binding.btnAdRegisterCar3.setOnClickListener {
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[1].carNumber
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[1].brand + " " + result[1].vehicleModelName
                        vehicleId = result[1].vehicleId
                        if(result[1].supporters == 1)
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                        else
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                        binding.btnAdRegisterCar2.isSelected = false
                        binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_400))
                        binding.btnAdRegisterCar3.isSelected = true
                        binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_wt))
                    }
                }
                3 -> {
                    isCarValid = true
                    val result = it

                    binding.btnAdRegisterCar1.visibility = View.VISIBLE
                    binding.btnAdRegisterCar2.visibility = View.VISIBLE
                    binding.btnAdRegisterCar3.visibility = View.VISIBLE

                    binding.btnAdRegisterCar1.isSelected = true
                    binding.btnAdRegisterCar1.setTextColor(getColor(R.color.grayscale_wt))
                    binding.btnAdRegisterCar2.isSelected = false
                    binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_400))
                    binding.btnAdRegisterCar3.isSelected = false
                    binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_400))

                    binding.cardAdRegisterNone.visibility = View.GONE
                    binding.cardAdRegisterCar.visibility = View.VISIBLE

                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[0].carNumber
                    binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[0].brand + " " + result[0].vehicleModelName
                    vehicleId = result[0].vehicleId
                    if(result[0].supporters == 1)
                        binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                    else
                        binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                    binding.btnAdRegisterCar1.setOnClickListener {
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[0].carNumber
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[0].brand + " " + result[0].vehicleModelName
                        vehicleId = result[0].vehicleId
                        if(result[0].supporters == 1)
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                        else
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                        binding.btnAdRegisterCar1.isSelected = true
                        binding.btnAdRegisterCar1.setTextColor(getColor(R.color.grayscale_wt))
                        binding.btnAdRegisterCar2.isSelected = false
                        binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_400))
                        binding.btnAdRegisterCar3.isSelected = false
                        binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_400))
                    }
                    binding.btnAdRegisterCar2.setOnClickListener {
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[1].carNumber
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[1].brand + " " + result[1].vehicleModelName
                        vehicleId = result[1].vehicleId
                        if(result[1].supporters == 1)
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                        else
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                        binding.btnAdRegisterCar1.isSelected = false
                        binding.btnAdRegisterCar1.setTextColor(getColor(R.color.grayscale_400))
                        binding.btnAdRegisterCar2.isSelected = true
                        binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_wt))
                        binding.btnAdRegisterCar3.isSelected = false
                        binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_400))
                    }
                    binding.btnAdRegisterCar3.setOnClickListener {
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarNumber).text = result[2].carNumber
                        binding.cardAdRegisterCar.findViewById<TextView>(R.id.tvAdRegisterCarBrandModel).text = result[2].brand + " " + result[2].vehicleModelName
                        vehicleId = result[2].vehicleId
                        if(result[2].supporters == 1)
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_supporters)
                        else
                            binding.cardAdRegisterCar.background = ContextCompat.getDrawable(this@AdRegisterActivity, R.drawable.row_mycar_not_supporters)

                        binding.btnAdRegisterCar1.isSelected = false
                        binding.btnAdRegisterCar1.setTextColor(getColor(R.color.grayscale_400))
                        binding.btnAdRegisterCar2.isSelected = false
                        binding.btnAdRegisterCar2.setTextColor(getColor(R.color.grayscale_400))
                        binding.btnAdRegisterCar3.isSelected = true
                        binding.btnAdRegisterCar3.setTextColor(getColor(R.color.grayscale_wt))
                    }
                }
            }
        })

        viewModel.userInfo.observe(binding.lifecycleOwner!!, {
            if(it.main_address.isNotBlank()) {
                binding.tvAdRegisterAddress1.text = it.main_address
                binding.etAdRegisterAddress2.isEnabled = true
                isAddress1Valid = true
            }
            if(it.detail_address.isNotBlank()) {
                binding.etAdRegisterAddress2.getEditText().setText(it.detail_address)
                isAddress2Valid = true
            }
            if(it.call_number.isNotBlank()) {
                binding.etAdRegisterPhone.getEditText().setText(it.call_number)
                isPhoneValid = true
            }
            if(it.name.isNotBlank()) {
                binding.etAdRegisterName.getEditText().setText(it.name)
                isNameValid = true
            }

            if(isCarValid && isNameValid && isPhoneValid && isAddress1Valid && isAddress2Valid && vehicleId!=null)
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        })

        viewModel.response.observe(binding.lifecycleOwner!!, {
            showToast("신청에 성공했습니다.")
            val intent = Intent(this@AdRegisterActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        /** Validation 관련 설정 */
        binding.etAdRegisterName.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isNameFocused) {
                isNameFocused = true
                binding.etAdRegisterName.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!binding.etAdRegisterName.getEditText().text.toString().isValidNickname()) {
                            binding.etAdRegisterName.setError("2자-50자 사이로 입력해주세요.")
                            isNameValid = false
                            isAllValid.postValue(false)
                        }
                        else {
                            binding.etAdRegisterName.setSuccess("수령인 입력 완료")
                            isNameValid = true
                            if(isCarValid && isNameValid && isPhoneValid && isAddress1Valid && isAddress2Valid && vehicleId!=null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }
        binding.etAdRegisterPhone.getEditText().addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.etAdRegisterPhone.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPhoneFocused) {
                isPhoneFocused = true
                binding.etAdRegisterPhone.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!binding.etAdRegisterPhone.getEditText().text.toString().isValidPhone()) {
                            binding.etAdRegisterPhone.setError("올바른 전화번호 형식으로 입력해주세요.")
                            isPhoneValid = false
                            isAllValid.postValue(false)
                        } else {
                            binding.etAdRegisterPhone.setSuccess("연락처 입력 완료")
                            isPhoneValid = true
                            if(isCarValid && isNameValid && isPhoneValid && isAddress1Valid && isAddress2Valid && vehicleId!=null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }
        binding.etAdRegisterAddress2.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isAddress2Focused) {
                isAddress2Focused = true
                binding.etAdRegisterAddress2.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(binding.etAdRegisterAddress2.getEditText().text.length < 2) {
                            binding.etAdRegisterAddress2.setError("상세 주소를 입력해주세요.")
                            isAddress2Valid = false
                            isAllValid.postValue(false)
                        } else {
                            binding.etAdRegisterAddress2.setSuccess("주소 입력 완료")
                            isAddress2Valid = true
                            if(isCarValid && isNameValid && isPhoneValid && isAddress1Valid && isAddress2Valid && vehicleId!=null)
                                isAllValid.postValue(true)
                        }
                    }
                })
            }
        }

        /** 배송지 주소 검색 */
        val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if(activityResult.resultCode == RESULT_OK) {
                isAddress1Valid = true
                binding.tvAdRegisterAddress1.text = activityResult.data!!.getStringExtra("address")
                viewModel.mainAddress.postValue(activityResult.data!!.getStringExtra("address"))
                binding.etAdRegisterAddress2.isEnabled = true
            }
        }

        binding.etAdRegisterAddress1.setOnClickListener {
            val intent = Intent(this@AdRegisterActivity, AddressActivity::class.java)
            requestActivity.launch(intent)
        }

        /** '신청하기' 버튼 클릭 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnAdRegister.isSelected = it
            binding.btnAdRegister.isEnabled = it
            binding.btnAdRegister.isFocusable = it
            binding.btnAdRegister.isClickable = it

            if(it) {
                binding.btnAdRegister.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnAdRegister.typeface = ResourcesCompat.getFont(this@AdRegisterActivity, R.font.notosanskr_bold)
            } else {
                binding.btnAdRegister.setTextColor(getColor(R.color.grayscale_400))
                binding.btnAdRegister.typeface = ResourcesCompat.getFont(this@AdRegisterActivity, R.font.notosanskr_medium)
            }
        })
        binding.btnAdRegister.setOnClickListener {
            val dialog = PopupDialog("정말 신청하시겠습니까?", "신청하기", "나중에 할래요")
            dialog.show(supportFragmentManager, "AdRegister")
        }
    }

    /** 신청 Dialog의 Callback */
    override fun onPositive() {
        viewModel.registerAd(intent.getIntExtra("adId", -1), vehicleId!!)
    }
    override fun onNegative() {}
}