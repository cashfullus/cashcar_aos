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
import com.cashfulus.cashcarplus.model.AdResponse
import com.cashfulus.cashcarplus.ui.adinfo.AdInfoActivity
import java.text.DecimalFormat

class AdRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<AdRecyclerAdapter.Holder>() {
    private var adList = ArrayList<AdResponse>()
    val numFormat = DecimalFormat("###,###")
    // 클릭 가능/불가능
    var tabState = "ongoing" //"scheduled", "done"
    private val TYPE_ONGOING = 1
    private val TYPE_SCHEDULED = 2
    private val TYPE_DONE = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdRecyclerAdapter.Holder {
        when(viewType) {
            TYPE_ONGOING -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                view.findViewById<TextView>(R.id.tvRowAdRegion1).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson1).visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd1Person).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate1).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay1).visibility = View.GONE
                view.findViewById<TextView>(R.id.tvRowAdEnd1).visibility = View.GONE

                view.findViewById<TextView>(R.id.tvRowAdRegion2).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson2).visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd2Person).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate2).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay2).visibility = View.GONE
                view.findViewById<TextView>(R.id.tvRowAdEnd2).visibility = View.GONE
                return Holder(view)
            }
            TYPE_SCHEDULED -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                view.findViewById<TextView>(R.id.tvRowAdRegion1).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson1).visibility = View.INVISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd1Person).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate1).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay1).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEnd1).visibility = View.GONE

                view.findViewById<TextView>(R.id.tvRowAdRegion2).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson2).visibility = View.INVISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd2Person).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate2).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay2).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEnd2).visibility = View.GONE
                return Holder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                view.findViewById<TextView>(R.id.tvRowAdRegion1).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson1).visibility = View.INVISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd1Person).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate1).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay1).visibility = View.GONE
                view.findViewById<TextView>(R.id.tvRowAdEnd1).visibility = View.VISIBLE

                view.findViewById<TextView>(R.id.tvRowAdRegion2).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvRowAdPerson2).visibility = View.INVISIBLE
                view.findViewById<ImageView>(R.id.ivRowAd2Person).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdEndDate2).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.tvRowAdDDay2).visibility = View.GONE
                view.findViewById<TextView>(R.id.tvRowAdEnd2).visibility = View.VISIBLE
                return Holder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: AdRecyclerAdapter.Holder, position: Int) {
        Glide.with(context).load(adList[position * 2].image).into(holder.ivRow1)
        holder.tvTitle1.text = adList[position * 2].title
        holder.tvPoint1.text = numFormat.format(adList[position * 2].totalPoint)

        when(tabState) {
            "ongoing" -> {
                holder.tvRegion1.text = adList[position * 2].area
                holder.tvPerson1.text = adList[position * 2].recruitingCount.toString() + "/" + adList[position * 2].maxRecruitingCount.toString()
                holder.tvEndDate1.text = adList[position * 2].recruitEndDate!!.substring(5, 7) + "." + adList[position * 2].recruitEndDate!!.substring(8, 10) + " 마감" //2021-05-22 23:59:59 -> 05.22 마감

                holder.row1.setOnClickListener {
                    val intent = Intent(context, AdInfoActivity::class.java)
                    intent.putExtra("id", adList[position * 2].adId)
                    intent.putExtra("canRegister", true)
                    context.startActivity(intent)
                }

                if (position * 2 + 1 < adList.size) {
                    // 오른쪽에도 데이터 셋팅
                    holder.row2.visibility = View.VISIBLE

                    Glide.with(context).load(adList[position * 2 + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[position * 2 + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[position * 2 + 1].totalPoint)
                    holder.tvRegion2.text = adList[position * 2 + 1].area
                    holder.tvPerson2.text = adList[position * 2 + 1].recruitingCount.toString() + "/" + adList[position * 2 + 1].maxRecruitingCount.toString()
                    holder.tvEndDate2.text = adList[position * 2].recruitEndDate!!.substring(5, 7) + "." + adList[position * 2].recruitEndDate!!.substring(8, 10) + " 마감" //2021-05-22 23:59:59 -> 05.22 마감

                    holder.row2.setOnClickListener {
                        val intent = Intent(context, AdInfoActivity::class.java)
                        intent.putExtra("id", adList[position * 2 + 1].adId)
                        intent.putExtra("canRegister", true)
                        context.startActivity(intent)
                    }
                } else {
                    holder.row2.visibility = View.GONE
                }
            }
            "scheduled" -> {
                holder.tvDDay1.text = "D"+adList[position*2].timeDiff.toString()

                holder.row1.setOnClickListener {
                    val intent = Intent(context, AdInfoActivity::class.java)
                    intent.putExtra("id", adList[position * 2].adId)
                    intent.putExtra("canRegister", false)
                    context.startActivity(intent)
                }

                if (position*2+1 < adList.size) {
                    // 오른쪽에도 데이터 셋팅
                    holder.row2.visibility = View.VISIBLE

                    Glide.with(context).load(adList[position * 2 + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[position * 2 + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[position * 2 + 1].totalPoint)
                    holder.tvDDay2.text = "D-"+adList[position*2+1].timeDiff.toString()

                    holder.row2.setOnClickListener {
                        val intent = Intent(context, AdInfoActivity::class.java)
                        intent.putExtra("id", adList[position * 2 + 1].adId)
                        intent.putExtra("canRegister", false)
                        context.startActivity(intent)
                    }
                } else {
                    holder.row2.visibility = View.GONE
                }
            }
            "done" -> {
                holder.tvRegion1.text = adList[position * 2].area

                if (position*2+1 < adList.size) {
                    // 오른쪽에도 데이터 셋팅
                    holder.row2.visibility = View.VISIBLE

                    Glide.with(context).load(adList[position * 2 + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[position * 2 + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[position * 2 + 1].totalPoint)
                    holder.tvRegion2.text = adList[position * 2 + 1].area
                } else {
                    holder.row2.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (tabState == "ongoing") TYPE_ONGOING else if (tabState == "scheduled") TYPE_SCHEDULED else TYPE_DONE
    }

    override fun getItemCount(): Int {
        return if(adList.size%2==0)
            adList.size/2
        else
            adList.size/2+1
    }

    fun refresh(newList: ArrayList<AdResponse>, newTabState: String) {
        adList = newList
        tabState = newTabState
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row1: ConstraintLayout = itemView.findViewById(R.id.clRowAdStart)
        val ivRow1: ImageView = itemView.findViewById(R.id.ivRowAd1)
        val tvTitle1: TextView = itemView.findViewById(R.id.tvRowAdTitle1)
        val tvPoint1: TextView = itemView.findViewById(R.id.tvRowAdPoint1)
        val tvRegion1: TextView = itemView.findViewById(R.id.tvRowAdRegion1)
        val tvPerson1: TextView = itemView.findViewById(R.id.tvRowAdPerson1)
        val tvEndDate1: TextView = itemView.findViewById(R.id.tvRowAdEndDate1)
        val tvDDay1: TextView = itemView.findViewById(R.id.tvRowAdDDay1)


        val row2: ConstraintLayout = itemView.findViewById(R.id.clRowAdEnd)
        val ivRow2: ImageView = itemView.findViewById(R.id.ivRowAd2)
        val tvTitle2: TextView = itemView.findViewById(R.id.tvRowAdTitle2)
        val tvPoint2: TextView = itemView.findViewById(R.id.tvRowAdPoint2)
        val tvRegion2: TextView = itemView.findViewById(R.id.tvRowAdRegion2)
        val tvPerson2: TextView = itemView.findViewById(R.id.tvRowAdPerson2)
        val tvEndDate2: TextView = itemView.findViewById(R.id.tvRowAdEndDate2)
        val tvDDay2: TextView = itemView.findViewById(R.id.tvRowAdDDay2)
    }
}