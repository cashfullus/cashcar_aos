package com.cashfulus.cashcarplus.ui.car

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityAddCarBinding
import com.cashfulus.cashcarplus.util.*
import com.cashfulus.cashcarplus.view.NOT_SUPPORTERS_CAR
import com.cashfulus.cashcarplus.view.ONLY_ONE_CAR
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddCarActivity : BaseActivity() {
    private val binding by binding<ActivityAddCarBinding>(R.layout.activity_add_car)
    private val viewModel: AddCarViewModel by viewModel { parametersOf() }

    private val CURRENT_YEAR = 2021

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isModelFocused = false
    var isYearFocused = false
    var isCarNumberFocused = false

    // 버튼 활성화/비활성화
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AddCarActivity
            viewModel = this@AddCarActivity.viewModel
        }

        /** 툴바 버튼 이벤트 */
        binding.toolbarAddCar.setLeftOnClick {
            setResult(RESULT_CANCELED)
            finish()
        }

        /** EditText 정규식 설정 (최초 Focus 시 정규식을 지정함) */
        binding.etCarNumberA.hasError = true
        binding.etCarModelA.hasError = true
        binding.etCarYearA.hasError = true
        binding.etCarCompanyA.getEditText().setText("Cashcar")
        binding.etCarOwnerA.getEditText().setText("Cashcar")

        binding.etCarNumberA.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isCarNumberFocused) {
                isCarNumberFocused = true
                binding.etCarNumberA.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidCarNumber()) {
                            binding.etCarNumberA.setError("올바르지 않은 차량 번호입니다.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etCarNumberA.setSuccess("차량번호 입력 완료")

                            if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                                if(binding.etCarCompanyA.visibility != View.VISIBLE && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(binding.etCarCompanyA.visibility != View.VISIBLE && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else
                                    isAllValid.postValue(false)
                            } else {
                                isAllValid.postValue(false)
                            }
                        }
                    }
                })
            }
        }

        binding.etCarModelA.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isModelFocused) {
                isModelFocused = true
                binding.etCarModelA.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidModel()) {
                            binding.etCarModelA.setError("2자-30자 길이로 입력해주세요.")
                            isAllValid.postValue(false)
                        } else {
                            binding.etCarModelA.setSuccess("차량 모델 입력 완료")

                            if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                                if(binding.etCarCompanyA.visibility != View.VISIBLE && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(binding.etCarCompanyA.visibility != View.VISIBLE && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else
                                    isAllValid.postValue(false)
                            } else {
                                isAllValid.postValue(false)
                            }
                        }
                    }
                })
            }
        }

        binding.etCarYearA.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isYearFocused) {
                isYearFocused = true
                binding.etCarYearA.getEditText().addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(!s.toString().isValidYear() || s.toString().toInt() > CURRENT_YEAR || s.toString().toInt() < 1970) { // 1970~2021
                            binding.etCarYearA.setError("연도를 4자리로 입력해주세요.(1970년 이후부터 등록 가능)")
                            isAllValid.postValue(false)
                        } else {
                            binding.etCarYearA.setSuccess("연식 입력 완료")

                            if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                                if(binding.etCarCompanyA.visibility != View.VISIBLE && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(binding.etCarCompanyA.visibility != View.VISIBLE && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && binding.etCarOwnerA.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarCompanyA.hasError && !binding.etCarOwnerA.hasError)
                                    isAllValid.postValue(true)
                                else
                                    isAllValid.postValue(false)
                            } else {
                                isAllValid.postValue(false)
                            }
                        }
                    }
                })
            }
        }

        binding.etCarCompanyA.getEditText().addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(!s.toString().isValidBrand()) {
                    binding.etCarCompanyA.setError("2자~20자 길이로 입력해주세요.")
                    isAllValid.postValue(false)
                } else {
                    binding.etCarCompanyA.setSuccess("제조사 입력 완료")

                    if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                        if(binding.etCarCompanyA.visibility != View.VISIBLE && binding.etCarOwnerA.visibility != View.VISIBLE)
                            isAllValid.postValue(true)
                        else if(binding.etCarCompanyA.visibility != View.VISIBLE && !binding.etCarOwnerA.hasError)
                            isAllValid.postValue(true)
                        else if(!binding.etCarCompanyA.hasError && binding.etCarOwnerA.visibility != View.VISIBLE)
                            isAllValid.postValue(true)
                        else if(!binding.etCarCompanyA.hasError && !binding.etCarOwnerA.hasError)
                            isAllValid.postValue(true)
                        else
                            isAllValid.postValue(false)
                    } else {
                        isAllValid.postValue(false)
                    }
                }
            }
        })
        binding.etCarOwnerA.getEditText().addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(!s.toString().isValidOwner()) {
                    binding.etCarOwnerA.setError("2자~30자 길이로 입력해주세요.")
                    isAllValid.postValue(false)
                } else {
                    binding.etCarOwnerA.setSuccess("차량소유주와의 관계 입력 완료")

                    if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                        if(binding.etCarCompanyA.visibility != View.VISIBLE && binding.etCarOwnerA.visibility != View.VISIBLE)
                            isAllValid.postValue(true)
                        else if(binding.etCarCompanyA.visibility != View.VISIBLE && !binding.etCarOwnerA.hasError)
                            isAllValid.postValue(true)
                        else if(!binding.etCarCompanyA.hasError && binding.etCarOwnerA.visibility != View.VISIBLE)
                            isAllValid.postValue(true)
                        else if(!binding.etCarCompanyA.hasError && !binding.etCarOwnerA.hasError)
                            isAllValid.postValue(true)
                        else
                            isAllValid.postValue(false)
                    } else {
                        isAllValid.postValue(false)
                    }
                }
            }
        })

        /** 제조국 Button + 제조사 Spinner 관련 설정 */
        val koreanAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.korean_company).toList())
        koreanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val foreignAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.foreign_company).toList())
        foreignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCarCompanyA.adapter = koreanAdapter
        /** 차량소유주와의 관계 Spinner 관련 설정 */
        val relationAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.car_owner).toList())
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCarOwnerA.adapter = relationAdapter

        // 최초 상태 : '국산'이 선택되어 있음
        binding.btnCarKoreanA.isSelected = true
        binding.btnCarKoreanA.setOnClickListener {
            viewModel.isKorean.postValue(true)
            binding.btnCarKoreanA.isSelected = true
            binding.btnCarForeignA.isSelected = false

            binding.spCarCompanyA.adapter = koreanAdapter
        }
        binding.btnCarForeignA.setOnClickListener {
            viewModel.isKorean.postValue(false)
            binding.btnCarKoreanA.isSelected = false
            binding.btnCarForeignA.isSelected = true

            binding.spCarCompanyA.adapter = foreignAdapter

            try {
                val popup = Spinner::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val popupWindow = popup.get(binding.spCarCompanyA) as android.widget.ListPopupWindow
                popupWindow.height = 200
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.spCarCompanyA.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if(viewModel.isKorean.value!!)
                    viewModel.company.postValue(resources.getStringArray(R.array.korean_company)[0])
                else
                    viewModel.company.postValue(resources.getStringArray(R.array.foreign_company)[0])
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(viewModel.isKorean.value!!)
                    if(position == resources.getStringArray(R.array.korean_company).size-1) {
                        binding.etCarCompanyA.visibility = View.VISIBLE
                        binding.etCarCompanyA.getEditText().setText("")
                    } else {
                        viewModel.company.postValue(resources.getStringArray(R.array.korean_company)[position])
                        binding.etCarCompanyA.visibility = View.GONE

                        if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                            if(binding.etCarOwnerA.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(!binding.etCarOwnerA.hasError)
                                isAllValid.postValue(true)
                            else
                                isAllValid.postValue(false)
                        } else {
                            isAllValid.postValue(false)
                        }
                    }
                else {
                    if(position == resources.getStringArray(R.array.foreign_company).size-1) {
                        binding.etCarCompanyA.visibility = View.VISIBLE
                        binding.etCarCompanyA.getEditText().setText("")
                    } else {
                        viewModel.company.postValue(resources.getStringArray(R.array.foreign_company)[position])
                        binding.etCarCompanyA.visibility = View.GONE

                        if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                            if(binding.etCarOwnerA.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(!binding.etCarOwnerA.hasError)
                                isAllValid.postValue(true)
                            else
                                isAllValid.postValue(false)
                        } else {
                            isAllValid.postValue(false)
                        }
                    }
                }
            }
        }
        /** '브랜드' Spinner 사이즈 제한 */
        try {
            val popup = Spinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup.get(binding.spCarCompanyA) as android.widget.ListPopupWindow
            popupWindow.height = 500
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*try {
            val popup = getPopupField()

            // Get private mPopup member variable and try cast to ListPopupWindow
            val popupWindow = popup.get(binding.spCarCompanyA) as android.widget.ListPopupWindow

            // Set popupWindow height to max - 40dp
            binding.spCarCompanyA.post {
                val r = Rect()
                binding.spCarCompanyA.getGlobalVisibleRect(r)
                popupWindow.height = 500
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        /** '차량소유주와의 관계' Spinner */
        binding.spCarOwnerA.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.owner.postValue(resources.getStringArray(R.array.car_owner)[0])
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == resources.getStringArray(R.array.car_owner).size-1) {
                    binding.etCarOwnerA.visibility = View.VISIBLE
                    binding.etCarOwnerA.getEditText().setText("")
                } else {
                    viewModel.owner.postValue(resources.getStringArray(R.array.car_owner)[position])
                    binding.etCarOwnerA.visibility = View.GONE

                    if(!binding.etCarNumberA.hasError && !binding.etCarModelA.hasError && !binding.etCarYearA.hasError) {
                        if(binding.etCarCompanyA.visibility != View.VISIBLE)
                            isAllValid.postValue(true)
                        else if(!binding.etCarCompanyA.hasError)
                            isAllValid.postValue(true)
                        else
                            isAllValid.postValue(false)
                    } else {
                        isAllValid.postValue(false)
                    }
                }
            }
        }


        /** 광고 서포터즈 Checkbox 활성화/비활성화 */
        binding.cbCarSupportersA.isEnabled(!intent.getBooleanExtra("isNewCar", true))
        binding.cbCarSupportersA.setCurrentState(if(intent.getBooleanExtra("isNewCar", true)) ONLY_ONE_CAR else NOT_SUPPORTERS_CAR)
        if(intent.getBooleanExtra("isNewCar", true))
            binding.cbCarSupportersA.visibility = View.GONE
        else
            binding.cbCarSupportersA.visibility = View.VISIBLE


        /** 등록하기 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnCarAddA.isEnabled = it
            binding.btnCarAddA.isClickable = it
            binding.btnCarAddA.isFocusable = it
            binding.btnCarAddA.isSelected = it
            if(it) {
                binding.btnCarAddA.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnCarAddA.typeface = ResourcesCompat.getFont(this@AddCarActivity, R.font.notosanskr_bold)
            } else {
                binding.btnCarAddA.setTextColor(getColor(R.color.grayscale_400))
                binding.btnCarAddA.typeface = ResourcesCompat.getFont(this@AddCarActivity, R.font.notosanskr_medium)
            }
        })

        /** 등록하기 버튼 */
        binding.btnCarAddA.setOnClickListener {
            viewModel.registerCar()
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            if(it) {
                showToast("차량 "+viewModel.carNumber.value!!+" 등록에 성공했습니다.")
                setResult(RESULT_OK)
                finish()
            } else {
                showToast("확인되지 않은 오류가 발생했습니다.")
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })
    }

    /*fun getPopupField(): Field {
        var sPopupField: Field? = null

        if (sPopupField == null) {
            try {
                val popup = Spinner::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true

                sPopupField = popup
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sPopupField!!
    }*/
}