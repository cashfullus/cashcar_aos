package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.FaqResponse
import com.cashfulus.cashcarplus.model.NoticeData

class ExpandableFaqListAdapter(private val context: Context, private val dataList: ArrayList<FaqResponse>): BaseExpandableListAdapter() {
    override fun getGroupCount() = dataList.size

    override fun getChildrenCount(parent: Int) = 1

    override fun getGroup(parent: Int) = dataList

    override fun getChild(parent: Int, child: Int): String = dataList[parent].description

    override fun getGroupId(parent: Int) = parent.toLong()

    override fun getChildId(parent: Int, child: Int) = child.toLong()

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    /* 부모 계층 레이아웃 설정 */
    override fun getGroupView(parent: Int, isExpanded: Boolean, convertView: View?, parentview: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentView = inflater.inflate(R.layout.row_notice_parent, parentview, false)
        var tvRowNoticeParentTitle =  parentView.findViewById<TextView>(R.id.tvRowNoticeParentTitle)
        tvRowNoticeParentTitle.text = dataList[parent].title
        setArrow(parentView, isExpanded)

        return parentView
    }

    /* 자식 계층 레이아웃 설정 */
    override fun getChildView(parent: Int, child: Int, isLastChild: Boolean, convertView: View?, parentview: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val childView = inflater.inflate(R.layout.row_notice_child, parentview, false)
        var tvRowNoticeChildContents =  childView.findViewById<TextView>(R.id.tvRowNoticeChildContents)
        tvRowNoticeChildContents.text = dataList[parent].title

        return childView
    }

    /* 닫힘, 열림 표시해주는 화살표 설정 */
    private fun setArrow(parentView: View, isExpanded: Boolean) {
        /* 0번째 부모는 자식이 없으므로 화살표 설정해주지 않음 */
        var ivRowNotice =  parentView.findViewById<ImageView>(R.id.ivRowNotice)
        if (isExpanded)
            ivRowNotice.setImageResource(R.drawable.ic_arrow_up)
        else
            ivRowNotice.setImageResource(R.drawable.ic_arrow_below)
    }
}