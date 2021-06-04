package com.cashfulus.cashcarplus.ui

import android.Manifest
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    private val adapter by lazy { MainAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vpMain.adapter = MainActivity@adapter
        vpMain.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vpMain.isUserInputEnabled = false

        /** 로그인이 되어 있지 않은 상태에서 MainActivity로 넘어온 경우.
         * 이런 오류가 발생할 수 있는 경우는 1가지이다 : Splash Page에서 알람이 와서, 알람을 클릭해서 로그인 과정 도중 Main으로 넘어온 경우. */
        if(!UserManager.isLogined) {
            Toast.makeText(this@MainActivity, "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
            finish()
        }

        TabLayoutMediator(tabMain, vpMain) { tab, position ->
            when(position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.selector_tab_home)
                    tab.text = "홈"
                }
                1 -> {
                    tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.selector_tab_cashtip)
                    tab.text = "캐시카팁"
                }
                2 -> {
                    tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.selector_tab_driving)
                    tab.text = "드라이빙"
                }
                3 -> {
                    tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.selector_tab_my)
                    tab.text = "마이캐시카"
                }
            }
        }.attach()
    }
}

class MainAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0       -> HomeFragment()
            1       -> CashcartipFragment()
            2       -> DrivingFragment()
            3       -> MyFragment()
            else -> NoneFragment()
        }
    }
}