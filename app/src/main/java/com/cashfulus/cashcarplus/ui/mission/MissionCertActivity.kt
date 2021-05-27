package com.cashfulus.cashcarplus.ui.mission

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMissionCertBinding
import com.cashfulus.cashcarplus.ui.dialog.CameraBottomDialog
import com.cashfulus.cashcarplus.ui.dialog.CameraBottomDialogClickListener
import com.cashfulus.cashcarplus.util.isValidGauge
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MissionCertActivity : BaseActivity(), CameraBottomDialogClickListener {
    private val binding by binding<ActivityMissionCertBinding>(R.layout.activity_mission_cert)
    private val viewModel: MissionCertViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isGaugeFocused = false
    // 버튼 활성화/비활성화
    val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MissionCertActivity
            viewModel = this@MissionCertActivity.viewModel
        }

        /** 미션 인증, 추가 미션 인증 구분 */
        if(intent.getStringExtra("type") == "important") {
            binding.ivMissionCert3.visibility = View.VISIBLE
            binding.tvMissionCertTitle6.visibility = View.VISIBLE
            binding.etMissionCertMileage.visibility = View.VISIBLE
            binding.tvMissionCertKm.visibility = View.VISIBLE
            binding.tvMissionCertNum.text = "필수 미션 "+intent.getIntExtra("order", -1).toString()+"회차"
        } else if(intent.getStringExtra("type") == "additional") {
            binding.ivMissionCert3.visibility = View.GONE
            binding.tvMissionCertTitle6.visibility = View.GONE
            binding.etMissionCertMileage.visibility = View.GONE
            binding.tvMissionCertKm.visibility = View.GONE
            binding.tvMissionCertNum.text = "스티커 추가 인증"
        } else {
            binding.ivMissionCert3.visibility = View.GONE
            binding.tvMissionCertTitle6.visibility = View.GONE
            binding.etMissionCertMileage.visibility = View.GONE
            binding.tvMissionCertKm.visibility = View.GONE
            binding.tvMissionCertNum.text = "드라이빙 인증"
        }
        /** 상단의 미션 정보 표시 */
        binding.tvMissionCertTitle.text = intent.getStringExtra("title")
        if(intent.getStringExtra("endDate") == "0000-00-00 00:00:00")
            binding.tvMissionCertDate.text = "-" //2021년 3월 3일까지
        else {
            val dateTmp = intent.getStringExtra("endDate")!!.substring(0,10).split('-')
            binding.tvMissionCertDate.text = dateTmp[0]+"년 "+dateTmp[1]+"월 "+dateTmp[2]+"일까지" //2021년 3월 3일까지
        }
        /** 미션 ID 받아서 viewModel에 전달 */
        val missionId = intent.getIntExtra("id", -1)
        if(missionId == -1) {
            showToast("미션 정보를 제대로 불러오지 못했습니다. 다시 시도해 주세요.")
            finish()
        }

        /** 툴바 버튼 이벤트 */
        binding.toolbarMissionCert.setLeftOnClick {
            finish()
        }

        /** 주행거리 입력 폼 validation 등록 */
        binding.etMissionCertMileage.hasError = true
        if(intent.getStringExtra("type") == "important") {
            binding.etMissionCertMileage.getEditText().onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isGaugeFocused) {
                    isGaugeFocused = true
                    binding.etMissionCertMileage.getEditText().addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if(!binding.etMissionCertMileage.getEditText().text.toString().isValidGauge()) {
                                binding.etMissionCertMileage.setErrorWithoutMsg()
                                viewModel.gaugeError.postValue(true)
                                isAllValid.postValue(false)
                            }
                            else {
                                binding.etMissionCertMileage.setSuccessWithoutMsg()
                                viewModel.gaugeError.postValue(false)

                                if(viewModel.sideImg.value != null && viewModel.rearImg.value != null && viewModel.gaugeImg.value != null)
                                    isAllValid.postValue(true)
                            }
                        }
                    })
                }
            }
        }

        /** 사진 등록 */
        binding.ivMissionCert1.setOnClickListener {
            val cameraBottomDialog = CameraBottomDialog(1)
            cameraBottomDialog.show(supportFragmentManager, "Camera")
        }
        binding.ivMissionCert2.setOnClickListener {
            val cameraBottomDialog = CameraBottomDialog(2)
            cameraBottomDialog.show(supportFragmentManager, "Camera")
        }
        binding.ivMissionCert3.setOnClickListener {
            val cameraBottomDialog = CameraBottomDialog(3)
            cameraBottomDialog.show(supportFragmentManager, "Camera")
        }

        /** 미션 인증 등록 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnMissionCert.isSelected = it
            binding.btnMissionCert.isEnabled = it
            binding.btnMissionCert.isFocusable = it
            binding.btnMissionCert.isClickable = it

            if(it) {
                binding.btnMissionCert.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnMissionCert.typeface = ResourcesCompat.getFont(this@MissionCertActivity, R.font.notosanskr_bold)
            } else {
                binding.btnMissionCert.setTextColor(getColor(R.color.grayscale_400))
                binding.btnMissionCert.typeface = ResourcesCompat.getFont(this@MissionCertActivity, R.font.notosanskr_medium)
            }
        })
        binding.btnMissionCert.setOnClickListener {
            viewModel.uploadMission(missionId, intent.getStringExtra("type") != "important")
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {

            if(!it.dataNotNull) {
                showToast("일부 데이터가 전송되지 않았습니다. 다시 시도해 주세요.")
            } else if(!it.imageAllowed) {
                showToast("이미지 확장자가 잘못되었습니다. 다시 시도해 주세요.")
            } else if(!it.imageData) {
                showToast("이미지가 제대로 전송되지 않았습니다. 다시 시도해 주세요.")
            } else if(!it.missionData) {
                showToast("미션 데이터가 제대로 전송되지 않았습니다. 다시 시도해 주세요.")
                finish()
            } else if(!it.missionType) {
                showToast("미션 데이터가 제대로 전송되지 않았습니다. 다시 시도해 주세요.")
                finish()
            } else {
                showToast("미션 인증을 등록했습니다.")
                finish()
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })
    }

    /** 사진 촬영 후 cameraBottomDialog의 interface의 콜백 함수 부분 */
    override fun onClick(imgNum: Int, image: Bitmap) {
        when(imgNum) {
            1 -> {
                binding.ivMissionCert1.setImageBitmap(image)
                viewModel.sideImg.postValue(image)

                if(intent.getStringExtra("type") == "important") {
                    if (viewModel.rearImg.value != null && viewModel.gaugeImg.value != null && !binding.etMissionCertMileage.hasError)
                        isAllValid.postValue(true)
                }
                else {
                    if (viewModel.rearImg.value != null)
                        isAllValid.postValue(true)
                }
            }
            2 -> {
                binding.ivMissionCert2.setImageBitmap(image)
                viewModel.rearImg.postValue(image)

                if(intent.getStringExtra("type") == "important") {
                    if (viewModel.sideImg.value != null && viewModel.gaugeImg.value != null && !binding.etMissionCertMileage.hasError)
                        isAllValid.postValue(true)
                } else {
                    if (viewModel.sideImg.value != null)
                        isAllValid.postValue(true)
                }
            }
            3 -> {
                binding.ivMissionCert3.setImageBitmap(image)
                viewModel.gaugeImg.postValue(image)

                if(viewModel.sideImg.value != null && viewModel.rearImg.value != null && !binding.etMissionCertMileage.hasError)
                    isAllValid.postValue(true)
            }
        }
    }

    override fun onError(msg: String) = showToast(msg)
}