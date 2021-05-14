package com.cashfulus.cashcarplus.ui.adinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityAdInfoBinding
import com.cashfulus.cashcarplus.ui.adapter.AdInfoSliderAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import com.cashfulus.cashcarplus.util.UserManager
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.template.model.*
import com.smarteist.autoimageslider.SliderView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class AdInfoActivity : BaseActivity() {
    val numFormat = DecimalFormat("###,###")

    // Loading Dialog 및 MVVM 관련 객체들
    val loadingDialog: LoadingDialog by inject { parametersOf(this@AdInfoActivity) }
    private val binding by binding<ActivityAdInfoBinding>(R.layout.activity_ad_info)
    private val viewModel: AdInfoViewModel by viewModel { parametersOf(this@AdInfoActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AdInfoActivity
            viewModel = this@AdInfoActivity.viewModel
        }

        /** 정보 받아오기 */
        if(intent.getIntExtra("id", -1) != -1)
            viewModel.loadData(intent.getIntExtra("id", -1))
        else {
            showToast("오류가 발생했습니다. 다시 시도해 주세요.")
            finish()
        }

        /** 진행중인 미션이 있다면 버튼 비활성화 */
        if(!UserManager.hasMission && intent.getBooleanExtra("canRegister", false)) {
            binding.btnAdInfo.background = ContextCompat.getDrawable(this@AdInfoActivity, R.drawable.button_form_active)
            binding.btnAdInfo.setTextColor(getColor(R.color.grayscale_wt))
            binding.btnAdInfo.isEnabled = true
            binding.btnAdInfo.isFocusable = true
            binding.btnAdInfo.isClickable = true
        } else {
            binding.btnAdInfo.background = ContextCompat.getDrawable(this@AdInfoActivity, R.drawable.button_form_deactive)
            binding.btnAdInfo.setTextColor(getColor(R.color.grayscale_400))
            binding.btnAdInfo.isEnabled = false
            binding.btnAdInfo.isFocusable = false
            binding.btnAdInfo.isClickable = false
        }

        /** LiveData 관련 설정 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            val data = it
            val adId = it.ad_id; val image = it.thumbnail_image; val title = it.title; val point = it.total_point

            // 최상단 슬라이드 이미지 설정
            binding.svAdInfo.setSliderAdapter(AdInfoSliderAdapter(it.images))
            Glide.with(this@AdInfoActivity).load(it.thumbnail_image).into(binding.ivAdInfoTitle)
            binding.tvAdInfoAdTitle.text = it.title
            binding.tvAdInfoPoint.text = numFormat.format(it.total_point)
            binding.tvAdInfoCurrentPeople.text = numFormat.format(it.recruiting_count)+"명 신청"

            binding.tvAdInfoDate.text = it.recruit_start_date.substring(0,10).replace('-', '.') + " - " + it.recruit_end_date.substring(0,10).replace('-', '.')
            binding.tvAdInfoDuration.text = it.activity_period.toString()+"일 (1차 미션 시작일부터)"
            binding.tvAdInfoPeopleNum.text = it.max_recruiting_count.toString()+"명"
            binding.tvAdInfoRegion.text = it.area

            binding.tvAdInfoContents.text = it.description

            Glide.with(this@AdInfoActivity).load(it.side_image).into(binding.ivAdInfoDesign1)
            binding.tvAdInfoSize1.text = it.side_width.toString() + " x " + it.side_length.toString() + " cm"
            Glide.with(this@AdInfoActivity).load(it.back_image).into(binding.ivAdInfoDesign2)
            binding.tvAdInfoSize2.text = it.back_width.toString() + " x " + it.back_length.toString() + " cm"

            /** 신청하기 버튼 */
            binding.btnAdInfo.setOnClickListener {
                val intent = Intent(this@AdInfoActivity, AdRegisterActivity::class.java)
                intent.putExtra("adId", adId)
                intent.putExtra("image", image)
                intent.putExtra("title", title)
                intent.putExtra("point", point)
                startActivity(intent)
            }

            /** 최상단 우측 공유 버튼 */
            binding.toolbarAdInfo.setRightOnClick {
                /*val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val shareMsg = "[Cashcar]서포터즈 : "+viewModel.response.value!!.title
                intent.putExtra(Intent.EXTRA_TEXT, shareMsg)

                val chooser = Intent.createChooser(intent, "친구에게 공유하기");
                startActivity(chooser)*/

                val defaultFeed = FeedTemplate(
                    content = Content(
                        title = "캐시카 플러스",
                        description = data.title,
                        imageUrl = data.thumbnail_image,
                        link = Link(
                            webUrl = getString(R.string.cashcar_kakao_channel),
                            mobileWebUrl = getString(R.string.cashcar_kakao_channel)
                        )
                    ),

                    social = Social(
                        likeCount = data.recruiting_count
                    ),
                    buttons = listOf(
                        Button(
                            "앱으로 보기",
                            Link(
                                androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                                iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                            )
                        )
                    )
                )

                // 피드 메시지 보내기
                LinkClient.instance.defaultTemplate(this@AdInfoActivity, defaultFeed) { linkResult, error ->
                    if (error != null) {
                        Log.e("Cashcarplus", "카카오링크 보내기 실패", error)
                    }
                    else if (linkResult != null) {
                        Log.d("Cashcarplus", "카카오링크 보내기 성공 ${linkResult.intent}")
                        startActivity(linkResult.intent)

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w("Cashcarplus", "Warning Msg: ${linkResult.warningMsg}")
                        Log.w("Cashcarplus", "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        /** 툴바 설정 */
        binding.toolbarAdInfo.setLeftOnClick {
            finish()
        }
    }
}