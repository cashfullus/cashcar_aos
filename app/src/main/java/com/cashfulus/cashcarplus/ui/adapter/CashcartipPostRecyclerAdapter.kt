package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.CashcartipImage
import com.cashfulus.cashcarplus.model.CashcartipList
import com.cashfulus.cashcarplus.ui.cashcartip.CashcartipActivity

class CashcartipPostRecyclerAdapter(private val context: Context, private val dataList: ArrayList<CashcartipImage>) : RecyclerView.Adapter<CashcartipPostRecyclerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashcartipPostRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_cashcartip_post, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(context).load(dataList[position].image).into(holder.ivRow)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRow: ImageView = itemView.findViewById(R.id.ivRowCashcartipPost)
    }
}