package com.cashfulus.cashcarplus.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/** 스크롤 작동만 막는 LayoutManager. */
// HomeTab 구현을 위해 필요함.
class NoScrollLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        return false
    }
}