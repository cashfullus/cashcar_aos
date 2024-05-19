package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import kotlinx.android.synthetic.main.dialog_code.*
import java.lang.ClassCastException

class CodeDialog : DialogFragment() {
    lateinit var listener: CodeDialogClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_code, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as CodeDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDialogCode.setOnClickListener {
            listener.onCodeApplyClick(dialog!!.etCode.getEditText().text.toString())
            dialog!!.dismiss()
        }
        btnCodeDialogClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog!!.setOnDismissListener {
            etCode.getEditText().setText("")
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

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}

interface CodeDialogClickListener {
    fun onCodeApplyClick(code: String)
}