package com.cashfulus.cashcarplus.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.cashfulus.cashcarplus.R

class LoadingDialog constructor(context: Context) : Dialog(context){

    init {
        setCanceledOnTouchOutside(false)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_loading)
    }

    override fun onBackPressed() {}
}