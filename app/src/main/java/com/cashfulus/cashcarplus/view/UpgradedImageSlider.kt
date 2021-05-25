package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.ImageUrl
import com.cashfulus.cashcarplus.ui.adapter.ImageSliderAdapter
import kotlinx.android.synthetic.main.widget_upgraded_image_slider.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class UpgradedImageSlider : ConstraintLayout {
    private val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.

    var pageNum by Delegates.notNull<Int>()
    var currentPage = 0

    constructor(context: Context): super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initLayout()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(
        context,
        attrs,
        defStyle
    ) {
        initLayout()
        getAttrs(attrs, defStyle)
    }

    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.widget_upgraded_image_slider, this, true)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UpgradedImageSlider) as TypedArray
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.UpgradedImageSlider,
            defStyle,
            0
        ) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        /** 2021.05.20(목)
         *  원래는 Auto Scroll 관련 항목을 attribute로 넣을려고 했으나,
         *  images 배열이 셋팅되는 건 attribute 처리 이후이기 때문에
         *  startAutoScroll 함수가 setImages보다 먼저 호출되서, 전체 이미지 갯수를 정상적으로 구하지 못할 것이라는 판단이 들었다.
         */
    }

    /** image List 셋팅 */
    fun setImages(images: List<ImageUrl>) {
        val adapter = ImageSliderAdapter(context, images)
        pageNum = images.size
        vpImageSlider.adapter = adapter
        tlImageSlider.setupWithViewPager(vpImageSlider, true)
    }

    /** Auto Scroll */
    fun startAutoScroll(milliseconds: Long) {
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == pageNum) {
                currentPage = 0
            }
            vpImageSlider.setCurrentItem(currentPage++, true)
        }

        // This will create a new Thread
        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, milliseconds)
    }
}