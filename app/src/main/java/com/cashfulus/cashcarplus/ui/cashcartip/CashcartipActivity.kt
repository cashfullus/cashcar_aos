package com.cashfulus.cashcarplus.ui.cashcartip

import android.os.Bundle
import android.text.Html
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityCashcartipBinding
import com.cashfulus.cashcarplus.ui.adapter.CashcartipPostRecyclerAdapter
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CashcartipActivity : BaseActivity() {
    val loadingDialog: LoadingDialog by inject { parametersOf(this@CashcartipActivity) }
    private val binding by binding<ActivityCashcartipBinding>(R.layout.activity_cashcartip)
    private val viewModel: CashcartipViewModel by viewModel { parametersOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@CashcartipActivity
            viewModel = this@CashcartipActivity.viewModel
        }

        /** 데이터 로딩 */
        if(intent.getIntExtra("postId", -1) != -1)
            viewModel.loadCashcarTipPost(intent.getIntExtra("postId", -1))
        else {
            showToast("글을 불러올 수 없습니다. 다시 시도해 주세요.")
            finish()
        }

        /** 툴바 버튼 이벤트 */
        binding.toolbarCashcartip.setLeftOnClick {
            finish()
        }

        /** LiveData 처리 */
        viewModel.response.observe(binding.lifecycleOwner!!, {
            Glide.with(this@CashcartipActivity).load(it.thumbnailImage).into(binding.ivCashcartip)
            binding.tvCashcartipTitle.text = it.title
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                binding.tvCashcartipContents.text = Html.fromHtml(it.description)
            } else {
                binding.tvCashcartipContents.text = Html.fromHtml(it.description, Html.FROM_HTML_MODE_LEGACY)
            }

            val adapter = CashcartipPostRecyclerAdapter(this@CashcartipActivity, it.images)
            binding.rvCashcartipContents.adapter = adapter
            binding.rvCashcartipContents.layoutManager = LinearLayoutManager(this@CashcartipActivity)
        })

        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)
        })

        viewModel.loading.observe(binding.lifecycleOwner!!, {

        })
    }
}