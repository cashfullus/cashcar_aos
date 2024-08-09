package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cashfulus.cashcarplus.R

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

        var btnAppbarBack = findViewById<FrameLayout>(R.id.btnAppbarBack)
        var btnIvAppbarBack = findViewById<ImageView>(R.id.btnIvAppbarBack)
        var tvAppbarTitle = findViewById<TextView>(R.id.tvAppbarTitle)
        var btnAppbarFunction = findViewById<FrameLayout>(R.id.btnAppbarFunction)
        var btnIvAppbarFunction = findViewById<ImageView>(R.id.btnIvAppbarFunction)

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

    fun getRightButtonRes(): FrameLayout {
        var btnAppbarFunction = findViewById<FrameLayout>(R.id.btnAppbarFunction)
        return btnAppbarFunction
    }

    fun setLeftButton(@DrawableRes img: Int) {
        var btnAppbarBack = findViewById<FrameLayout>(R.id.btnAppbarBack)
        var btnIvAppbarBack = findViewById<ImageView>(R.id.btnIvAppbarBack)
        if(img == 0) {
            btnIvAppbarBack.background = null
            btnAppbarBack.visibility = View.GONE
        } else {
            btnIvAppbarBack.background = ContextCompat.getDrawable(context, img)
            btnAppbarBack.visibility = View.VISIBLE
        }
    }

    fun setTitle(title: String) {
        var tvAppbarTitle = findViewById<TextView>(R.id.tvAppbarTitle)
        tvAppbarTitle.text = title
    }

    fun setRightButton(@DrawableRes img: Int?) {
        var btnAppbarFunction = findViewById<FrameLayout>(R.id.btnAppbarFunction)
        var btnIvAppbarFunction = findViewById<ImageView>(R.id.btnIvAppbarFunction)
        if(img != null) {
            btnIvAppbarFunction.background = ContextCompat.getDrawable(context, img)
            btnAppbarFunction.visibility = VISIBLE
        } else {
            btnIvAppbarFunction.background = null
            btnAppbarFunction.visibility = GONE
        }
    }

    fun setLeftOnClick(function: (View) -> Unit) {
        var btnAppbarBack = findViewById<FrameLayout>(R.id.btnAppbarBack)
        btnAppbarBack.setOnClickListener(function)
    }

    fun setRightOnClick(function: (View) -> Unit) {
        var btnAppbarFunction = findViewById<FrameLayout>(R.id.btnAppbarFunction)
        btnAppbarFunction.setOnClickListener(function)
    }
}