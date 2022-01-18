package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.PointHistoryRow
import java.text.DecimalFormat

class PointHistoryAdapter(private val context: Context, private val dataList: ArrayList<PointHistoryRow>) : RecyclerView.Adapter<PointHistoryAdapter.Holder>() {
    val numFormat = DecimalFormat("+###,###;-###,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointHistoryAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_point_history, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvTitle.text = dataList[position].contents
        holder.tvRegister.text = dataList[position].registerTime
        holder.tvPoint.text = numFormat.format(dataList[position].point)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvPointHistoryTitle)
        val tvRegister: TextView = itemView.findViewById(R.id.tvPointHistoryRegister)
        val tvPoint: TextView = itemView.findViewById(R.id.tvPointHistoryPoint)
    }
}