package com.cashfulus.cashcarplus.ui.notice

import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityNoticeListBinding
import com.cashfulus.cashcarplus.ui.adapter.ExpandableNoticeListAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoticeListActivity : BaseActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@NoticeListActivity) }
    private val binding by binding<ActivityNoticeListBinding>(R.layout.activity_notice_list)
    private val viewModel: NoticeListViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@NoticeListActivity
            viewModel = this@NoticeListActivity.viewModel
        }

        /** Toolbar 설정 */
        binding.toolbarNoticeList.setLeftOnClick {
            finish()
        }

        /** 공지사항 리스트 LiveData **/
        viewModel.noticeList.observe(binding.lifecycleOwner!!, {
            val expandableAdapter = ExpandableNoticeListAdapter(this@NoticeListActivity, it.data)
            binding.elvNoticeList.setAdapter(expandableAdapter)
        })

        /** 오류 LiveData **/
        viewModel.error.observe(binding.lifecycleOwner!!, {
            // 오류 발생 : 메세지를 출력하고 공지사항 종료
            showToast(it.message)
            finish()
        })
    }
}