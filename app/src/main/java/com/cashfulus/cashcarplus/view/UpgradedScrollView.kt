package com.cashfulus.cashcarplus.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView

class UpgradedScrollView : ScrollView, ViewTreeObserver.OnGlobalLayoutListener {
    // fullScroll 함수로 스크롤된 것인지 아닌지를 판별하는 flag
    var isFullScroll = false

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        overScrollMode = OVER_SCROLL_NEVER
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    var header: View? = null
        set(value) {
            field = value
            field?.let {
                it.translationZ = 1f
            }
        }

    var stickListener: (View) -> Unit = {}
    var freeListener: (View) -> Unit = {}

    // 헤더가 천장에 달라 붙어 있는지 아닌지를 체크하는 flag
    private var mIsHeaderSticky = false

    // 헤더의 초기 위치를 저장할 변수
    // 이 변수는 스크롤되는 위치와 비교되어서 헤더가 천장을 넘어서는 스크롤인지 아닌지 판단하는데 이용된다.
    private var mHeaderInitPosition = 0f

    // 이 함수는 헤더의 y포지션을 저장하는 부분이다.
    // 만약 이 로직을 생성자나 onAttachToWindow등에 넣어두면 올바르게 저장되지 않는다.
    override fun onGlobalLayout() {
        mHeaderInitPosition = header?.top?.toFloat() ?: 0f
        Log.d("Cashcarplus", "onGlobalLayout : "+mHeaderInitPosition.toString())
    }

    // onScrollChanged를 오버라이드해서 어느시점에 헤더를 천장에 붙일지, 다시 땔지 등을 구현한다.
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t

        // 먼저 scrolly가 헤더의 초기 y포지션보다 클 경우, 헤더를 천장에 붙인다고 표현되어 있다.
        // scrolly = '스크롤 뷰가 스크롤 된 정도'
        if (scrolly > mHeaderInitPosition) {
            stickHeader(scrolly - mHeaderInitPosition)
        } else {
            freeHeader()
        }
    }

    // 헤더를 천장에 붙이는 부분이다.
    private fun stickHeader(position: Float) {
        if(!isFullScroll) {
            header?.translationY = position
            callStickListener()
        } else
            isFullScroll = false
    }

    private fun callStickListener() {
        if (!mIsHeaderSticky) {
            stickListener(header ?: return)
            mIsHeaderSticky = true
        }
    }

    private fun freeHeader() {
        header?.translationY = 0f
        callFreeListener()
    }

    private fun callFreeListener() {
        if (mIsHeaderSticky) {
            freeListener(header ?: return)
            mIsHeaderSticky = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun fullScroll(direction: Int): Boolean {
        isFullScroll = true
        return super.fullScroll(direction)
    }
}