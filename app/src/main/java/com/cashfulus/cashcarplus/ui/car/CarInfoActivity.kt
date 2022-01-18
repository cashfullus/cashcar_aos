package com.cashfulus.cashcarplus.ui.car

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityCarInfoBinding
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import com.cashfulus.cashcarplus.util.*
import com.cashfulus.cashcarplus.view.IS_SUPPORTERS_CAR
import com.cashfulus.cashcarplus.view.NOT_SUPPORTERS_CAR
import com.cashfulus.cashcarplus.view.ONLY_ONE_CAR
import com.cashfulus.cashcarplus.view.ONLY_ONE_CAR_NOT_CHECKED
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/** 2021.05.04 : 수정 가능/불가능 상태가 하나로 합쳐짐. */
/** 2021.06.09 : 데이터 수신 이후 UI를 갱신하도록 수정함. */

class CarInfoActivity : BaseActivity(), PopupDialogClickListener {
    private val binding by binding<ActivityCarInfoBinding>(R.layout.activity_car_info)
    private val viewModel: CarInfoViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isModelFocused = false
    var isYearFocused = false
    var isCarNumberFocused = false
    // 전체 Validation
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@CarInfoActivity
            viewModel = this@CarInfoActivity.viewModel
        }

        /** Popup 설정 */
        // PopupDialogClickListener의 경우, deleteDialog만 onPositive를 호출하고, 둘 다 dialog를 종료시키기만 하는 onNegative를 호출하므로
        // PopupDialogClickListener에 대해 dialog별로 따로 처리를 가할 필요는 없다.
        val deleteDialog = PopupDialog("정말 삭제하시겠습니까?", "확인", "취소")
        val cannotDeleteDialog = PopupDialog("서포터즈 진행 차량은 수정 또는 삭제 할 수 없습니다.", null, "확인")

        /** 상태 설정 + 초기값 받아오기 */
        if (intent.getIntExtra("vehicle_id", 0) == 0) {
            showToastShort("정보를 받아오는 도중 오류가 발생했습니다.")
            setResult(-999)
            finish()
        } else {
            viewModel.getCarInfo(intent.getIntExtra("vehicle_id", 0))
        }

        /** toolbar 설정 */
        binding.toolbarAddInfo.setLeftOnClick {
            finish()
        }
        binding.toolbarAddInfo.setRightOnClick {
            if(viewModel.isDeletable!!) {
                deleteDialog.show(supportFragmentManager, "deleteCar")
            } else {
                cannotDeleteDialog.show(supportFragmentManager, "cannotDeleteCar")
            }
        }

        /** 광고 서포터즈 Checkbox 활성화/비활성화 */
        /*binding.cbCarSupporters.isEnabled(intent.getBooleanExtra("checkboxAvailable", false))
        if(!intent.getBooleanExtra("checkboxAvailable", false))
            binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR)*/

        /** 등록하기 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnCarAdd.isEnabled = it
            binding.btnCarAdd.isClickable = it
            binding.btnCarAdd.isFocusable = it
            binding.btnCarAdd.isSelected = it
            if(it) {
                binding.btnCarAdd.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnCarAdd.typeface = ResourcesCompat.getFont(this@CarInfoActivity, R.font.notosanskr_bold)
            } else {
                binding.btnCarAdd.setTextColor(getColor(R.color.grayscale_400))
                binding.btnCarAdd.typeface = ResourcesCompat.getFont(this@CarInfoActivity, R.font.notosanskr_medium)
            }
        })
        /** 등록하기 버튼 */
        binding.btnCarAdd.setOnClickListener {
            if(binding.cbCarSupporters.getCheckbox().isChecked && UserManager.hasMission) {
                cannotDeleteDialog.show(supportFragmentManager, "cannotDeleteCar")
            } else {
                viewModel.modifyCarInfo(intent.getIntExtra("vehicle_id", 0))
            }
        }

        /** LiveData 처리 */
        viewModel.myCarInfo.observe(binding.lifecycleOwner!!, {
            /** UI 처리부분 */
            /** EditText 정규식 설정 */
            binding.etCarModel.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isModelFocused) {
                    isModelFocused = true
                    binding.etCarModel.getEditText().addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if(!s.toString().isValidModel()) {
                                binding.etCarModel.setError("2자-30자 길이로 입력해주세요.")
                                isAllValid.postValue(false)
                            } else {
                                binding.etCarModel.setSuccess("차량 모델명 입력 완료")

                                if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                                    if(binding.etCarCompany.visibility != View.VISIBLE && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(binding.etCarCompany.visibility != View.VISIBLE && !binding.etCarOwner.hasError)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && !binding.etCarOwner.hasError)
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

            binding.etCarYear.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isYearFocused) {
                    isYearFocused = true
                    binding.etCarYear.getEditText().addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if(!s.toString().isValidYear() || s.toString().toInt() > 2021 || s.toString().toInt() < 1955) {
                                binding.etCarYear.setError("4자리의 연도로 입력해주세요.")
                                isAllValid.postValue(false)
                            } else {
                                binding.etCarYear.setSuccess("연식 입력 완료")

                                if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                                    if(binding.etCarCompany.visibility != View.VISIBLE && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(binding.etCarCompany.visibility != View.VISIBLE && !binding.etCarOwner.hasError)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && !binding.etCarOwner.hasError)
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

            binding.etCarNumber.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isCarNumberFocused) {
                    isCarNumberFocused = true
                    binding.etCarNumber.getEditText().addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if(!s.toString().isValidCarNumber()) {
                                binding.etCarNumber.setError("올바르지 않은 차량 번호입니다.")
                                isAllValid.postValue(false)
                            } else {
                                binding.etCarNumber.setSuccess("차량번호 입력 완료")

                                if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                                    if(binding.etCarCompany.visibility != View.VISIBLE && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(binding.etCarCompany.visibility != View.VISIBLE && !binding.etCarOwner.hasError)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && binding.etCarOwner.visibility != View.VISIBLE)
                                        isAllValid.postValue(true)
                                    else if(!binding.etCarCompany.hasError && !binding.etCarOwner.hasError)
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

            binding.etCarCompany.getEditText().addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if(!s.toString().isValidBrand()) {
                        binding.etCarCompany.setError("2자~20자 길이로 입력해주세요.")
                        isAllValid.postValue(false)
                    } else {
                        binding.etCarCompany.setSuccess("제조사 입력 완료")

                        if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                            if(binding.etCarCompany.visibility != View.VISIBLE && binding.etCarOwner.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(binding.etCarCompany.visibility != View.VISIBLE && !binding.etCarOwner.hasError)
                                isAllValid.postValue(true)
                            else if(!binding.etCarCompany.hasError && binding.etCarOwner.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(!binding.etCarCompany.hasError && !binding.etCarOwner.hasError)
                                isAllValid.postValue(true)
                            else
                                isAllValid.postValue(false)
                        } else {
                            isAllValid.postValue(false)
                        }
                    }
                }
            })
            binding.etCarOwner.getEditText().addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if(!s.toString().isValidOwner()) {
                        binding.etCarOwner.setError("2자~30자 길이로 입력해주세요.")
                        isAllValid.postValue(false)
                    } else {
                        binding.etCarOwner.setSuccess("차량소유주와의 관계 입력 완료")

                        if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                            if(binding.etCarCompany.visibility != View.VISIBLE && binding.etCarOwner.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(binding.etCarCompany.visibility != View.VISIBLE && !binding.etCarOwner.hasError)
                                isAllValid.postValue(true)
                            else if(!binding.etCarCompany.hasError && binding.etCarOwner.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(!binding.etCarCompany.hasError && !binding.etCarOwner.hasError)
                                isAllValid.postValue(true)
                            else
                                isAllValid.postValue(false)
                        } else {
                            isAllValid.postValue(false)
                        }
                    }
                }
            })

            /** Spinner 관련 설정 */
            /** 제조국 Button + 제조사 Spinner 관련 설정 */
            val koreanAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.korean_company).toList())
            koreanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val foreignAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.foreign_company).toList())
            foreignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCarCompany.adapter = koreanAdapter

            // 최초 상태 : '국산'이 선택되어 있음
            binding.btnCarKorean.setOnClickListener {
                viewModel.isKorean.postValue(true)
                binding.btnCarKorean.isSelected = true
                binding.btnCarForeign.isSelected = false
                binding.spCarCompany.adapter = koreanAdapter
            }
            binding.btnCarForeign.setOnClickListener {
                viewModel.isKorean.postValue(false)
                binding.btnCarKorean.isSelected = false
                binding.btnCarForeign.isSelected = true
                binding.spCarCompany.adapter = foreignAdapter
            }

            /** '차량소유주와의 관계' Spinner */
            val relationAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.car_owner).toList())
            relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCarOwner.adapter = relationAdapter
            binding.spCarOwner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //viewModel.owner.postValue(resources.getStringArray(R.array.car_owner)[0])
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position == resources.getStringArray(R.array.car_owner).size-1) {
                        binding.etCarOwner.visibility = View.VISIBLE
                    } else {
                        viewModel.owner.postValue(resources.getStringArray(R.array.car_owner)[position])
                        binding.etCarOwner.visibility = View.GONE

                        if (!binding.etCarModel.hasError && !binding.etCarYear.hasError && !binding.etCarNumber.hasError) {
                            if(binding.etCarCompany.visibility != View.VISIBLE)
                                isAllValid.postValue(true)
                            else if(!binding.etCarCompany.hasError)
                                isAllValid.postValue(true)
                            else
                                isAllValid.postValue(false)
                        } else
                            isAllValid.postValue(false)
                    }
                }
            }

            viewModel.isKorean.postValue(it.data.isForeignCar == 0)
            viewModel.company.postValue(it.data.brand)
            viewModel.modelName.postValue(it.data.model)
            viewModel.year.postValue(it.data.year.toString())
            viewModel.carNumber.postValue(it.data.carNumber)
            viewModel.isSupporters.postValue(it.data.supporters == 1)
            viewModel.owner.postValue(it.data.ownerRelationship)
            viewModel.isDeletable = it.data.isDelete

            if(it.data.isForeignCar == 0) {
                binding.btnCarKorean.isSelected = true
                binding.btnCarForeign.isSelected = false

                binding.spCarCompany.adapter = koreanAdapter
            } else {
                binding.btnCarKorean.isSelected = false
                binding.btnCarForeign.isSelected = true

                binding.spCarCompany.adapter = foreignAdapter
            }
            binding.spCarCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (viewModel.isKorean.value!!)
                        viewModel.company.postValue(resources.getStringArray(R.array.korean_company)[0])
                    else
                        viewModel.company.postValue(resources.getStringArray(R.array.foreign_company)[0])
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(viewModel.isKorean.value!!)
                        if(position == resources.getStringArray(R.array.korean_company).size-1) {
                            binding.etCarCompany.visibility = View.VISIBLE
                        } else {
                            viewModel.company.postValue(resources.getStringArray(R.array.korean_company)[position])
                            binding.etCarCompany.visibility = View.GONE

                            if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                                if(binding.etCarOwner.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarOwner.hasError)
                                    isAllValid.postValue(true)
                                else
                                    isAllValid.postValue(false)
                            } else {
                                isAllValid.postValue(false)
                            }
                        }
                    else {
                        if(position == resources.getStringArray(R.array.foreign_company).size-1) {
                            binding.etCarCompany.visibility = View.VISIBLE
                        } else {
                            viewModel.company.postValue(resources.getStringArray(R.array.foreign_company)[position])
                            binding.etCarCompany.visibility = View.GONE

                            if(!binding.etCarNumber.hasError && !binding.etCarModel.hasError && !binding.etCarYear.hasError) {
                                if(binding.etCarOwner.visibility != View.VISIBLE)
                                    isAllValid.postValue(true)
                                else if(!binding.etCarOwner.hasError)
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

            when(it.data.brand) {
                "현대" -> { binding.spCarCompany.setSelection(0) }
                "제네시스" -> { binding.spCarCompany.setSelection(1) }
                "기아" -> { binding.spCarCompany.setSelection(2) }
                "한국GM" -> { binding.spCarCompany.setSelection(3) }
                "르노삼성" -> { binding.spCarCompany.setSelection(4) }
                "쌍용" -> { binding.spCarCompany.setSelection(5) }

                "벤츠" -> { binding.spCarCompany.setSelection(0) }
                "BMW" -> { binding.spCarCompany.setSelection(1) }
                "아우디" -> { binding.spCarCompany.setSelection(2) }
                "폭스바겐" -> { binding.spCarCompany.setSelection(3) }
                "볼보" -> { binding.spCarCompany.setSelection(4) }
                "쉐보레" -> { binding.spCarCompany.setSelection(5) }
                "테슬라" -> { binding.spCarCompany.setSelection(6) }
                "미니" -> { binding.spCarCompany.setSelection(7) }
                "렉서스" -> { binding.spCarCompany.setSelection(8) }
                "닛산" -> { binding.spCarCompany.setSelection(9) }
                "도요타" -> { binding.spCarCompany.setSelection(10) }
                "혼다" -> { binding.spCarCompany.setSelection(11) }
                "인피니티" -> { binding.spCarCompany.setSelection(12) }
                "마세라티" -> { binding.spCarCompany.setSelection(13) }
                "람보르기니" -> { binding.spCarCompany.setSelection(14) }
                "포드" -> { binding.spCarCompany.setSelection(15) }
                "랜드로버" -> { binding.spCarCompany.setSelection(16) }
                "링컨" -> { binding.spCarCompany.setSelection(17) }
                "재규어" -> { binding.spCarCompany.setSelection(18) }
                "지프" -> { binding.spCarCompany.setSelection(19) }
                "페라리" -> { binding.spCarCompany.setSelection(20) }
                "포르쉐" -> { binding.spCarCompany.setSelection(21) }
                "푸조" -> { binding.spCarCompany.setSelection(22) }

                else  -> {
                    if(it.data.isForeignCar == 0)
                        binding.spCarCompany.setSelection(6)
                    else
                        binding.spCarCompany.setSelection(23)
                }
            }

            when(it.data.ownerRelationship) {
                "본인" -> { binding.spCarOwner.setSelection(0) }
                "배우자" -> { binding.spCarOwner.setSelection(1) }
                "부모" -> { binding.spCarOwner.setSelection(2) }
                "형제/자매" -> { binding.spCarOwner.setSelection(3) }
                else -> { binding.spCarOwner.setSelection(4) }
            }

            // 진행 중인 미션이 있고 supporters -> ONLY_ONE_CAR
            if(UserManager.hasMission && it.data.supporters == 1)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR)
            // 삭제 불가능하고 supporters -> ONLY_ONE_CAR
            else if(!it.data.isDelete && it.data.supporters == 1)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR)
            // 진행 중인 미션이 있고 supporters가 아님 -> ONLY_ONE_CAR_NOT_CHECKED
            else if(UserManager.hasMission && it.data.supporters == 0)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR_NOT_CHECKED)
            // 삭제 불가능하고 supporters가 아님 -> ONLY_ONE_CAR_NOT_CHECKED
            else if(!it.data.isDelete && it.data.supporters == 0)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR_NOT_CHECKED)
            else if(it.data.supporters == 1)
                binding.cbCarSupporters.setCurrentState(IS_SUPPORTERS_CAR)
            else
                binding.cbCarSupporters.setCurrentState(NOT_SUPPORTERS_CAR)
        })

        viewModel.response.observe(binding.lifecycleOwner!!, {
            if (it == DELETE_SUCCESS) {
                showToast("차량 " + viewModel.carNumber.value!! + "를 삭제했습니다.")
                setResult(RESULT_OK)
                finish()
            } else if (it == MODIFY_SUCCESS) {
                showToast("차량 정보를 수정했습니다.")
                setResult(RESULT_OK)
                finish()
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })
    }

    /** 로그아웃 PopupDialog의 interface의 콜백 함수 부분 */
    override fun onPositive() {
        viewModel.deleteCarInfo(intent.getIntExtra("vehicle_id", 0))
    }
    override fun onNegative() {}
}