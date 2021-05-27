package com.cashfulus.cashcarplus.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.content.res.use
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.cashfulus.cashcarplus.R

@SuppressLint("Recycle")
class LoadingDialogView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : View(context, attrs) {
    private lateinit var dialog: Dialog

    private lateinit var binding: ViewDataBinding

    private var dialogVisibility: Int = GONE
        set(value) {
            field = value
            if (value == VISIBLE) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

    init {
        createDialog(context)
        /*context.obtainStyledAttributes(attrs, R.styleable.DialogShowingView, defStyleAttr, 0).use {
            createDialog(context)
        }*/
    }

    private fun createDialog(context: Context) {
        val frameLayout = FrameLayout(context)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_loading,
            frameLayout,
            true
        )

        dialog = Dialog(context).apply { //@style/Theme.AppCompat.Dialog
            setContentView(frameLayout)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0, 0)
    }

    override fun setVisibility(visibility: Int) {
        dialogVisibility = visibility
    }

    override fun getVisibility() = dialogVisibility

    /**
     * Sometimes while showing the dialog we need to replace its holder fragment or activity. In this case we
     * need to dismiss dialog.
     */
    override fun onDetachedFromWindow() {
        dialog.dismiss()
        super.onDetachedFromWindow()
    }
}