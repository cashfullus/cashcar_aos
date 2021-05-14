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

        /** 권한 확인 */
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@MainActivity, "권한 거부\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("필수 권한 거부 시 앱 이용이 어려울 수 있습니다.\n\n[설정] > [권한]에서 필수 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                .check()
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