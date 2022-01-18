package com.cashfulus.cashcarplus.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.BaseFragment
import com.cashfulus.cashcarplus.data.service.LOST_USER_INFO_ERROR_CODE
import com.cashfulus.cashcarplus.databinding.FragmentMyBinding
import com.cashfulus.cashcarplus.ui.car.MyCarActivity
import com.cashfulus.cashcarplus.ui.clause.ClauseListActivity
import com.cashfulus.cashcarplus.ui.faq.FaqActivity
import com.cashfulus.cashcarplus.ui.inquiry.InquiryActivity
import com.cashfulus.cashcarplus.ui.login.LoginActivity
import com.cashfulus.cashcarplus.ui.myactivities.MyActivitiesActivity
import com.cashfulus.cashcarplus.ui.notice.NoticeListActivity
import com.cashfulus.cashcarplus.ui.point.PointActivity
import com.cashfulus.cashcarplus.ui.user.UserAddressActivity
import com.cashfulus.cashcarplus.ui.user.UserInfoActivity
import com.cashfulus.cashcarplus.util.UserManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class MyFragment : BaseFragment<FragmentMyBinding, MyViewModel>(R.layout.fragment_my) {
    override val viewModel: MyViewModel by viewModel { parametersOf() }

    val numFormat = DecimalFormat("###,###")

    override fun init() {
        /** RecyclerView 셋팅 */
        binding.rvMyMenu.adapter = MenuAdapter()
        binding.rvMyMenu.layoutManager = LinearLayoutManager(requireActivity())

        /** View의 onClickEvent 처리 */
        binding.clMyUserInfo.setOnClickListener {
            val intent = Intent(requireActivity(), UserInfoActivity::class.java)
            startActivity(intent)
        }

        /** 포인트, 활동 내역 onClickEvent 처리 */
        binding.clMyPoint.setOnClickListener {
            startActivity(Intent(requireActivity(), PointActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        /** 포인트 관련 정보를 불러옴 */
        viewModel.loadPointInfo()

        /** 유저의 상세정보 기입 여부 확인 */
        Log.d("Cashcarplus", "name : "+UserManager.name+"")
        if(UserManager.name == null) {
            // 상세 정보가 없음 : 상세 정보 입력을 유도하고, blocking 상태 유지.
            binding.tvMyName.text = "클릭!"
            binding.tvMyEmail.text = "상세 정보를 입력해주세요."
        } else {
            // 상세 정보가 있음 : 유저 정보를 표시하고, blocking 상태 해제.
            if(UserManager.nickName.isNullOrBlank()) //유저 사진 옆에 [이름] 정보가 아니라 [별명] 정보 띄어주기
                binding.tvMyName.text = UserManager.name
            else
                binding.tvMyName.text = UserManager.nickName
            binding.tvMyEmail.text = UserManager.email
            if(!UserManager.profileImage.isNullOrBlank())
                Glide.with(requireActivity()).load(UserManager.profileImage!!).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.ivMyProfile)
        }

        /** 포인트 관련 정보 LiveData */
        viewModel.pointData.observe(binding.lifecycleOwner!!, {
            binding.tvMyPoint.text = numFormat.format(it.userPoint)
        })
        viewModel.error.observe(binding.lifecycleOwner!!, {
            showToast(it.message)

            if(it.status == LOST_USER_INFO_ERROR_CODE) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        })
    }

    inner class MenuAdapter : RecyclerView.Adapter<MenuAdapter.Holder>() {
        private val menuList = ArrayList<String>()
        private val intentList = ArrayList<Intent?>()

        init {
            menuList.add("활동 내역")
            menuList.add("내 차량")
            menuList.add("배송지 설정")
            menuList.add("공지사항")
            menuList.add("FAQ / 1:1 문의")
            menuList.add("광고 문의")
            menuList.add("약관 및 정책")

            intentList.add(Intent(requireActivity(), MyActivitiesActivity::class.java))
            intentList.add(Intent(requireActivity(), MyCarActivity::class.java))
            intentList.add(Intent(requireActivity(), UserAddressActivity::class.java))
            intentList.add(Intent(requireActivity(), NoticeListActivity::class.java))
            intentList.add(Intent(requireActivity(), FaqActivity::class.java))
            intentList.add(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.cashcar_kakao_url))))
            intentList.add(Intent(requireActivity(), ClauseListActivity::class.java))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.Holder {
            val view = LayoutInflater.from(context).inflate(R.layout.row_my_fragment, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.tvTitle.text = menuList[position]
            holder.row.setOnClickListener {
                if(intentList[position] == null)
                    showToast("준비 중인 기능입니다.")
                else
                    startActivity(intentList[position])
            }
        }

        override fun getItemCount(): Int {
            return menuList.size
        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val row: LinearLayout = itemView.findViewById(R.id.rowMy)
            val tvTitle: TextView = itemView.findViewById(R.id.tvRowMy)
        }
    }
}