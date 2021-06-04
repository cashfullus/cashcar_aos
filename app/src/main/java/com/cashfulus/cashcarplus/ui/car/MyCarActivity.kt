package com.cashfulus.cashcarplus.ui.car

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityMyCarBinding
import com.cashfulus.cashcarplus.ui.adapter.MyCarRecyclerAdapter
import com.cashfulus.cashcarplus.ui.dialog.PopupDialog
import com.cashfulus.cashcarplus.ui.dialog.PopupDialogClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyCarActivity : BaseActivity(), PopupDialogClickListener {
    private val binding by binding<ActivityMyCarBinding>(R.layout.activity_my_car)
    private val viewModel: MyCarViewModel by viewModel { parametersOf() }

    val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            viewModel.refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MyCarActivity
            viewModel = this@MyCarActivity.viewModel
        }

        /** Popup 설정 */
        val cannotDeleteDialog = PopupDialog("차량은 최대 3대까지만 등록 가능합니다.", null, "확인")

        /** 툴바 버튼 설정 */
        binding.toolbarMyCar.setLeftOnClick {
            finish()
        }

        /** RecyclerView 셋팅 */
        val adapter = MyCarRecyclerAdapter(this@MyCarActivity, requestActivity)
        binding.rvMyCar.adapter = adapter
        binding.rvMyCar.layoutManager = LinearLayoutManager(this@MyCarActivity)
        /** LiveData 셋팅 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            adapter.setData(it)

            // 새로 추가하는 차량은 광고 서포터즈 차량이 아니어도 됨.
            if (it.size >= 3) {
                binding.toolbarMyCar.setRightOnClick {
                    cannotDeleteDialog.show(supportFragmentManager, "cannotDelete")
                }
            } else {
                binding.toolbarMyCar.setRightOnClick {
                    val intent = Intent(this@MyCarActivity, AddCarActivity::class.java)
                    intent.putExtra("isNewCar", false)
                    requestActivity.launch(intent)
                }
            }
        })

        viewModel.empty.observe(binding.lifecycleOwner!!, {
            adapter.setData(null)
            showToast("등록된 차량이 없습니다.")

            // 새로 추가하는 차량은 광고 서포터즈 차량이어야 함.
            binding.toolbarMyCar.setRightOnClick {
                val intent = Intent(this@MyCarActivity, AddCarActivity::class.java)
                intent.putExtra("isNewCar", true)
                requestActivity.launch(intent)
            }
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
            finish()
        })
    }

    /** cannotDeleteDialog의 interface의 콜백 함수 부분 */
    override fun onPositive() {}
    override fun onNegative() {}
}