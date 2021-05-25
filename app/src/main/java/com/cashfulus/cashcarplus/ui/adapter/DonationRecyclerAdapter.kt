package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.DonationListResponse
import com.cashfulus.cashcarplus.ui.donation.DonationDialog


class DonationRecyclerAdapter(private val context: Context, private val dataList: ArrayList<DonationListResponse>, private val fragmentManager: FragmentManager, private val donationId: MutableLiveData<Int>) : RecyclerView.Adapter<DonationRecyclerAdapter.Holder>() {
    private var lastChecked: Button? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_donation, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvOrganization1.text = dataList[position * 2].name
        holder.tvContents1.text = dataList[position * 2].imageInformation[0].description
        Glide.with(context).load(dataList[position * 2].logo).into(holder.ivTitle1)
        Glide.with(context).load(dataList[position * 2].imageInformation[0].image).into(holder.ivBig1)

        // Left Radio Button
        holder.rb1.tag = position*2
        holder.rb1.setOnClickListener {
            holder.rb1.isSelected = true

            if(lastChecked != null) {
                lastChecked!!.isSelected = false
                //fonts.get(lastCheckedPos).setSelected(false)
            }

            lastChecked = holder.rb1
            donationId.postValue(dataList[position * 2].id)
        }

        holder.rowLeft.setOnClickListener {
            val dialog = DonationDialog(dataList[position * 2])
            dialog.show(fragmentManager, "Donation")
        }

        if(position*2+1 < dataList.size) {
            holder.tvOrganization2.text = dataList[position * 2 + 1].name
            holder.tvContents2.text = dataList[position * 2 + 1].imageInformation[0].description
            Glide.with(context).load(dataList[position * 2 + 1].logo).into(holder.ivTitle2)
            Glide.with(context).load(dataList[position * 2 + 1].imageInformation[0].image).into(holder.ivBig2)

            // Right Radio Button
            holder.rb2.tag = position*2+1
            holder.rb2.setOnClickListener {
                holder.rb2.isSelected = true

                if(lastChecked != null) {
                    lastChecked!!.isSelected = false
                    //fonts.get(lastCheckedPos).setSelected(false)
                }

                lastChecked = holder.rb2
                donationId.postValue(dataList[position * 2 + 1].id)
            }

            holder.rowRight.setOnClickListener {
                val dialog = DonationDialog(dataList[position * 2 + 1])
                dialog.show(fragmentManager, "Donation")
            }
        }
    }

    override fun getItemCount(): Int {
        return if(dataList.size%2==0)
            dataList.size/2
        else
            dataList.size/2+1
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowLeft: ConstraintLayout = itemView.findViewById(R.id.rowDonationLeft)
        val ivTitle1: ImageView = itemView.findViewById(R.id.ivDonationTitle1)
        val tvOrganization1: TextView = itemView.findViewById(R.id.tvDonationOrganization1)
        val ivBig1: ImageView = itemView.findViewById(R.id.ivDonationBig1)
        val tvContents1: TextView = itemView.findViewById(R.id.tvDonationContents1)
        val rb1: Button = itemView.findViewById(R.id.rbDonation1)

        val rowRight: ConstraintLayout = itemView.findViewById(R.id.rowDonationRight)
        val ivTitle2: ImageView = itemView.findViewById(R.id.ivDonationTitle2)
        val tvOrganization2: TextView = itemView.findViewById(R.id.tvDonationOrganization2)
        val ivBig2: ImageView = itemView.findViewById(R.id.ivDonationBig2)
        val tvContents2: TextView = itemView.findViewById(R.id.tvDonationContents2)
        val rb2: Button = itemView.findViewById(R.id.rbDonation2)
    }
}