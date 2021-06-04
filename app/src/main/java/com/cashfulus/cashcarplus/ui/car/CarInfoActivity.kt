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
import com.cashfulus.cashcarplus.view.ONLY_ONE_CAR
import com.cashfulus.cashcarplus.view.ONLY_ONE_CAR_NOT_CHECKED
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/** 2021.05.04 : 수정 가능/불가능 상태가 하나로 합쳐짐. */

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
        }
        binding.btnCarForeign.setOnClickListener {
            viewModel.isKorean.postValue(false)
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

        /** '차량소유주와의 관계' Spinner */
        val relationAdapter = ArrayAdapter(this, R.layout.row_spinner_origin, resources.getStringArray(R.array.car_owner).toList())
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCarOwner.adapter = relationAdapter
        binding.spCarOwner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.owner.postValue(resources.getStringArray(R.array.car_owner)[0])
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

        /** 광고 서포터즈 Checkbox 활성화/비활성화 */
        binding.cbCarSupporters.isEnabled(intent.getBooleanExtra("checkboxAvailable", false))
        if(!intent.getBooleanExtra("checkboxAvailable", false))
            binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR)

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
            viewModel.modifyCarInfo(intent.getIntExtra("vehicle_id", 0))
        }
        /** 최상단 제조국 버튼 */
        viewModel.isKorean.observe(binding.lifecycleOwner!!, {
            if(it) {
                binding.btnCarKorean.isSelected = true
                binding.btnCarForeign.isSelected = false

                binding.spCarCompany.adapter = koreanAdapter
            } else {
                binding.btnCarKorean.isSelected = false
                binding.btnCarForeign.isSelected = true

                binding.spCarCompany.adapter = foreignAdapter
            }
        })

        /** LiveData 처리 */
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

        viewModel.companyIndex.observe(binding.lifecycleOwner!!, {
            if (it == -1) {
                if (viewModel.isKorean.value!!) {
                    binding.spCarCompany.adapter = koreanAdapter
                    binding.spCarCompany.setSelection(6)
                } else {
                    binding.spCarCompany.adapter = foreignAdapter
                    binding.spCarCompany.setSelection(23)}

              } else {
                if (viewModel.isKorean.value!!)
                    binding.spCarCompany.adapter = koreanAdapter
                else
                    binding.spCarCompany.adapter = foreignAdapter
                binding.spCarCompany.setSelection(it)
            }

            /** 서포터즈 차량 체크박스 상태 */
            // 이미 광고 진행중이거나 삭제 불가능한 경우 -> 체크 변경 불가
            if(UserManager.hasMission || !viewModel.isDeletable!!)
                binding.cbCarSupporters.isEnabled(false)
            else
                binding.cbCarSupporters.isEnabled(true)

            // 삭제 불가능한 경우 : ONLY_ONE_CAR 상태
            if(!viewModel.isDeletable!!)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR)
            // 삭제는 가능한데 미션이 존재 : 체크박스 해제된 상태로 비활성화
            else if(UserManager.hasMission)
                binding.cbCarSupporters.setCurrentState(ONLY_ONE_CAR_NOT_CHECKED)
        })

        var isOwnerLoaded = false
        viewModel.owner.observe(binding.lifecycleOwner!!, {
            if(it != "];9(" && !isOwnerLoaded) { // it이 초기값이 아니고, 아직 로딩된 적이 없다면 체크.
                isOwnerLoaded = true
                when (it) {
                    "본인" -> { binding.spCarOwner.setSelection(0) }
                    "배우자" -> { binding.spCarOwner.setSelection(1) }
                    "부모" -> { binding.spCarOwner.setSelection(2) }
                    "형제/자매" -> { binding.spCarOwner.setSelection(3) }
                    else -> { binding.spCarOwner.setSelection(4) }
                }
            }
        })
    }

    /** 로그아웃 PopupDialog의 interface의 콜백 함수 부분 */
    override fun onPositive() {
        viewModel.deleteCarInfo(intent.getIntExtra("vehicle_id", 0))
    }
    override fun onNegative() {}
}