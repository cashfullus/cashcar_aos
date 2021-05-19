package com.cashfulus.cashcarplus.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.WindowManager

class HeightLimitedSpinner : androidx.appcompat.widget.AppCompatSpinner {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getWindowVisibleDisplayFrame(outRect: Rect) {
        Log.e("Cashcarplus", "getWindowVisibleDisplayFrame called")
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = wm.getDefaultDisplay()
        d.getRectSize(outRect)
        outRect.set(outRect.left, 100, outRect.right, outRect.bottom)
    }
}