package com.cashfulus.cashcarplus.ui.faq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityFaqBinding
import com.cashfulus.cashcarplus.ui.adapter.ExpandableFaqListAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_faq.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FaqActivity : BaseActivity() {
    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@FaqActivity) }
    private val binding by binding<ActivityFaqBinding>(R.layout.activity_faq)
    private val viewModel: FaqViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@FaqActivity
            viewModel = this@FaqActivity.viewModel
        }

        /** Toolbar 설정 */
        binding.toolbarFaq.setLeftOnClick {
            finish()
        }

        /** 공지사항 리스트 LiveData **/
        viewModel.faqList.observe(binding.lifecycleOwner!!, {
            val expandableAdapter = ExpandableFaqListAdapter(this@FaqActivity, it)
            binding.elvFaq.setAdapter(expandableAdapter)
        })

        /** 오류 LiveData **/
        viewModel.error.observe(binding.lifecycleOwner!!, {
            // 오류 발생 : 메세지를 출력하고 공지사항 종료
            showToast(it.message)
            finish()
        })

        /** '카톡 1:1 문의하기' 버튼 */
        binding.btnFaqGotoKakao.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.cashcar_kakao_url))))
        }
    }
}