package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.AlarmResponse

class AlarmRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<AlarmRecyclerAdapter.Holder>() {
    var alarmList: ArrayList<AlarmResponse> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_alarm, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(alarmList[position].required == 1)
            holder.ivIcon.setImageResource(R.drawable.dot_alarm_important)
        else
            holder.ivIcon.setImageResource(R.drawable.dot_alarm_marketing)

        holder.tvMsg.text = alarmList[position].description

        holder.row.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    fun changeList(paramList: ArrayList<AlarmResponse>) {
        alarmList = paramList
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: ConstraintLayout = itemView.findViewById(R.id.rowAlarm)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivRowAlarm)
        val tvMsg: TextView = itemView.findViewById(R.id.tvRowAlarm)
    }
}