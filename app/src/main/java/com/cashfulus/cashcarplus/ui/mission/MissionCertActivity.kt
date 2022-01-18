package com.cashfulus.cashcarplus.ui.mission

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMissionCertBinding
import com.cashfulus.cashcarplus.ui.dialog.CameraBottomDialog
import com.cashfulus.cashcarplus.ui.dialog.CameraBottomDialogClickListener
import com.cashfulus.cashcarplus.util.GPSUtil
import com.cashfulus.cashcarplus.util.isValidGauge
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MissionCertActivity : BaseActivity(), CameraBottomDialogClickListener {
    private val binding by binding<ActivityMissionCertBinding>(R.layout.activity_mission_cert)
    private val viewModel: MissionCertViewModel by viewModel { parametersOf() }

    // 각 UpgradedEditText가 Focus된 적이 있는지 체크.
    var isGaugeFocused = false
    // 버튼 활성화/비활성화
    val isAllValid = MutableLiveData<Boolean>(false)

    /*var latitude: Double? = null
    var longitude: Double? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MissionCertActivity
            viewModel = this@MissionCertActivity.viewModel
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /** 권한 확인 */
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                showToast("카메라 관련 권한이 거부되었습니다.")
                finish()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("필수 권한 거부 시 앱 이용이 어려울 수 있습니다.\n\n[설정] > [권한]에서 필수 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) //, Manifest.permission.READ_PHONE_STATE
                .check()

        /*val permissionlistener2: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MissionCertActivity)
                if (ActivityCompat.checkSelfPermission(this@MissionCertActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MissionCertActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            Log.d("Test", "GPS Location Latitude: $latitude" +
                                    ", Longitude: $longitude")
                        }
                    }
                }

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                showToast("위치 관련 권한이 거부되었습니다.")
                finish()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionlistener2)
                .setDeniedMessage("필수 권한 거부 시 앱 이용이 어려울 수 있습니다.\n\n[설정] > [권한]에서 필수 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check()*/

        /** 미션 인증, 추가 미션 인증 구분 */
        if(intent.getStringExtra("type") == "important") {
            binding.cvMissionCert3.visibility = View.VISIBLE
            binding.tvMissionCertTitle6.visibility = View.VISIBLE
            binding.etMissionCertMileage.visibility = View.VISIBLE
            binding.tvMissionCertKm.visibility = View.VISIBLE
            binding.tvMissionCertNum.text = "필수 미션 "+intent.getIntExtra("order", -1).toString()+"회차"
            binding.tvMissionCertText.text = "1. 차량 뒷면, 옆면에 붙인 스티커를 찍어주세요\\n2. 계기판 사진을 찍고 주행거리를 입력해주세요"
        } else if(intent.getStringExtra("type") == "additional") {
            binding.cvMissionCert3.visibility = View.GONE
            binding.tvMissionCertTitle6.visibility = View.GONE
            binding.etMissionCertMileage.visibility = View.GONE
            binding.tvMissionCertKm.visibility = View.GONE
            binding.tvMissionCertNum.text = intent.getStringExtra("missionname")
            binding.tvMissionCertText.text = "차량 뒷면, 옆면에 붙인 스티커를 찍어주세요"
        } else {
            binding.cvMissionCert3.visibility = View.GONE
            binding.tvMissionCertTitle6.visibility = View.GONE
            binding.etMissionCertMileage.visibility = View.GONE
            binding.tvMissionCertKm.visibility = View.GONE
            binding.tvMissionCertNum.text = "드라이빙 인증"
            binding.tvMissionCertText.text = "차량 뒷면, 옆면에 붙인 스티커를 찍어주세요"
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