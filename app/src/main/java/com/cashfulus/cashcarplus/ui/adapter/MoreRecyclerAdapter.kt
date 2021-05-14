package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import java.text.DecimalFormat

class MoreRecyclerAdapter(private val context: Context, private val dataList: ArrayList<MoreRecyclerData>) : RecyclerView.Adapter<MoreRecyclerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_more_recycler, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvTitle.setText(dataList[position].title)

        if(dataList[position].image != null)
            holder.ivRow.setImageResource(dataList[position].image!!)
        else
            holder.ivRow.visibility = View.GONE

        if(dataList[position].intent != null) {
            holder.row.setOnClickListener {
                context.startActivity(dataList[position].intent!!)
            }
        } else {

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: LinearLayout = itemView.findViewById(R.id.rowMore)
        val ivRow: ImageView = itemView.findViewById(R.id.ivRowMore)
        val tvTitle: TextView = itemView.findViewById(R.id.tvRowMore)
    }
}

data class MoreRecyclerData(@DrawableRes val image: Int?, val title: String, val intent: Intent?)