package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import java.lang.ClassCastException

class SubmitDialog(var reason: String) : DialogFragment() {

    lateinit var listener: SubmitDialogClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_submit, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as SubmitDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if(reason == "reject") {
            dialog!!.setCancelable(false)
        }

        var tvDialogSubmitTitle = dialog!!.findViewById<TextView>(R.id.tvDialogSubmitTitle)
        var btnDialogSubmitX = dialog!!.findViewById<ImageView>(R.id.btnDialogSubmitX)
        var tvDialogSubmit = dialog!!.findViewById<TextView>(R.id.tvDialogSubmit)

        tvDialogSubmitTitle.text =
            if(reason == "reject") {
                "신청코드 10회 오류로 인해\n해당 광고를 신청할 수 없습니다"
            } else {
                "입력하신 신청코드가 일치하지 않습니다"
            }

        btnDialogSubmitX.setOnClickListener {
            dialog!!.dismiss()
        }

        tvDialogSubmit.setOnClickListener {
            dialog!!.dismiss()
        }

        dialog!!.setOnDismissListener {
            if(reason == "reject") {
                listener.onSubmitClick("reject")
            } else if (reason == "fail") {
                listener.onSubmitClick("fail")
            }
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

interface SubmitDialogClickListener {
    fun onSubmitClick(reason: String)
}