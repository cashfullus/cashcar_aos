package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.ContextWrapper
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.widget_upgraded_toolbar.view.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class UpgradedToolbar: ConstraintLayout {
    constructor(context: Context) : super(context) {
        initLayout()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout()
        if (attrs != null) {
            getAttrs(attrs)
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout()
        if (attrs != null) {
            getAttrs(attrs, defStyleAttr)
        }
    }


    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.widget_upgraded_toolbar, this, true)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UTB) as TypedArray
        setTypeArray(typedArray)
    }


    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UTB, defStyle, 0) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        // 왼쪽 버튼 이미지
        val leftDrawable = typedArray.getDrawable(R.styleable.UTB_leftButtonImg)
        if(leftDrawable == null)
            btnAppbarBack.visibility = View.GONE
        else {
            btnAppbarBack.visibility = View.VISIBLE
            btnIvAppbarBack.background = leftDrawable
        }

        // 제목
        val title = typedArray.getString(R.styleable.UTB_title)
        if(title == null)
            tvAppbarTitle.visibility = View.GONE
        else {
            tvAppbarTitle.visibility = View.VISIBLE
            tvAppbarTitle.text = title
        }

        // 오른쪽 버튼 이미지
        val rightDrawable = typedArray.getDrawable(R.styleable.UTB_rightButtonImg)
        if(rightDrawable == null)
            btnAppbarFunction.visibility = View.GONE
        else {
            btnAppbarFunction.visibility = View.VISIBLE
            btnIvAppbarFunction.background = rightDrawable
        }

        // 왼쪽 버튼 이벤트 리스너
        /*val leftButtonEvent = typedArray.getString(R.styleable.UTB_leftButtonOnClick)
        if(leftButtonEvent != null) {
            btnAppbarBack.setOnClickListener(DeclaredOnClickListener(btnAppbarBack, leftButtonEvent))
        }

        // 오른쪽 버튼 이벤트 리스너
        val rightButtonEvent = typedArray.getString(R.styleable.UTB_rightButtonOnClick)
        if(rightButtonEvent != null) {
            btnAppbarFunction.setOnClickListener(DeclaredOnClickListener(btnAppbarFunction, rightButtonEvent))
        }*/

        typedArray.recycle()
    }

    fun getRightButtonRes(): ImageButton {
        return btnIvAppbarFunction
    }

    fun setLeftButton(@DrawableRes img: Int) {
        if(img == 0) {
            btnIvAppbarBack.background = null
            btnAppbarBack.visibility = View.GONE
        } else {
            btnIvAppbarBack.background = ContextCompat.getDrawable(context, img)
            btnAppbarBack.visibility = View.VISIBLE
        }
    }

    fun setTitle(title: String) {
        tvAppbarTitle.text = title
    }

    fun setRightButton(@DrawableRes img: Int?) {
        if(img != null) {
            btnIvAppbarFunction.background = ContextCompat.getDrawable(context, img)
            btnAppbarFunction.visibility = VISIBLE
        } else {
            btnIvAppbarFunction.background = null
            btnAppbarFunction.visibility = GONE
        }
    }

    fun setLeftOnClick(function: (View) -> Unit) {
        btnAppbarBack.setOnClickListener(function)
    }

    fun setRightOnClick(function: (View) -> Unit) {
        btnAppbarFunction.setOnClickListener(function)
    }
}