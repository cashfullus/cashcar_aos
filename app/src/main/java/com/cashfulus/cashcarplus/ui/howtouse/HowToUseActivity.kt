package com.cashfulus.cashcarplus.ui.howtouse

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_how_to_use.*

class HowToUseActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        vpHowToUse.adapter = pagerAdapter

        TabLayoutMediator(indicatorHowToUse, vpHowToUse) { tab, position -> }.attach()

        vpHowToUse.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> {
                        btnHowToUseSkip.visibility = View.VISIBLE
                        btnHowToUseNext.visibility = View.VISIBLE
                        btnHowToUseStart.visibility = View.GONE
                    }
                    1 -> {
                        btnHowToUseSkip.visibility = View.GONE
                        btnHowToUseNext.visibility = View.VISIBLE
                        btnHowToUseStart.visibility = View.GONE
                    }
                    2 -> {
                        btnHowToUseSkip.visibility = View.GONE
                        btnHowToUseNext.visibility = View.GONE
                        btnHowToUseStart.visibility = View.VISIBLE
                    }
                }
            }
        })

        btnHowToUseSkip.setOnClickListener {
            val initInfo = getSharedPreferences("init", MODE_PRIVATE)
            initInfo.edit().putBoolean("isFirst", false).apply()

            val intent = Intent(this@HowToUseActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        btnHowToUseNext.setOnClickListener {
            vpHowToUse.currentItem += 1
        }
        btnHowToUseStart.setOnClickListener {
            val initInfo = getSharedPreferences("init", MODE_PRIVATE)
            initInfo.edit().putBoolean("isFirst", false).apply()

            val intent = Intent(this@HowToUseActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}