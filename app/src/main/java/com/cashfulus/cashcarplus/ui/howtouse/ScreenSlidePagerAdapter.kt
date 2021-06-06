package com.cashfulus.cashcarplus.ui.howtouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cashfulus.cashcarplus.R

class ScreenSlidePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = HowToUseFragment()

        when(position){
            0 ->
                fragment.arguments = Bundle().apply {
                    putString(TITLE_HOW_TO_USE, "STEP 01")
                    putString(TEXT_HOW_TO_USE, "차량 정보를 등록한 후 원하는\n"+"광고의 서포터즈를 신청해주세요")
                    putInt(IMAGE_HOW_TO_USE, R.drawable.howtouse_01)
                }
            1 ->
                fragment.arguments = Bundle().apply {
                    putString(TITLE_HOW_TO_USE, "STEP 02")
                    putString(TEXT_HOW_TO_USE, "배송된 스티커를 차량에 부착한 후\n"+"미션을 인증하고 안전운행 해주세요")
                    putInt(IMAGE_HOW_TO_USE, R.drawable.howtouse_02)
                }
            2 ->
                fragment.arguments = Bundle().apply {
                    putString(TITLE_HOW_TO_USE, "STEP 03")
                    putString(TEXT_HOW_TO_USE, "서포터즈 활동을 완료하면 포인트를\n"+"적립하여 출금 또는 기부 할 수 있습니다 :)")
                    putInt(IMAGE_HOW_TO_USE, R.drawable.howtouse_03)
                }
        }
        return fragment
    }
}