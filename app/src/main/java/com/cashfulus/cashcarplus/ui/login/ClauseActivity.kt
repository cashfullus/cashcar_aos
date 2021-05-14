package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityClauseBinding
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.android.synthetic.main.activity_clause.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val CLAUSE_AGREED = 0
const val CLAUSE_CANCELED = -1

class ClauseActivity : BaseActivity() {
    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@ClauseActivity) }
    private val binding by binding<ActivityClauseBinding>(R.layout.activity_clause)

    // Button Validation
    private val isAllValid = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@ClauseActivity
        }

        // 로그인 방식 : kakao, email
        val activity = intent.getStringExtra("method")
        val currentEmail = intent.getStringExtra("email")

        /** Toolbar Click Event */
        binding.toolbarCluase.setLeftOnClick {
            finish()
        }

        // 약관 동의 여부 체크
        val clauseAgreed = ArrayList<Boolean>()
        clauseAgreed.add(false)
        clauseAgreed.add(false)
        clauseAgreed.add(false)
        clauseAgreed.add(false)
        clauseAgreed.add(false)
        // clauseAgreed와 체크 상태를 연동하기 위한 ArrayList
        val clauseView = ArrayList<CheckBox>()
        clauseView.add(cbClauseService)
        clauseView.add(cbClausePolicy)
        clauseView.add(cbClauseGPS)
        clauseView.add(cbClauseAdvertisement)
        clauseView.add(cbClauseMarketing)

        val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if(activityResult.data != null) {
                when (activityResult.resultCode) {
                    CLAUSE_AGREED -> {
                        //clauseAgreed[activityResult.data!!.getIntExtra("number", 0)] = true
                        clauseView[activityResult.data!!.getIntExtra("number", 0)].isChecked = true
                    }
                    CLAUSE_CANCELED -> {
                        //clauseAgreed[activityResult.data!!.getIntExtra("number", 0)] = false
                        //clauseView[activityResult.data!!.getIntExtra("number", 0)].isChecked = false
                    }
                }
            }
        }

        /** 전체 동의 */
        cbClauseAll.setOnCheckedChangeListener { buttonView, isChecked ->
            clauseAgreed[0] = isChecked
            clauseView[0].isChecked = isChecked
            clauseAgreed[1] = isChecked
            clauseView[1].isChecked = isChecked
            clauseAgreed[2] = isChecked
            clauseView[2].isChecked = isChecked
            clauseAgreed[3] = isChecked
            clauseView[3].isChecked = isChecked
            clauseAgreed[4] = isChecked
            clauseView[4].isChecked = isChecked

            isAllValid.postValue(isChecked)
        }

        tvClauseAll.setOnClickListener {
            cbClauseAll.isChecked = !cbClauseAll.isChecked
        }

        /** 각 체크박스에 대한 이벤트 */
        clauseView[0].setOnCheckedChangeListener { buttonView, isChecked ->
            clauseAgreed[0] = isChecked

            if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2] && clauseAgreed[3] && clauseAgreed[4]) {
                cbClauseAll.setChecked(true)
                isAllValid.postValue(true)
            } else if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2])
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
        clauseView[1].setOnCheckedChangeListener { buttonView, isChecked ->
            clauseAgreed[1] = isChecked

            if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2] && clauseAgreed[3] && clauseAgreed[4]) {
                cbClauseAll.setChecked(true)
                isAllValid.postValue(true)
            } else if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2])
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
        clauseView[2].setOnCheckedChangeListener { buttonView, isChecked ->
            clauseAgreed[2] = isChecked

            if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2] && clauseAgreed[3] && clauseAgreed[4]) {
                cbClauseAll.setChecked(true)
                isAllValid.postValue(true)
            } else if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2])
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
        clauseView[3].setOnCheckedChangeListener { buttonView, isChecked ->
            clauseAgreed[3] = isChecked

            if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2] && clauseAgreed[3] && clauseAgreed[4]) {
                cbClauseAll.setChecked(true)
                isAllValid.postValue(true)
            } else if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2])
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }
        clauseView[4].setOnCheckedChangeListener { _, isChecked ->
            clauseAgreed[4] = isChecked

            if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2] && clauseAgreed[3] && clauseAgreed[4]) {
                cbClauseAll.setChecked(true)
                isAllValid.postValue(true)
            } else if(clauseAgreed[0] && clauseAgreed[1] && clauseAgreed[2])
                isAllValid.postValue(true)
            else
                isAllValid.postValue(false)
        }

        /** 약관 세부 페이지로 이동 */
        llCluaseService.setOnClickListener {
            cbClauseService.isClickable = true
            cbClauseService.isFocusable = true

            val intent = Intent(this@ClauseActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("number", 0)
            intent.putExtra("title", "서비스 이용약관")
            intent.putExtra("url", getString(R.string.url_clause_service))
            requestActivity.launch(intent)
        }

        llCluasePolicy.setOnClickListener {
            cbClausePolicy.isClickable = true
            cbClausePolicy.isFocusable = true

            val intent = Intent(this@ClauseActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("number", 1)
            intent.putExtra("title", "개인정보처리방침")
            intent.putExtra("url", getString(R.string.url_privacy_policy))
            requestActivity.launch(intent)
        }

        llCluaseGPS.setOnClickListener {
            cbClauseGPS.isClickable = true
            cbClauseGPS.isFocusable = true

            val intent = Intent(this@ClauseActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("number", 2)
            intent.putExtra("title", "위치기반서비스 이용약관")
            intent.putExtra("url", getString(R.string.url_location_based_service))
            requestActivity.launch(intent)
        }

        cbClauseAdvertisement.isClickable = true
        cbClauseAdvertisement.isFocusable = true

        llCluaseMarketing.setOnClickListener {
            cbClauseMarketing.isClickable = true
            cbClauseMarketing.isFocusable = true

            val intent = Intent(this@ClauseActivity, CluaseWebviewActivity::class.java)
            intent.putExtra("number", 4)
            intent.putExtra("title", "마케팅 정보 수신 동의")
            intent.putExtra("url", getString(R.string.url_marketing_information))
            requestActivity.launch(intent)
        }

        /** '동의하기' 버튼 활성화/비활성화 */
        isAllValid.observe(binding.lifecycleOwner!!, {
            binding.btnClauseStart.isEnabled = it
            binding.btnClauseStart.isClickable = it
            binding.btnClauseStart.isFocusable = it
            binding.btnClauseStart.isSelected = it
            if(it) {
                binding.btnClauseStart.setTextColor(getColor(R.color.grayscale_wt))
                binding.btnClauseStart.typeface = ResourcesCompat.getFont(this@ClauseActivity, R.font.notosanskr_bold)
            } else {
                binding.btnClauseStart.setTextColor(getColor(R.color.grayscale_400))
                binding.btnClauseStart.typeface = ResourcesCompat.getFont(this@ClauseActivity, R.font.notosanskr_medium)
            }
        })
        /** '동의하기' 버튼 */
        btnClauseStart.setOnClickListener {
            when(activity) {
                "normal" -> {
                    val intentRegister = Intent(this@ClauseActivity, RegisterBasicActivity::class.java)
                    intentRegister.putExtra("alarm", clauseAgreed[3])
                    intentRegister . putExtra ("marketing", clauseAgreed[4])
                    startActivity(intentRegister)
                }
                "kakao" -> {
                    val intentRegister = Intent(this@ClauseActivity, RegisterActivity::class.java)
                    intentRegister.putExtra("alarm", clauseAgreed[3])
                    intentRegister.putExtra ("marketing", clauseAgreed[4])
                    intentRegister.putExtra("method", "kakao")
                    UserManager.email = currentEmail
                    startActivity(intentRegister)
                }
            }
        }
    }
}