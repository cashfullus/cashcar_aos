package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.data.repository.*
import com.cashfulus.cashcarplus.model.Driving
import com.cashfulus.cashcarplus.model.MissionAdditional
import com.cashfulus.cashcarplus.model.MissionImportant
import com.cashfulus.cashcarplus.ui.mission.MissionCertActivity
import com.cashfulus.cashcarplus.view.*
import com.cashfulus.cashcarplus.view.MISSION_STATE_SUCCESS
import java.text.DecimalFormat

/** 각 버튼의 상태 정리
 *  MISSION_STATUS_STAND_BY('미션 대기')
 *  -> MISSION_STATUS_ON_GOING('인증하기')
 *  -> MISSION_STATUS_REVIEW('검토중')
 *  -> 성공 시 : MISSION_STATUS_SUCCESS('미션 성공')
 *     실패 시 : MISSION_STATUS_REJECT('재인증하기')
 *  -> MISSION_STATUS_REAUTH('검토중')
 *  -> 최종 실패 : MISSION_STATUS_FAIL('미션 실패')
 */

class MissionImportantRecyclerAdapter(private val context: Context, private val dataList: ArrayList<MissionImportant>, private val title: String) : RecyclerView.Adapter<MissionImportantRecyclerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionImportantRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_mission, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvTitle1.visibility = View.GONE
        holder.ivPoint1.visibility = View.GONE
        holder.tvPoint1.visibility = View.GONE
        holder.tvTitle2.visibility = View.GONE
        holder.ivPoint2.visibility = View.GONE
        holder.tvPoint2.visibility = View.GONE

        when(dataList[position*2].status) {
            MISSION_STATUS_ON_GOING -> {
                holder.btn1.setState(MISSION_STATE_NEED_CERT)
                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "important")
                    intent.putExtra("title", title)
                    intent.putExtra("order", dataList[position*2].order)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_STAND_BY -> holder.btn1.setState(MISSION_STATE_WAITING)
            MISSION_STATUS_REVIEW -> holder.btn1.setState(MISSION_STATE_CHECKING)
            MISSION_STATUS_REJECT -> {
                holder.btn1.setState(MISSION_STATE_NEED_RECERT)
                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "important")
                    intent.putExtra("title", title)
                    intent.putExtra("order", dataList[position*2].order)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_REAUTH -> holder.btn1.setState(MISSION_STATE_CHECKING)
            MISSION_STATUS_SUCCESS -> holder.btn1.setState(MISSION_STATE_SUCCESS)
            MISSION_STATUS_FAIL -> holder.btn1.setState(MISSION_STATE_FAIL)
        }

        if(dataList[position*2].status == MISSION_STATUS_SUCCESS)
            holder.iv1.setImageResource(R.drawable.ic_mission_important_success)
        else
            holder.iv1.setImageResource(R.drawable.ic_mission_important_fail)

        if(dataList[position*2].status == MISSION_STATUS_ON_GOING || dataList[position*2].status == MISSION_STATUS_REJECT)
            holder.tvDate1.text = dataList[position*2].endDate.substring(0, 10).replace('-', '.')+" 까지"
        else
            holder.tvDate1.visibility = View.GONE

        if(position*2+1 < dataList.size) {
            when(dataList[position*2+1].status) {
                MISSION_STATUS_ON_GOING -> {
                    holder.btn2.setState(MISSION_STATE_NEED_CERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "important")
                        intent.putExtra("title", title)
                        intent.putExtra("order", dataList[position*2+1].order)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_STAND_BY -> holder.btn2.setState(MISSION_STATE_WAITING)
                MISSION_STATUS_REVIEW -> holder.btn2.setState(MISSION_STATE_CHECKING)
                MISSION_STATUS_REJECT -> {
                    holder.btn2.setState(MISSION_STATE_NEED_RECERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "important")
                        intent.putExtra("title", title)
                        intent.putExtra("order", dataList[position*2+1].order)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_REAUTH -> holder.btn2.setState(MISSION_STATE_CHECKING)
                MISSION_STATUS_SUCCESS -> holder.btn2.setState(MISSION_STATE_SUCCESS)
                MISSION_STATUS_FAIL -> holder.btn2.setState(MISSION_STATE_FAIL)
            }

            if(dataList[position*2+1].status == MISSION_STATUS_SUCCESS)
                holder.iv2.setImageResource(R.drawable.ic_mission_important_success)
            else
                holder.iv2.setImageResource(R.drawable.ic_mission_important_fail)

            if(dataList[position*2+1].status == MISSION_STATUS_ON_GOING || dataList[position*2+1].status == MISSION_STATUS_REJECT)
                holder.tvDate2.text = dataList[position*2+1].endDate.substring(0, 10).replace('-', '.')+" 까지"
            else
                holder.tvDate2.visibility = View.GONE
        } else {
            holder.iv2.visibility = View.INVISIBLE
            holder.btn2.visibility = View.GONE
            holder.tvDate2.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return if(dataList.size%2==0)
            dataList.size/2
        else
            dataList.size/2+1
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv1: ImageView = itemView.findViewById(R.id.ivRowMission1)
        val tvTitle1: TextView = itemView.findViewById(R.id.tvRowMissionTitle1)
        val btn1: MissionButton = itemView.findViewById(R.id.btnRowMission1)
        val ivPoint1: ImageView = itemView.findViewById(R.id.ivRowMissionPoint1)
        val tvPoint1: TextView = itemView.findViewById(R.id.tvRowMissionPoint1)
        val tvDate1: TextView = itemView.findViewById(R.id.tvRowMissionDate1)

        val iv2: ImageView = itemView.findViewById(R.id.ivRowMission2)
        val tvTitle2: TextView = itemView.findViewById(R.id.tvRowMissionTitle2)
        val btn2: MissionButton = itemView.findViewById(R.id.btnRowMission2)
        val ivPoint2: ImageView = itemView.findViewById(R.id.ivRowMissionPoint2)
        val tvPoint2: TextView = itemView.findViewById(R.id.tvRowMissionPoint2)
        val tvDate2: TextView = itemView.findViewById(R.id.tvRowMissionDate2)
    }
}


class MissionAdditionalRecyclerAdapter(private val context: Context, private val dataList: ArrayList<MissionAdditional>, private val title: String) : RecyclerView.Adapter<MissionAdditionalRecyclerAdapter.Holder>() {
    val numFormat = DecimalFormat("###,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionAdditionalRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_mission, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when(dataList[position*2].status) {
            MISSION_STATUS_ON_GOING -> {
                holder.btn1.setState(ADDTIONAL_MISSION_STATE_NEED_CERT)

                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "additional")
                    intent.putExtra("title", title)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    intent.putExtra("missionname", dataList[position*2].title)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_STAND_BY -> holder.btn1.setState(ADDTIONAL_MISSION_STATE_WAITING)
            MISSION_STATUS_REVIEW -> holder.btn1.setState(ADDTIONAL_MISSION_STATE_CHECKING)
            MISSION_STATUS_REJECT -> {
                holder.btn1.setState(ADDTIONAL_MISSION_STATE_NEED_RECERT)

                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "additional")
                    intent.putExtra("title", title)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    intent.putExtra("missionname", dataList[position*2].title)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_REAUTH -> holder.btn1.setState(ADDTIONAL_MISSION_STATE_CHECKING)
            MISSION_STATUS_SUCCESS -> holder.btn1.setState(ADDTIONAL_MISSION_STATE_SUCCESS)
            MISSION_STATUS_FAIL -> holder.btn1.setState(ADDTIONAL_MISSION_STATE_FAIL)
        }
        holder.tvTitle1.text = dataList[position*2].title
        holder.tvPoint1.text = numFormat.format(dataList[position*2].point)

        if(dataList[position*2].status == MISSION_STATUS_SUCCESS)
            holder.iv1.setImageResource(R.drawable.ic_mission_additional_success)
        else
            holder.iv1.setImageResource(R.drawable.ic_mission_additional_fail)

        if(dataList[position*2].status == MISSION_STATUS_ON_GOING || dataList[position*2].status == MISSION_STATUS_REJECT)
            holder.tvDate1.text = dataList[position*2].endDate.substring(0, 10).replace('-', '.')+" 까지"
        else
            holder.tvDate1.visibility = View.GONE

        if(position*2+1 < dataList.size) {
            when(dataList[position*2+1].status) {
                MISSION_STATUS_ON_GOING -> {
                    holder.btn2.setState(ADDTIONAL_MISSION_STATE_NEED_CERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "additional")
                        intent.putExtra("title", title)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        intent.putExtra("missionname", dataList[position*2+1].title)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_STAND_BY -> holder.btn2.setState(ADDTIONAL_MISSION_STATE_WAITING)
                MISSION_STATUS_REVIEW -> holder.btn2.setState(ADDTIONAL_MISSION_STATE_CHECKING)
                MISSION_STATUS_REJECT -> {
                    holder.btn2.setState(ADDTIONAL_MISSION_STATE_NEED_RECERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "additional")
                        intent.putExtra("title", title)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        intent.putExtra("missionname", dataList[position*2+1].title)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_REAUTH -> holder.btn2.setState(ADDTIONAL_MISSION_STATE_CHECKING)
                MISSION_STATUS_SUCCESS -> holder.btn2.setState(ADDTIONAL_MISSION_STATE_SUCCESS)
                MISSION_STATUS_FAIL -> holder.btn2.setState(ADDTIONAL_MISSION_STATE_FAIL)
            }
            holder.tvTitle2.text = dataList[position*2+1].title
            holder.tvPoint2.text = numFormat.format(dataList[position*2+1].point)

            if(dataList[position*2+1].status == MISSION_STATUS_SUCCESS)
                holder.iv2.setImageResource(R.drawable.ic_mission_additional_success)
            else
                holder.iv2.setImageResource(R.drawable.ic_mission_additional_fail)

            if(dataList[position*2+1].status == MISSION_STATUS_ON_GOING || dataList[position*2+1].status == MISSION_STATUS_REJECT)
                holder.tvDate2.text = dataList[position*2+1].endDate.substring(0, 10).replace('-', '.')+" 까지"
            else
                holder.tvDate2.visibility = View.GONE
        } else {
            holder.iv2.visibility = View.INVISIBLE
            holder.tvTitle2.visibility = View.GONE
            holder.btn2.visibility = View.GONE
            holder.ivPoint2.visibility = View.GONE
            holder.tvPoint2.visibility = View.GONE
            holder.tvDate2.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return if(dataList.size%2==0)
            dataList.size/2
        else
            dataList.size/2+1
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv1: ImageView = itemView.findViewById(R.id.ivRowMission1)
        val tvTitle1: TextView = itemView.findViewById(R.id.tvRowMissionTitle1)
        val btn1: MissionButton = itemView.findViewById(R.id.btnRowMission1)
        val tvPoint1: TextView = itemView.findViewById(R.id.tvRowMissionPoint1)
        val tvDate1: TextView = itemView.findViewById(R.id.tvRowMissionDate1)

        val iv2: ImageView = itemView.findViewById(R.id.ivRowMission2)
        val tvTitle2: TextView = itemView.findViewById(R.id.tvRowMissionTitle2)
        val btn2: MissionButton = itemView.findViewById(R.id.btnRowMission2)
        val ivPoint2: ImageView = itemView.findViewById(R.id.ivRowMissionPoint2)
        val tvPoint2: TextView = itemView.findViewById(R.id.tvRowMissionPoint2)
        val tvDate2: TextView = itemView.findViewById(R.id.tvRowMissionDate2)
    }
}


class DrivingRecyclerAdapter(private val context: Context, private val dataList: ArrayList<Driving>, private val title: String) : RecyclerView.Adapter<DrivingRecyclerAdapter.Holder>() {
    val numFormat = DecimalFormat("###,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrivingRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_mission, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when(dataList[position*2].status) {
            MISSION_STATUS_ON_GOING -> {
                holder.btn1.setState(DRIVING_STATE_NEED_CERT)

                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "driving")
                    intent.putExtra("title", title)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_REVIEW -> holder.btn1.setState(DRIVING_STATE_CHECKING)
            MISSION_STATUS_REJECT -> {
                holder.btn1.setState(DRIVING_STATE_NEED_RECERT)

                holder.btn1.setOnClickListener {
                    val intent = Intent(context, MissionCertActivity::class.java)
                    intent.putExtra("type", "driving")
                    intent.putExtra("title", title)
                    intent.putExtra("endDate", dataList[position*2].endDate)
                    intent.putExtra("id", dataList[position*2].missionId)
                    context.startActivity(intent)
                }
            }
            MISSION_STATUS_REAUTH -> holder.btn1.setState(DRIVING_STATE_CHECKING)
            MISSION_STATUS_SUCCESS -> holder.btn1.setState(DRIVING_STATE_SUCCESS)
            // 아래 두 상태는 사용되지 않음.
            MISSION_STATUS_STAND_BY -> holder.btn1.setState(DRIVING_STATE_WAITING)
            MISSION_STATUS_FAIL -> holder.btn1.setState(DRIVING_STATE_FAIL)
        }
        holder.tvTitle1.text = dataList[position*2].title
        holder.tvPoint1.text = numFormat.format(dataList[position*2].point)

        if(dataList[position*2].status == MISSION_STATUS_SUCCESS)
            holder.iv1.setImageResource(R.drawable.ic_driving_success)
        else
            holder.iv1.setImageResource(R.drawable.ic_driving_fail)

        if(dataList[position*2].status == MISSION_STATUS_ON_GOING || dataList[position*2].status == MISSION_STATUS_REJECT)
            holder.tvDate1.text = dataList[position*2].endDate.substring(0, 10).replace('-', '.')+" 까지"
        else
            holder.tvDate1.visibility = View.GONE

        if(position*2+1 < dataList.size) {
            when(dataList[position*2+1].status) {
                MISSION_STATUS_ON_GOING -> {
                    holder.btn2.setState(DRIVING_STATE_NEED_CERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "driving")
                        intent.putExtra("title", title)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_REVIEW -> holder.btn2.setState(DRIVING_STATE_CHECKING)
                MISSION_STATUS_REJECT -> {
                    holder.btn2.setState(DRIVING_STATE_NEED_RECERT)

                    holder.btn2.setOnClickListener {
                        val intent = Intent(context, MissionCertActivity::class.java)
                        intent.putExtra("type", "driving")
                        intent.putExtra("title", title)
                        intent.putExtra("endDate", dataList[position*2+1].endDate)
                        intent.putExtra("id", dataList[position*2+1].missionId)
                        context.startActivity(intent)
                    }
                }
                MISSION_STATUS_REAUTH -> holder.btn2.setState(DRIVING_STATE_CHECKING)
                MISSION_STATUS_SUCCESS -> holder.btn2.setState(DRIVING_STATE_SUCCESS)
                // 아래 두 상태는 사용되지 않음.
                MISSION_STATUS_STAND_BY -> holder.btn2.setState(DRIVING_STATE_WAITING)
                MISSION_STATUS_FAIL -> holder.btn2.setState(DRIVING_STATE_FAIL)
            }
            holder.tvTitle2.text = dataList[position*2+1].title
            holder.tvPoint2.text = numFormat.format(dataList[position*2+1].point)

            if(dataList[position*2+1].status == MISSION_STATUS_SUCCESS)
                holder.iv2.setImageResource(R.drawable.ic_driving_success)
            else
                holder.iv2.setImageResource(R.drawable.ic_driving_fail)

            if(dataList[position*2+1].status == MISSION_STATUS_ON_GOING || dataList[position*2+1].status == MISSION_STATUS_REJECT)
                holder.tvDate2.text = dataList[position*2+1].endDate.substring(0, 10).replace('-', '.')+" 까지"
            else
                holder.tvDate2.visibility = View.GONE
        } else {
            holder.iv2.visibility = View.INVISIBLE
            holder.tvTitle2.visibility = View.GONE
            holder.btn2.visibility = View.GONE
            holder.ivPoint2.visibility = View.GONE
            holder.tvPoint2.visibility = View.GONE
            holder.tvDate2.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return if(dataList.size%2==0)
            dataList.size/2
        else
            dataList.size/2+1
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv1: ImageView = itemView.findViewById(R.id.ivRowMission1)
        val tvTitle1: TextView = itemView.findViewById(R.id.tvRowMissionTitle1)
        val btn1: MissionButton = itemView.findViewById(R.id.btnRowMission1)
        val tvPoint1: TextView = itemView.findViewById(R.id.tvRowMissionPoint1)
        val tvDate1: TextView = itemView.findViewById(R.id.tvRowMissionDate1)

        val iv2: ImageView = itemView.findViewById(R.id.ivRowMission2)
        val tvTitle2: TextView = itemView.findViewById(R.id.tvRowMissionTitle2)
        val btn2: MissionButton = itemView.findViewById(R.id.btnRowMission2)
        val ivPoint2: ImageView = itemView.findViewById(R.id.ivRowMissionPoint2)
        val tvPoint2: TextView = itemView.findViewById(R.id.tvRowMissionPoint2)
        val tvDate2: TextView = itemView.findViewById(R.id.tvRowMissionDate2)
    }
}