package com.cashfulus.cashcarplus.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import kotlinx.android.synthetic.main.dialog_mission.*
import kotlinx.android.synthetic.main.dialog_withdraw.*
import java.lang.ClassCastException

class WithdrawingDialog(private val isDonate: Boolean, private val isOngoing: Boolean) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_withdraw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDialogWithdraw.setOnClickListener {
            dismiss()
        }

        // 기부
        if(isDonate) {
            tvDialogWithdrawTitle.text = "현재 기부 진행 과정 중에 있으며\n" + "한 번에 한 건의 기부만 가능합니다."
            tvDialogWithdrawImg1.text = "기부신청"
            tvDialogWithdrawImg2.text = "기부중"
            tvDialogWithdrawImg3.text = "기부완료"
        }
        // 출금
        else {
            tvDialogWithdrawTitle.text = "현재 출금 진행 과정 중에 있으며\n" + "한 번에 한 건의 출금만 가능합니다."
            tvDialogWithdrawImg1.text = "출금신청"
            tvDialogWithdrawImg2.text = "출금중"
            tvDialogWithdrawImg3.text = "출금완료"
        }

        // 진행중
        if(isOngoing) {
            ivDialogWithdrawImg1.setImageResource(R.drawable.ic_withdraw_apply_inactive)
            ivDialogWithdrawImg2.setImageResource(R.drawable.ic_withdraw_ing_active)

            tvDialogWithdrawImg1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.grayscale_600))
            tvDialogWithdrawImg2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.brand_orange1))
        }
        //신청
        else {
            ivDialogWithdrawImg1.setImageResource(R.drawable.ic_withdraw_apply_active)
            ivDialogWithdrawImg2.setImageResource(R.drawable.ic_withdraw_ing_inactive)

            tvDialogWithdrawImg1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.brand_orange1))
            tvDialogWithdrawImg2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.grayscale_600))
        }
    }

    override fun onResume() {
        super.onResume()

        // 꼭 DialogFragment 클래스에서 선언하지 않아도 된다.
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