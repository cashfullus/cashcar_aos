package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import java.text.DecimalFormat

class RecyclerAdapter(private val context: Context, private val dataList: ArrayList<RecyclerData>) : RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    val numFormat = DecimalFormat("###,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recycler, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvStr.setText(dataList[position].strData)
        holder.tvInt.setText(numFormat.format(dataList[position].intData))

        if(dataList[position].boolData) {
            holder.tvStr.setTextColor(ContextCompat.getColor(context, R.color.brand_green))
        } else {
            holder.tvStr.setTextColor(ContextCompat.getColor(context, R.color.brand_green3))
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStr: TextView = itemView.findViewById(R.id.tvRowStr)
        val tvInt: TextView = itemView.findViewById(R.id.tvRowInt)
    }
}

data class RecyclerData(val strData: String, val intData: Int, val boolData: Boolean)