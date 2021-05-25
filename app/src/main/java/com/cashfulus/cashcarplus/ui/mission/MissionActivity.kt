package com.cashfulus.cashcarplus.ui.mission

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMissionBinding
import com.cashfulus.cashcarplus.extension.setStartMargins
import com.cashfulus.cashcarplus.ui.adapter.*
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat
import java.util.*
import kotlin.properties.Delegates

class MissionActivity : BaseActivity() {
    private val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.
    var pageNum by Delegates.notNull<Int>()
    var currentPage = 0
    val numFormat = DecimalFormat("###,###")

    val loadingDialog: LoadingDialog by inject { parametersOf(this@MissionActivity) }
    private val binding by binding<ActivityMissionBinding>(R.layout.activity_mission)
    private val viewModel: MissionViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MissionActivity
            viewModel = this@MissionActivity.viewModel
        }

        /** 툴바 버튼 이벤트 */
        binding.toolbarMission.setLeftOnClick {
            finish()
        }

        /** LiveData 셋팅 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            // 최상단 슬라이드 이미지 설정
            val adapter = ImageSliderAdapter(this@MissionActivity, it.images)
            pageNum = it.images.size
            binding.vpMission.adapter = adapter
            binding.tlMission.setupWithViewPager(binding.vpMission, true)
            // 최상단 슬라이드 자동 재생
            val handler = Handler()
            val Update = Runnable {
                if (currentPage == pageNum) {
                    currentPage = 0
                }
                binding.vpMission.setCurrentItem(currentPage++, true)
            }
            // This will create a new Thread
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Update)
                }
            }, DELAY_MS, PERIOD_MS)
            // 최상단 슬라이드 자체 슬라이딩 시 포지션 초기화 작업
            binding.vpMission.setOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    currentPage = position
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })

            binding.tvMissionTitle.text = it.adUserInformation.title
            binding.tvMissionPoint.text = numFormat.format(it.adUserInformation.totalPoint)
            Glide.with(this@MissionActivity).load(it.adUserInformation.thumbnailImage).into(binding.ivMissionTitle)

            if(it.isMissionStart) {
                binding.tvMissionDateCurrent.visibility = View.VISIBLE

                binding.tvMissionDateStart.text = it.adUserInformation.activityStartDate.substring(0,10).replace("-",".") //2021.03.28
                binding.tvMissionDateEnd.text = it.adUserInformation.activityEndDate.substring(0,10).replace("-",".")
                binding.pbMissionDate.max = it.missionLength!!
                binding.pbMissionDate.progress = it.missionCurrentDate!!
                binding.tvMissionDateCurrent.text = it.missionCurrentDate!!.toString()+"일"

                binding.tvMissionDateCurrent.setStartMargins(20+(binding.pbMissionDate.width / it.missionLength!! * it.missionCurrentDate!!))
            } else {
                binding.tvMissionDateCurrent.visibility = View.GONE

                binding.tvMissionDateStart.text = "-"
                binding.tvMissionDateEnd.text = "-"
                binding.pbMissionDate.max = 100
                binding.pbMissionDate.progress = 0
            }

            if(it.importantMissions.size > 0) {
                binding.tvMissionTitle2.visibility = View.VISIBLE
                binding.rvMissionImportant.visibility = View.VISIBLE
                binding.lineMission3.visibility = View.VISIBLE

                binding.rvMissionImportant.adapter = MissionImportantRecyclerAdapter(this@MissionActivity, it.importantMissions, it.adUserInformation.title)
                binding.rvMissionImportant.layoutManager = LinearLayoutManager(this@MissionActivity)
            } else {
                binding.tvMissionTitle2.visibility = View.GONE
                binding.rvMissionImportant.visibility = View.GONE
                binding.lineMission3.visibility = View.GONE
            }

            if(it.additionalMissions.size > 0) {
                binding.tvMissionTitle3.visibility = View.VISIBLE
                binding.tvMissionSubtitle3.visibility = View.VISIBLE
                binding.rvMissionAdditional.visibility = View.VISIBLE
                binding.lineMission4.visibility = View.VISIBLE

                binding.rvMissionAdditional.adapter = MissionAdditionalRecyclerAdapter(this@MissionActivity, it.additionalMissions, it.adUserInformation.title)
                binding.rvMissionAdditional.layoutManager = LinearLayoutManager(this@MissionActivity)
            } else {
                binding.tvMissionTitle3.visibility = View.GONE
                binding.tvMissionSubtitle3.visibility = View.GONE
                binding.rvMissionAdditional.visibility = View.GONE
                binding.lineMission4.visibility = View.GONE
            }

            if(it.drivings.size > 0) {
                binding.tvMissionTitle4.visibility = View.VISIBLE
                binding.rvMissionDriving.visibility = View.VISIBLE
                binding.lineMission5.visibility = View.VISIBLE

                binding.rvMissionDriving.adapter = DrivingRecyclerAdapter(this@MissionActivity, it.drivings, it.adUserInformation.title)
                binding.rvMissionDriving.layoutManager = LinearLayoutManager(this@MissionActivity)
            } else {
                binding.tvMissionTitle4.visibility = View.GONE
                binding.rvMissionDriving.visibility = View.GONE
                binding.lineMission5.visibility = View.GONE
            }

            binding.tvMissionAssumePoint.text = numFormat.format(it.assumePoint)
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        viewModel.loading.observe(binding.lifecycleOwner!!, {
            if(it)
                loadingDialog.show()
            else
                loadingDialog.dismiss()
        })

        /** 최하단 주의사항 텍스트 일부 색상 설정 */
        val cautionSpannable = SpannableString(binding.tvMissionCaution.text.toString())
        val cautionStart = binding.tvMissionCaution.text.toString().indexOf("1:1문의")
        val cautionEnd = cautionStart + "1:1문의".length
        cautionSpannable.setSpan(ForegroundColorSpan(getColor(R.color.brand_orange1)), cautionStart, cautionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvMissionCaution.text = cautionSpannable
    }

    override fun onResume() {
        /** 페이지가 노출될 때마다 데이터를 새로 갱신함. */
        super.onResume()
        viewModel.loadData()
    }
}