package com.cashfulus.cashcarplus.ui.alarm

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityAlarmBinding
import com.cashfulus.cashcarplus.ui.adapter.AlarmRecyclerAdapter
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmActivity : BaseActivity() {

    // Loading Dialog 및 MVVM 관련 객체들
    private val binding by binding<ActivityAlarmBinding>(R.layout.activity_alarm)
    private val viewModel: AlarmViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AlarmActivity
            viewModel = this@AlarmActivity.viewModel
        }

        /** Toolbar 셋팅 */
        binding.toolbarAlarm.setLeftOnClick {
            finish()
        }

        /** 알람 RecyclerView 셋팅 */
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvAlarm.layoutManager = linearLayoutManager
        val adapter = AlarmRecyclerAdapter(this@AlarmActivity)
        binding.rvAlarm.adapter = adapter

        /** 최상단 이벤트 감지 : 전체 Refresh (SwipeRefreshLayout 이용) */
        binding.srlAlarm.setOnRefreshListener {
            // 채널 RecyclerView를 새로고침 -> 데이터 로딩이 끝나면 아래의 alarmList의 observe로 이동.
            viewModel.getAlarmList()
        }

        /** ViewModel에 따른 View의 변화 반영 */
        viewModel.alarmList.observe(this@AlarmActivity, {
            adapter.changeList(it)
            // 새로고침 완료 : 새로고침 아이콘을 없앰.
            binding.srlAlarm.isRefreshing = false

            if(it.size == 0)
                showToast("새로운 알림이 없습니다.")
        })

        viewModel.error.observe(this@AlarmActivity, {
            Toast.makeText(this@AlarmActivity, it.message, Toast.LENGTH_LONG).show()
            // 새로고침 완료 : 새로고침 아이콘을 없앰.
            binding.srlAlarm.isRefreshing = false
        })
    }
}