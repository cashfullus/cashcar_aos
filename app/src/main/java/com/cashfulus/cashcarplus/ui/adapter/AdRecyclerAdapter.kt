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
import com.cashfulus.cashcarplus.model.AdResponse
import com.cashfulus.cashcarplus.ui.adinfo.AdInfoActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdRecyclerAdapter(
    private val context: Context,
    private val newList: ArrayList<AdResponse>,
    private val newTabState: String
) : RecyclerView.Adapter<AdRecyclerAdapter.Holder>() {
    private var adList = newList //ArrayList<AdResponse>()
    private val numFormat = DecimalFormat("###,###")
    private var tabState = newTabState // "ongoing" //"scheduled", "done"
    private val TYPE_ONGOING = 1
    private val TYPE_SCHEDULED = 2
    private val TYPE_DONE = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        when (viewType) {
            TYPE_ONGOING -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                // Setting visibility for TYPE_ONGOING
                return Holder(view, viewType)
            }
            TYPE_SCHEDULED -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                // Setting visibility for TYPE_SCHEDULED
                return Holder(view, viewType)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.row_ad, parent, false)
                // Setting visibility for TYPE_DONE
                return Holder(view, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val adPosition = position * 2

        // Setting data for the first row
        Glide.with(context).load(adList[adPosition].image).into(holder.ivRow1)
        holder.tvTitle1.text = adList[adPosition].title
        holder.tvPoint1.text = numFormat.format(adList[adPosition].totalPoint)

        when (tabState) {
            "ongoing" -> {
                holder.tvRegion1.text = adList[adPosition].area
                holder.tvPerson1.text =
                    "${adList[adPosition].recruitingCount}/${adList[adPosition].maxRecruitingCount}"
                holder.tvEndDate1.text = "${adList[adPosition].recruitEndDate!!.substring(5, 7)}.${adList[adPosition].recruitEndDate!!.substring(8, 10)} 마감"

                val maxRecruitingCount: Int = adList[adPosition].maxRecruitingCount ?: 0
                val recruitingCount: Int = adList[adPosition].recruitingCount ?: 0
                val recruitEndDate: String? = adList[adPosition].recruitEndDate
                val maxRecruitingCount1: Int = adList[adPosition + 1].maxRecruitingCount ?: 0
                val recruitingCount1: Int = adList[adPosition + 1].recruitingCount ?: 0
                val recruitEndDate1: String? = adList[adPosition + 1].recruitEndDate

                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val endDate = formatter.parse(recruitEndDate)
                val endDate1 = formatter.parse(recruitEndDate1)

                val currentDate: Calendar = Calendar.getInstance()

                if (maxRecruitingCount > 0 && (recruitingCount >= maxRecruitingCount || endDate.before(currentDate.time))) {
                    // 마감 인원이 다 찼거나 날짜가 지난 경우
                    // 뷰를 보여주는 작업 추가
                    holder.setOngoingTypeCondition(false)
                } else {
                    // 아직 마감 인원이 다 차지 않았고, 날짜가 지나지 않은 경우
                    // 뷰를 숨기는 작업 추가
                    holder.setOngoingTypeCondition(true)
                    holder.row1.setOnClickListener {
                        val intent = Intent(context, AdInfoActivity::class.java)
                        intent.putExtra("id", adList[adPosition].adId)
                        intent.putExtra("canRegister", true)
                        context.startActivity(intent)
                    }
                }

                // Checking if there is data for the second row
                if (adPosition + 1 < adList.size) {
                    holder.row2.visibility = View.VISIBLE

                    // Setting data for the second row
                    Glide.with(context).load(adList[adPosition + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[adPosition + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[adPosition + 1].totalPoint)
                    holder.tvRegion2.text = adList[adPosition + 1].area
                    holder.tvPerson2.text =
                        "${adList[adPosition + 1].recruitingCount}/${adList[adPosition + 1].maxRecruitingCount}"
                    holder.tvEndDate2.text = "${adList[adPosition + 1].recruitEndDate!!.substring(5, 7)}.${adList[adPosition + 1].recruitEndDate!!.substring(8, 10)} 마감"

                    // Checking condition for TYPE_ONGOING (second row)
                    if (maxRecruitingCount1 > 0 && (recruitingCount1 >= maxRecruitingCount1 || endDate1.before(currentDate.time))) {
                        holder.setOngoingTypeCondition(false, secondRow = true)
                    } else {
                        holder.setOngoingTypeCondition(true, secondRow = true)
                        holder.row2.setOnClickListener {
                            val intent = Intent(context, AdInfoActivity::class.java)
                            intent.putExtra("id", adList[adPosition + 1].adId)
                            intent.putExtra("canRegister", true)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    holder.row2.visibility = View.GONE
                }
            }
            "scheduled" -> {
                // Setting data for TYPE_SCHEDULED
                holder.tvDDay1.text = if (adList[adPosition].timeDiff!! > 0) {
                    "D-${adList[adPosition].timeDiff}"
                } else {
                    "D${adList[adPosition].timeDiff}"
                }

                // Checking if there is data for the second row
                if (adPosition + 1 < adList.size) {
                    holder.row2.visibility = View.VISIBLE

                    // Setting data for the second row
                    Glide.with(context).load(adList[adPosition + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[adPosition + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[adPosition + 1].totalPoint)
                    holder.tvDDay2.text = if (adList[adPosition + 1].timeDiff!! > 0) {
                        "D-${adList[adPosition + 1].timeDiff}"
                    } else {
                        "D${adList[adPosition + 1].timeDiff}"
                    }
                } else {
                    holder.row2.visibility = View.GONE
                }
            }
            "done" -> {
                holder.tvRegion1.text = adList[adPosition].area

                // Checking if there is data for the second row
                if (adPosition + 1 < adList.size) {
                    holder.row2.visibility = View.VISIBLE

                    // Setting data for the second row
                    Glide.with(context).load(adList[adPosition + 1].image).into(holder.ivRow2)
                    holder.tvTitle2.text = adList[adPosition + 1].title
                    holder.tvPoint2.text = numFormat.format(adList[adPosition + 1].totalPoint)
                    holder.tvRegion2.text = adList[adPosition + 1].area
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
        return if (adList.size % 2 == 0) adList.size / 2 else adList.size / 2 + 1
    }

    fun refresh(newList: ArrayList<AdResponse>, newTabState: String) {
        adList = newList
        tabState = newTabState
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder variables
        val row1: ConstraintLayout = itemView.findViewById(R.id.clRowAdStart)
        val ivRow1: ImageView = itemView.findViewById(R.id.ivRowAd1)
        val tvTitle1: TextView = itemView.findViewById(R.id.tvRowAdTitle1)
        val tvPoint1: TextView = itemView.findViewById(R.id.tvRowAdPoint1)
        val tvRegion1: TextView = itemView.findViewById(R.id.tvRowAdRegion1)
        val tvPerson1: TextView = itemView.findViewById(R.id.tvRowAdPerson1)
        val tvEndDate1: TextView = itemView.findViewById(R.id.tvRowAdEndDate1)
        val tvDDay1: TextView = itemView.findViewById(R.id.tvRowAdDDay1)
        val tvEnd1: TextView = itemView.findViewById(R.id.tvRowAdEnd1)
        val vBack1: View = itemView.findViewById(R.id.tvRowAdEnd1Back)

        val row2: ConstraintLayout = itemView.findViewById(R.id.clRowAdEnd)
        val ivRow2: ImageView = itemView.findViewById(R.id.ivRowAd2)
        val tvTitle2: TextView = itemView.findViewById(R.id.tvRowAdTitle2)
        val tvPoint2: TextView = itemView.findViewById(R.id.tvRowAdPoint2)
        val tvRegion2: TextView = itemView.findViewById(R.id.tvRowAdRegion2)
        val tvPerson2: TextView = itemView.findViewById(R.id.tvRowAdPerson2)
        val tvEndDate2: TextView = itemView.findViewById(R.id.tvRowAdEndDate2)
        val tvDDay2: TextView = itemView.findViewById(R.id.tvRowAdDDay2)
        val tvEnd2: TextView = itemView.findViewById(R.id.tvRowAdEnd2)
        val vBack2: View = itemView.findViewById(R.id.tvRowAdEnd2Back)

        init {
            // Setting visibility based on viewType
            when (viewType) {
                TYPE_ONGOING -> {
                    // Setting visibility for TYPE_ONGOING
                    tvRegion1.visibility = View.VISIBLE
                    tvPerson1.visibility = View.VISIBLE
                    ivRow1.visibility = View.VISIBLE
                    tvEndDate1.visibility = View.VISIBLE
                    tvDDay1.visibility = View.GONE
                    tvEnd1.visibility = View.GONE
                    vBack1.visibility = View.GONE

                    tvRegion2.visibility = View.VISIBLE
                    tvPerson2.visibility = View.VISIBLE
                    ivRow2.visibility = View.VISIBLE
                    tvEndDate2.visibility = View.VISIBLE
                    tvDDay2.visibility = View.GONE
                    tvEnd2.visibility = View.GONE
                    vBack2.visibility = View.GONE
                }
                TYPE_SCHEDULED -> {
                    // Setting visibility for TYPE_SCHEDULED
                    tvRegion1.visibility = View.INVISIBLE
                    tvPerson1.visibility = View.INVISIBLE
                    ivRow1.visibility = View.INVISIBLE
                    tvEndDate1.visibility = View.INVISIBLE
                    tvDDay1.visibility = View.VISIBLE
                    tvEnd1.visibility = View.GONE
                    vBack1.visibility = View.GONE

                    tvRegion2.visibility = View.INVISIBLE
                    tvPerson2.visibility = View.INVISIBLE
                    ivRow2.visibility = View.INVISIBLE
                    tvEndDate2.visibility = View.INVISIBLE
                    tvDDay2.visibility = View.VISIBLE
                    tvEnd2.visibility = View.GONE
                    vBack2.visibility = View.GONE
                }
                else -> {
                    // Setting visibility for TYPE_DONE
                    tvRegion1.visibility = View.VISIBLE
                    tvPerson1.visibility = View.INVISIBLE
                    ivRow1.visibility = View.VISIBLE
                    tvEndDate1.visibility = View.VISIBLE
                    tvDDay1.visibility = View.GONE
                    tvEnd1.visibility = View.VISIBLE
                    vBack1.visibility = View.VISIBLE

                    tvRegion2.visibility = View.VISIBLE
                    tvPerson2.visibility = View.INVISIBLE
                    ivRow2.visibility = View.VISIBLE
                    tvEndDate2.visibility = View.VISIBLE
                    tvDDay2.visibility = View.GONE
                    tvEnd2.visibility = View.VISIBLE
                    vBack2.visibility = View.VISIBLE
                }
            }
        }

        fun setOngoingTypeCondition(condition: Boolean, secondRow: Boolean = false) {
            if (secondRow) {
                if (!condition) {
                    tvRegion2.visibility = View.VISIBLE
                    tvPerson2.visibility = View.INVISIBLE
                    ivRow2.visibility = View.VISIBLE
                    tvEndDate2.visibility = View.VISIBLE
                    tvDDay2.visibility = View.GONE
                    tvEnd2.visibility = View.VISIBLE
                    vBack2.visibility = View.VISIBLE
                } else {
                    tvRegion2.visibility = View.VISIBLE
                    tvPerson2.visibility = View.VISIBLE
                    ivRow2.visibility = View.VISIBLE
                    tvEndDate2.visibility = View.VISIBLE
                    tvDDay2.visibility = View.GONE
                    tvEnd2.visibility = View.GONE
                    vBack2.visibility = View.GONE
                }
            } else {
                if (!condition) {
                    tvRegion1.visibility = View.VISIBLE
                    tvPerson1.visibility = View.INVISIBLE
                    ivRow1.visibility = View.VISIBLE
                    tvEndDate1.visibility = View.VISIBLE
                    tvDDay1.visibility = View.GONE
                    tvEnd1.visibility = View.VISIBLE
                    vBack1.visibility = View.VISIBLE
                } else {
                    tvRegion1.visibility = View.VISIBLE
                    tvPerson1.visibility = View.VISIBLE
                    ivRow1.visibility = View.VISIBLE
                    tvEndDate1.visibility = View.VISIBLE
                    tvDDay1.visibility = View.GONE
                    tvEnd1.visibility = View.GONE
                    vBack1.visibility = View.GONE
                }
            }
        }
    }
}

