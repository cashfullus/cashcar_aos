package com.cashfulus.cashcarplus.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.base.TestActivity
import com.cashfulus.cashcarplus.databinding.FragmentHomeBinding
import com.cashfulus.cashcarplus.model.AdResponse
import com.cashfulus.cashcarplus.ui.adapter.AdRecyclerAdapter
import com.cashfulus.cashcarplus.ui.alarm.AlarmActivity
import com.cashfulus.cashcarplus.ui.car.AddCarActivity
import com.cashfulus.cashcarplus.ui.dialog.*
import com.cashfulus.cashcarplus.ui.howtouse.HowToUseActivity
import com.cashfulus.cashcarplus.ui.mission.MissionActivity
import com.cashfulus.cashcarplus.ui.mission.MissionCertActivity
import com.cashfulus.cashcarplus.util.UserManager
import com.cashfulus.cashcarplus.view.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.dialog_popup.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    val numFormat = DecimalFormat("###,###")
    override val viewModel: HomeViewModel by viewModel { parametersOf() }

    // ?????? ????????? ?????? state(????????? ???)
    var tabState = "ongoing" //"scheduled", "done"
    // ??? ???????????? ?????? ??????
    val adNum: Array<Int> = arrayOf(0, 0, 0)

    // ?????? ????????? ?????? BroadcastReceiver
    private var receiver: BroadcastReceiver? = null
    private var mIsReceiverRegistered = false

    override fun init() {
        /** Alarm Button ?????? ??? ?????? */
        binding.btnHomeAlarm.setOnClickListener {
            startActivity(Intent(requireActivity(), AlarmActivity::class.java))
        }

        /** ViewModel ?????? ??? ?????? */
        viewModel.currentMission.observe(binding.lifecycleOwner!!, {
            // ?????? ?????????(it??? ??? ?????? ????????? ?????? ????????? ??????)
            val dataResult = it.data

            if (!it.data.data.isReadAlarm) {
                binding.btnHomeAlarm.setImageDrawable(
                        getDrawable(
                                requireActivity(),
                                R.drawable.ic_alarm_none
                        )
                )
            } else {
                binding.btnHomeAlarm.setImageDrawable(
                        getDrawable(
                                requireActivity(),
                                R.drawable.ic_alarm_new
                        )
                )
            }

            when (it.status) {
                // ????????? ????????? ??????
                HOME_STATE_NO_CAR -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            resources.displayMetrics
                    ).toInt()

                    card.background = requireActivity().getDrawable(R.drawable.button_mission_no_car)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text = "??? ?????? ?????? ????????????"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface = ResourcesCompat.getFont(requireActivity(), R.font.notosanskr_bold)
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16f
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(
                            getColor(
                                    requireActivity(),
                                    R.color.grayscale_wt
                            )
                    )

                    card.setOnClickListener {
                        requireActivity().startActivity(
                                Intent(
                                        requireActivity(),
                                        AddCarActivity::class.java
                                )
                        )
                    }

                    /** ?????? ????????? ??? ????????? ?????? ?????? */
                    val initSP = App().context().getSharedPreferences("started", MODE_PRIVATE)
                    if (!initSP.getBoolean("started", false)) {
                        WelcomeDialog().show(parentFragmentManager, "WelcomeDialog")
                        initSP.edit().putBoolean("started", true).apply()
                    }
                }
                HOME_STATE_NO_AD -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            resources.displayMetrics
                    ).toInt()

                    card.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_mission_no_mission
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text =
                            "???????????? ????????? ????????? ????????? :)"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface =
                            ResourcesCompat.getFont(
                                    requireActivity(),
                                    R.font.notosanskr_regular
                            )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16f
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(
                            getColor(
                                    requireActivity(),
                                    R.color.grayscale_500
                            )
                    )

                    card.setOnClickListener {}
                }

                // ????????? ?????? ?????????
                HOME_STATE_AD_WAITING -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            152f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.VISIBLE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "?????????"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener { }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    card.findViewById<Button>(R.id.btnCurrentMissionCancel).setOnClickListener {
                        val popupDialog = CancelMissionPopupDialog(
                                "???????????? ????????? ????????? ??? ?????? ???????????? ?????? ??? ????????????.\n?????? ????????? ?????????????????????????",
                                "??????",
                                "??????",
                                { viewModel.deleteMyMission() })
                        popupDialog.show(parentFragmentManager, "CancelSupporters")
                    }
                }
                // ????????? ?????? ?????????, 1?????? ????????? ?????? ?????????
                HOME_STATE_AD_WAITING_CANNOT_DELETABLE -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            152f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.VISIBLE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "?????????"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener { }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    card.findViewById<Button>(R.id.btnCurrentMissionCancel).visibility = View.GONE
                    card.setOnClickListener {}
                }
                // ????????? ????????? ????????? ?????? ????????? ???????????? ?????? ?????????
                HOME_STATE_AD_REGISTER_FAILED -> {
                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            resources.displayMetrics
                    ).toInt()

                    card.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_mission_no_mission
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text =
                            "???????????? ????????? ????????? ????????? :)"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface =
                            ResourcesCompat.getFont(
                                    requireActivity(),
                                    R.font.notosanskr_regular
                            )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16f
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(
                            getColor(
                                    requireActivity(),
                                    R.color.grayscale_500
                            )
                    )

                    // ????????? ?????? ?????? ?????? ???????????? ??????.
                    if (it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }
                        }

                        val dialog = MissionDialog(
                                it.data.data.message.title,
                                it.data.data.message.reason,
                                "??????"
                        )
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }

                // ????????? ?????? ?????????
                HOME_STATE_STAND_BY -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            212f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.VISIBLE
                    if (it.data.data.adInformation!!.order == 0) {
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_SUB_MISSION_START, "?????? ?????? ??????")
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                                    val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                    intent.putExtra("type", "additional")
                                    intent.putExtra("title", dataResult.data.adInformation.title)
                                    intent.putExtra("missionname", dataResult.data.adInformation.missionName)
                                    intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                    intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                    startActivity(intent)
                                }
                    } else {
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_MAIN_MISSION_START, it.data.data.adInformation!!.order.toString() + "??? ?????? ??????")
                        card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setOnClickListener {
                                    val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                    intent.putExtra("type", "important")
                                    intent.putExtra("title", dataResult.data.adInformation.title)
                                    intent.putExtra("order", dataResult.data.adInformation.order)
                                    intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                    intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                    startActivity(intent)
                                }
                    }

                    card.setOnClickListener {
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
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            212f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(
                            CURRENT_MAIN_MISSION_VERIFY,
                            ""
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener { }
                    card.setOnClickListener {
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
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            152f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(
                            CURRENT_ALL_MISSION_EMPTY,
                            ""
                    )
                    card.setOnClickListener {
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
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            212f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(
                            CURRENT_MAIN_MISSION_RESEND,
                            ""
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener {
                                val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                intent.putExtra("type", "important")
                                intent.putExtra("title", dataResult.data.adInformation.title)
                                intent.putExtra("order", dataResult.data.adInformation.order)
                                intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                startActivity(intent)
                            }
                    card.setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // ????????? ?????? ?????? ?????? ???????????? ??????.
                    if (it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                                val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                intent.putExtra("type", "important")
                                intent.putExtra("title", dataResult.data.adInformation.title)
                                intent.putExtra("order", dataResult.data.adInformation.order)
                                intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                startActivity(intent)
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }
                        }

                        val dialog = MissionDialog(
                                it.data.data.message.title,
                                it.data.data.message.reason,
                                "???????????????"
                        )
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_REJECT_ADDITIONAL -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            212f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(
                            CURRENT_SUB_MISSION_RESEND,
                            ""
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener {
                                val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                intent.putExtra("type", "additional")
                                intent.putExtra("title", dataResult.data.adInformation.title)
                                intent.putExtra("missionname", dataResult.data.adInformation.missionName)
                                intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                startActivity(intent)
                            }
                    card.setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    // ????????? ?????? ?????? ?????? ???????????? ??????.
                    if (it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                                val intent = Intent(requireActivity(), MissionCertActivity::class.java)
                                intent.putExtra("type", "additional")
                                intent.putExtra("title", dataResult.data.adInformation.title)
                                intent.putExtra("missionname", dataResult.data.adInformation.missionName)
                                intent.putExtra("endDate", dataResult.data.adInformation.missionEndDate)
                                intent.putExtra("id", dataResult.data.adInformation.adMissionCardUserId)
                                startActivity(intent)
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }
                        }

                        var btnString = "???????????????"
                        if(dataResult.data.adInformation.title.contains("?????? ????????? ?????????????????????")) {
                            btnString = "??????"
                        }

                        val dialog = MissionDialog(
                                it.data.data.message.title,
                                it.data.data.message.reason,
                                btnString
                        )
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_REAUTH -> {
                    UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            212f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility =
                            View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(
                            card.findViewById(
                                    R.id.ivCurrentMission
                            )
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text =
                            it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(
                            it.data.data.adInformation!!.point
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility =
                            View.VISIBLE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(
                            CURRENT_MAIN_MISSION_VERIFY,
                            ""
                    )
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission)
                            .setOnClickListener { }
                    card.setOnClickListener {
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
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            152f,
                            resources.displayMetrics
                    ).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text =
                            it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text =
                            it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text =
                            it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).setState(CURRENT_ALL_MISSION_EMPTY, "")
                    card.setOnClickListener {
                        val intent = Intent(requireActivity(), MissionActivity::class.java)
                        intent.putExtra("id", viewModel.UserApplyId)
                        startActivity(intent)
                    }

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent

                    /** ???????????? ?????? ??? '???????????? ????????? ????????? ????????? :)'??? ??????. */
                    /*UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            resources.displayMetrics
                    ).toInt()

                    card.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_mission_no_mission
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text =
                            "???????????? ????????? ????????? ????????? :)"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface =
                            ResourcesCompat.getFont(
                                    requireActivity(),
                                    R.font.notosanskr_regular
                            )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16f
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(
                            getColor(
                                    requireActivity(),
                                    R.color.grayscale_500
                            )
                    )

                    card.setOnClickListener {}*/

                    // ????????? ?????? ?????? ?????? ???????????? ??????.
                    if (it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }
                        }

                        val dialog = MissionDialog(
                                it.data.data.message.title,
                                it.data.data.message.reason,
                                "????????? ??????"
                        )
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
                HOME_STATE_FAIL -> {
                    /*UserManager.hasMission = true
                    binding.cardNoneMission.visibility = View.GONE
                    binding.cardCurrentMission.visibility = View.VISIBLE
                    val card = binding.cardCurrentMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 152f, resources.displayMetrics).toInt()

                    card.findViewById<ConstraintLayout>(R.id.clCurrentMissionNotReady).visibility = View.GONE
                    Glide.with(requireActivity()).load(it.data.data.adInformation!!.logoImage).into(card.findViewById(R.id.ivCurrentMission))
                    card.findViewById<TextView>(R.id.tvCurrentMissionTitle).text = it.data.data.adInformation!!.title
                    /*if(it.data.data.adInformation!!.ongoingDays == 0)
                        card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = "-"
                    else*/
                    card.findViewById<TextView>(R.id.tvCurrentMissionDate).text = it.data.data.adInformation!!.ongoingDays.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionSuccess).text = it.data.data.adInformation!!.defaultMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionAdditional).text = it.data.data.adInformation!!.additionalMissionSuccessCount.toString() + "???"
                    card.findViewById<TextView>(R.id.tvCurrentMissionPoint).text = numFormat.format(it.data.data.adInformation!!.point)
                    card.findViewById<CurrentMissionButton>(R.id.btnCurrentMission).visibility = View.GONE

                    val pbMission = card.findViewById<ProgressBar>(R.id.pbRowMission)
                    pbMission.max = 100
                    pbMission.progress = it.data.data.adInformation!!.ongoingDayPercent*/

                    UserManager.hasMission = false
                    binding.cardNoneMission.visibility = View.VISIBLE
                    binding.cardCurrentMission.visibility = View.GONE
                    val card = binding.cardNoneMission
                    binding.srlHome.layoutParams.height = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            resources.displayMetrics
                    ).toInt()

                    card.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_mission_no_mission
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).text =
                            "???????????? ????????? ????????? ????????? :)"
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).typeface =
                            ResourcesCompat.getFont(
                                    requireActivity(),
                                    R.font.notosanskr_regular
                            )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextSize(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16f
                    )
                    card.findViewById<TextView>(R.id.tvCurrentMissionNone).setTextColor(
                            getColor(
                                    requireActivity(),
                                    R.color.grayscale_500
                            )
                    )

                    // ????????? ?????? ?????? ?????? ???????????? ??????.
                    if (it.data.data.message.isRead == 0) {
                        val listener = object : MissionDialogClickListener {
                            override fun onPositiveClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }

                            override fun onNegativeClick() {
                                viewModel.messageRead(it.data.data.message.reasonId) //loadData()??? ??? ?????? ?????? ??????.
                            }
                        }

                        val dialog = MissionDialog(
                                it.data.data.message.title,
                                it.data.data.message.reason,
                                "??????"
                        )
                        dialog.listener = listener
                        dialog.show(parentFragmentManager, "MissionDialog")
                    }
                }
            }

            /** Sticky ScrollView ?????? ?????? */
            binding.usvHome.run {
                header = binding.tlHomeAd
                binding.usvHome.onGlobalLayout()

                stickListener = { _ ->
                    binding.btnHomePageUp.visibility = View.VISIBLE
                }
                freeListener = { _ ->
                    binding.btnHomePageUp.visibility = View.GONE
                }
            }

            // ?????? ?????? ?????? ??? ??????????????? ?????????.
            binding.usvHome.smoothScrollTo(0, 0)
        })

        /** ?????? ?????? ????????? */
        viewModel.loadAllAdList()
        viewModel.adList.observe(binding.lifecycleOwner!!, {
            adNum[0] = if(it[0].size < 2) 2 else if (it[0].size % 2 == 0) it[0].size / 2 else it[0].size / 2 + 1
            adNum[1] = if(it[1].size < 2) 2 else if (it[1].size % 2 == 0) it[1].size / 2 else it[1].size / 2 + 1
            adNum[2] = if(it[2].size < 2) 2 else if (it[2].size % 2 == 0) it[2].size / 2 else it[2].size / 2 + 1

            var clRowAd: ConstraintLayout? = null

            binding.vpHomeAd.adapter = MyAdapter(requireActivity(), it)//HomeFragment@adapter
            binding.vpHomeAd.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.vpHomeAd.isUserInputEnabled = true
            TabLayoutMediator(binding.tlHomeAd, binding.vpHomeAd) { tab, position ->
                when (position) {
                    0 -> {
                        tab.customView = getTabView(position)
                    }
                    1 -> {
                        tab.customView = getTabView(position)
                    }
                    2 -> {
                        tab.customView = getTabView(position)
                    }
                }
            }.attach()
            binding.tlHomeAd.setSelectedTabIndicatorColor(
                    getColor(
                            requireActivity(),
                            android.R.color.transparent
                    )
            )

            binding.vpHomeAd.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (clRowAd == null) {
                        clRowAd = requireActivity().findViewById(R.id.clRowAd)

                        clRowAd!!.post {
                            val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                                    clRowAd!!.width,
                                    View.MeasureSpec.EXACTLY
                            )
                            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                                    clRowAd!!.height,
                                    View.MeasureSpec.UNSPECIFIED
                            )
                            clRowAd!!.measure(wMeasureSpec, hMeasureSpec)
                        }
                    }

                    if (binding.vpHomeAd.layoutParams.height != clRowAd!!.height * adNum[position]) {
                        // ParentViewGroup is, for example, LinearLayout
                        // ... or whatever the parent of the ViewPager2 is
                        binding.vpHomeAd.layoutParams =
                                (binding.vpHomeAd.layoutParams as LinearLayout.LayoutParams)
                                        .also { lp -> lp.height = clRowAd!!.height * adNum[position] }
                    }
                }
            })
        })

        /** ?????? ????????? ????????? ??? ??? ?????? ????????? ?????? */
        binding.btnHomePageUp.setOnClickListener {
            binding.usvHome.smoothScrollTo(0, 0)
        }

        viewModel.error.observe(binding.lifecycleOwner!!, {
            binding.srlHome.isRefreshing = false
            showToast(it.message)
        })

        viewModel.missionDeleted.observe(binding.lifecycleOwner!!, {
            // ?????? ????????? ?????? ????????? -> ?????? ???????????? ?????? ?????????.
            viewModel.loadData()
        })

        /** ?????? ?????? ?????? Refresh */
        binding.srlHome.setOnRefreshListener {
            viewModel.loadData()
            binding.srlHome.isRefreshing = false
        }
    }

    fun getTabView(position: Int) : View {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.tab_home, null, false)
        val tab1Indicator = view.findViewById<ImageView>(R.id.indicatorTabHome)
        val tab1Tv = view.findViewById<TextView>(R.id.tvTabHome)

        when (position) {
            0 -> {
                tab1Tv.text = "?????????"
                tabState = "ongoing"
                tab1Indicator.visibility = View.VISIBLE
            }
            1 -> {
                tab1Tv.text = "????????????"
                tabState = "scheduled"
                tab1Indicator.visibility = View.VISIBLE
            }
            2 -> {
                tab1Tv.text = "??????"
                tabState = "done"
                tab1Indicator.visibility = View.VISIBLE
            }
        }

        return view
    }

    private fun startRegisterReceiver() {
        if (!mIsReceiverRegistered) {
            if (receiver == null) {
                receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        Log.d("CashcarMsg", "Message Received")
                        viewModel.loadData()
                    }
                }
            }
            requireActivity().registerReceiver(receiver, IntentFilter("com.package.notification"))
            Log.d("CashcarMsg", "startRegisterReceiver()")
            mIsReceiverRegistered = true
        }
    }

    private fun finishRegisterReceiver() {
        if (mIsReceiverRegistered) {
            requireActivity().unregisterReceiver(receiver)
            receiver = null
            mIsReceiverRegistered = false

            Log.d("CashcarMsg", "finishRegisterReceiver()")
        }
    }

    private fun pauseRegisterReceiver() {
        if (mIsReceiverRegistered) {
            mIsReceiverRegistered = false
        }

        Log.d("CashcarMsg", "pauseRegisterReceiver()")
    }

    /** Home Tab?????? ????????? ????????? ???????????? ?????? ?????????. */
    override fun onResume() {
        super.onResume()
        viewModel.loadData()
        startRegisterReceiver()
    }

    override fun onPause() {
        super.onPause()
        pauseRegisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishRegisterReceiver()
    }

    override fun onStart() {
        super.onStart()
        startRegisterReceiver()
    }

    inner class MyAdapter(private val context: Context, private val dataList: ArrayList<ArrayList<AdResponse>>) : RecyclerView.Adapter<MyAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.Holder {
            val view = LayoutInflater.from(context).inflate(R.layout.row_tmp, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            when(position) {
                0 -> {
                    val adapter = AdRecyclerAdapter(context, dataList[position], "ongoing")
                    holder.rvTmp.adapter = adapter
                    holder.rvTmp.layoutManager = NoScrollLayoutManager(context)
                }
                1 -> {
                    val adapter = AdRecyclerAdapter(context, dataList[position], "scheduled")
                    holder.rvTmp.adapter = adapter
                    holder.rvTmp.layoutManager = NoScrollLayoutManager(context)
                }
                2 -> {
                    val adapter = AdRecyclerAdapter(context, dataList[position], "done")
                    holder.rvTmp.adapter = adapter
                    holder.rvTmp.layoutManager = NoScrollLayoutManager(context)
                }
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rvTmp: RecyclerView = itemView.findViewById(R.id.rvRowTmp)
        }
    }
}

/** ???????????? ?????? ?????? Dialog
(PopupDialog??? Fragment?????? ??????????????? ???????????? ?????? ????????? ????????? ??????.)*/
class CancelMissionPopupDialog(
        private val msg: String,
        private val okMsg: String,
        private val cancelMsg: String,
        private val positiveFun: (() -> Unit)
) : DialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        tvPopup.text = msg
        btnPopupOk.text = okMsg
        btnPopupCancel.text = cancelMsg

        btnPopupOk.setOnClickListener {
            positiveFun()
            dismiss()
        }

        btnPopupCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        // ??? DialogFragment ??????????????? ???????????? ????????? ??????.clRowAd!!.height
        val windowManager = App().context().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()

        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getSize(size)

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealSize(size)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getSize(size)
        }*/

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}