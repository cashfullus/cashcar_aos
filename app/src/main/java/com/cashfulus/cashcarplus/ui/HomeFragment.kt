package com.cashfulus.cashcarplus.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.databinding.FragmentHomeBinding
import com.cashfulus.cashcarplus.ui.alarm.AlarmActivity
import com.cashfulus.cashcarplus.ui.car.AddCarActivity
import com.cashfulus.cashcarplus.ui.dialog.MissionDialog
import com.cashfulus.cashcarplus.ui.dialog.MissionDialogClickListener
import com.cashfulus.cashcarplus.ui.dialog.WelcomeDialog
import com.cashfulus.cashcarplus.ui.mission.MissionActivity
import com.cashfulus.cashcarplus.util.UserManager
import com.cashfulus.cashcarplus.view.*
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    val numFormat = DecimalFormat("###,###")
    override val viewModel: HomeViewModel by viewModel { parametersOf() }

    // 광고 리스트 탭의 state(선택된 것)
    var tabState = "ongoing" //"scheduled", "done"

    // 광고 리스트 탭 adapter
    private val adapter by lazy { HomeAdapter(childFragmentManager) }

    override fun init() {
        /** Alarm Button 클릭 시 처리 */
        binding.btnHomeAlarm.setOnClickListener {
            startActivity(Intent(requireActivity(), AlarmActivity::class.java))
        }

        /** ViewModel 갱신 시 처리 */
        viewModel.currentMission.observe(binding.lifecycleOwner!!, {
            // 알람 관련 설정
            if(!it.data.data.isReadAlarm) {
                binding.btnHomeAlarm.setImageDrawable(getDrawable(requireActivity(), R.drawable.ic_alarm_none))
            } else {
                binding.btnHomeAlarm.setImageDrawable(getDrawable(requireActivity(), R.drawable.ic_alarm_new))
            }

            when (it.status) {
                // 신청한 미션이 없음
                HOME_STATE_NO_CAR -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission

                    card.background = requireActivity().getDrawable(R.drawable.button_mission_no_car)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "내 차량 정보 등록하기"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_bold)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(getColor(requireActivity(), R.color.grayscale_wt))

                    card.setOnClickListener {
                        requireActivity().startActivity(Intent(requireActivity(), AddCarActivity::class.java))
                    }

                    /** 최초 로그인 후 실행인 경우 띄움 */
                    val initSP = App().context().getSharedPreferences("started", MODE_PRIVATE)
                    if(!initSP.getBoolean("started", false)) {
                        WelcomeDialog().show(parentFragmentManager, "WelcomeDialog")
                        initSP.edit().putBoolean("started", true).apply()
                    }
                }
                HOME_STATE_NO_AD -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission

                    card.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_mission_no_mission)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "현재 진행 중인 서포터즈 활동이 없습니다"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_regular)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(getColor(requireActivity(), R.color.grayscale_500))
                }

                // 신청한 미션 검토중
                HOME_STATE_AD_WAITING -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "검토중"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener { }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    card.findViewById<Button>(R.id.btnCurrentMissionCancel).setOnClickListener {
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setMessage("서포터즈 신청을 취소할 시 추후 패널티를 받을 수 있습니다. 정말 신청을 취소하시겠습니까?")
                        builder.setPositiveButton("확인") { _: DialogInterface, _: Int -> viewModel.deleteMyMission() }
                        builder.setNegativeButton("취소", null)
                        builder.create().show()
                    }
                }
                // 신청한 미션 검토중, 1시간 지나서 삭제 불가능
                HOME_STATE_AD_WAITING_CANNOT_DELETABLE -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "검토중"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener { }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    card.findViewById<Button>(R.id.btnCurrentMissionCancel).visibility = View.GONE
                }
                // 신청한 광고가 조건에 맞지 않아서 운영자에 의해 취소됨
                HOME_STATE_AD_REGISTER_FAILED -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission

                    card.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_mission_no_mission)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "현재 진행 중인 서포터즈 활동이 없습니다"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_regular)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(getColor(requireActivity(), R.color.grayscale_500))

                    // 팝업을 아직 보지 않은 상태라면 띄움.
                    if(it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }
                        }

                        val dialog = MissionDialog(it.data.data.message.title, it.data.data.message.reason, "확인")
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }

                // 신청한 미션 진행중
                HOME_STATE_STAND_BY -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    if(it.data.data.adInformation!!.order == 0)
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_SUB_MISSION_START, "추가 미션 인증")
                    else
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_START, it.data.data.adInformation!!.order.toString()+"차 미션 인증")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent
                }
                HOME_STATE_REVIEW -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_VERIFY, "")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent
                }
                HOME_STATE_CURRENT_NO_MISSION -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_ALL_MISSION_EMPTY, "")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent
                }
                HOME_STATE_REJECT -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_RESEND, "")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // 팝업을 아직 보지 않은 상태라면 띄움.
                    if(it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                                val intent = Intent(requireActivity(), MissionActivity::class.java)
                                intent.putExtra("id", viewModel.UserApplyId)
                                startActivity(intent)
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }
                        }

                        val dialog = MissionDialog(it.data.data.message.title, it.data.data.message.reason, "재인증하기")
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_REJECT_ADDITIONAL -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_SUB_MISSION_RESEND, "")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // 팝업을 아직 보지 않은 상태라면 띄움.
                    if(it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                                val intent = Intent(requireActivity(), MissionActivity::class.java)
                                intent.putExtra("id", viewModel.UserApplyId)
                                startActivity(intent)
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }
                        }

                        val dialog = MissionDialog(it.data.data.message.title, it.data.data.message.reason, "재인증하기")
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_REAUTH -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_VERIFY, "")
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent
                }
                HOME_STATE_SUCCESS -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // 팝업을 아직 보지 않은 상태라면 띄움.
                    if(it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                                showToast("최종 포인트 미구현 상태")
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }
                        }

                        val dialog = MissionDialog(it.data.data.message.title, it.data.data.message.reason, "포인트 확인")
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_FAIL -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "일"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "회"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // 팝업을 아직 보지 않은 상태라면 띄움.
                    if(it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()도 이 함수 안에 있다.
                            }
                        }

                        val dialog = MissionDialog(it.data.data.message.title, it.data.data.message.reason, "확인")
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
            }
        })

        /** 아래 광고 리스트 */
        binding.vpHomeAd.adapter = HomeFragment@adapter
        binding.vpHomeAd.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpHomeAd.isUserInputEnabled = true
        TabLayoutMediator(binding.tlHomeAd, binding.vpHomeAd) { tab, position ->
            when(position) {
                0 -> {
                    tab.customView = getTabView(position)
                }
                1 -> {
                    tab.customView = getTabView(position)
                    /*tabState = "scheduled"
                    tab1Indicator.visibility = View.GONE
                    tab2Indicator.visibility = View.VISIBLE
                    tab3Indicator.visibility = View.GONE*/
                }
                2 -> {
                    tab.customView = getTabView(position)
                    /*tabState = "done"
                    tab1Indicator.visibility = View.GONE
                    tab2Indicator.visibility = View.GONE
                    tab3Indicator.visibility = View.VISIBLE*/
                }
            }
        }.attach()
        binding.tlHomeAd.setSelectedTabIndicatorColor(getColor(requireActivity(), android.R.color.transparent))

        viewModel.error.observe(binding.lifecycleOwner!!, {
            binding.srlHome.isRefreshing = false
            showToast(it.message)
        })

        viewModel.missionDeleted.observe(binding.lifecycleOwner!!, {
            // 미션 데이터 삭제 성공함 -> 전체 데이터를 다시 로딩함.
            viewModel.loadData()
        })

        /** 미션 카드 부분 Refresh */
        binding.srlHome.setOnRefreshListener {
            viewModel.loadData()
            binding.srlHome.isRefreshing = false
        }

        /*when(currentState) {
            CURRENT_MISSION_NO_CAR -> {
                val view = ConstraintLayout.LayoutInflater.from(context).inflate(R.layout.card_none_mission, binding.cardCurrentMission, false)
                view.findViewById<ConstraintLayout>(R.id.clCurrentMissionNone).background = ContextCompat.getDrawable(requireActivity(), R.drawable.button_mission_no_car)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "내 차량 정보 등록하기"
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_700)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(ContextCompat.getColor(requireActivity(), R.color.grayscale_wt))

                binding.cardCurrentMission.addView(view)
            }
            CURRENT_MISSION_NONE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.card_none_mission, binding.cardCurrentMission, false)
                view.findViewById<ConstraintLayout>(R.id.clCurrentMissionNone).background = ContextCompat.getDrawable(requireActivity(), R.drawable.button_mission_no_mission)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "현재 진행 중인 서포터즈 활동이 없습니다"
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_400)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
                view.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(ContextCompat.getColor(requireActivity(), R.color.grayscale_500))

                binding.cardCurrentMission.addView(view)
            }
            CURRENT_CONSIDERATION_DISABLE_CANCEL -> {
                val view = LayoutInflater.from(context).inflate(R.layout.card_current_mission, binding.cardCurrentMission, false)
                view.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.btnCurrentMissionCancel).visibility = View.GONE
                view.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE

                binding.cardCurrentMission.addView(view)
            }
            CURRENT_MAIN_MISSION_START -> {
                val view = LayoutInflater.from(context).inflate(R.layout.card_current_mission, binding.cardCurrentMission, false)
                view.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                view.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                view.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_START, "1차 미션 인증")

                binding.cardCurrentMission.addView(view)
            }
        }*/

        /*viewModel.currentMission.observe(binding.lifecycleOwner!!, Observer {
            binding.tvRowMissionTitleX.text = it.title
            binding.tvRowMissionContents.text = it.contents
            binding.pbRowMission.progress = it.progress
            binding.pbRowMission.max = it.finalDay
            binding.tvRowMissionDate.text = Integer.toString(it.progress)+" / "+Integer.toString(it.finalDay)+"일"
            binding.tvRowMissionPoint.text = numFormat.format(it.point)

            binding.tvRowMissionTitle2.text = Integer.toString(it.step)+"차 미션을 인증해 주세요"
            binding.tvRowMissionDate2.text = it.stepDate+"까지"
            binding.tvRowMissionMessage2.text = it.message
        })

        viewModel.adList.observe(binding.lifecycleOwner!!, Observer {
            /** 광고 리스트의 TabLayout 제목 셋팅 */
            binding.vpHomeAd.adapter = AdListViewPagerAdapter(this@HomeFragment, it)

            TabLayoutMediator(binding.tlHomeAd, binding.vpHomeAd) { tab, position ->
                when(position) {
                    0 -> tab.text = "모집중"
                    1 -> tab.text = "모집예정"
                    2 -> tab.text = "종료"
                    else -> tab.text = ""
                }
            }.attach()
        })

        viewModel.error.observe(binding.lifecycleOwner!!, Observer {
            if(viewModel.error.value != null) {
                showToast(viewModel.error.value!!.message)
            }
        })

        /** '인증하기' 버튼 클릭 시 */
        binding.btnRowMission2.setOnClickListener {
            startActivity(Intent(requireActivity(), MissionCheckActivity::class.java))
        }

        /** '더보기' 버튼 클릭 시 */
        binding.tvHomeAdMore.setOnClickListener {
            startActivity(Intent(requireActivity(), AdListActivity::class.java))
        }*/

        /*val adapter = YoutuberListAdapter(requireActivity())

        /** 채널 리스트 RecyclerView 관련 설정 */
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvHome.layoutManager = linearLayoutManager
        rvHome.adapter = adapter

        rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 최하단 이벤트 감지 : 추가로 몇십개의 데이터 갱신
                if(!recyclerView.canScrollVertically(1)) {

                }
            }
        })

        /** 최상단 이벤트 감지 : 전체 Refresh (SwipeRefreshLayout 이용) */
        srlHome.setOnRefreshListener {
            // 채널 RecyclerView를 새로고침 -> 데이터 로딩이 끝나면 아래의 channelList의 observe로 이동.
            mViewModel.refreshChannelList()
        }*/
    }

    fun getTabView(position: Int) : View {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.tab_home, null, false)
        val tab1Indicator = view.findViewById<ImageView>(R.id.indicatorTabHome)
        val tab1Tv = view.findViewById<TextView>(R.id.tvTabHome)

        when (position) {
            0 -> {
                tab1Tv.text = "모집중"
                tabState = "ongoing"
                tab1Indicator.visibility = View.VISIBLE
            }
            1 -> {
                tab1Tv.text = "모집예정"
                tabState = "scheduled"
                tab1Indicator.visibility = View.VISIBLE
            }
            2 -> {
                tab1Tv.text = "종료"
                tabState = "done"
                tab1Indicator.visibility = View.VISIBLE
            }
        }

        return view
    }

    /** Home Tab으로 넘어올 때마다 데이터를 다시 로드함. */
    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    inner class HomeAdapter(fa: FragmentManager) : FragmentStateAdapter(fa, lifecycle) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                // scheduled(예정) ongoing(진행중) done(모집완료)
                0       -> AdListFragment.newInstance("ongoing")
                1       -> AdListFragment.newInstance("scheduled")
                2       -> AdListFragment.newInstance("done")
                else -> AdListFragment.newInstance("ongoing")
            }
        }
    }
}