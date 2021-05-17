package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.CashcartipList
import com.cashfulus.cashcarplus.ui.cashcartip.CashcartipActivity

class CashcartipRecyclerAdapter(private val context: Context, private val dataList: CashcartipList) : RecyclerView.Adapter<CashcartipRecyclerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashcartipRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_cashcartip, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(context).load(dataList[position].image).into(holder.ivRow)
        holder.tvTitle.text = dataList[position].title
        holder.tvContents.text = dataList[position].description

        holder.row.setOnClickListener {
            val intent = Intent(context, CashcartipActivity::class.java)
            intent.putExtra("postId", dataList[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: ConstraintLayout = itemView.findViewById(R.id.rowCashcartip)
        val ivRow: ImageView = itemView.findViewById(R.id.ivRowCashcartip)
        val tvTitle: TextView = itemView.findViewById(R.id.tvRowCashcartipTitle)
        val tvContents: TextView = itemView.findViewById(R.id.tvRowCashcartipContents)
    }
}