package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.MyCarResponse
import com.cashfulus.cashcarplus.ui.car.CarInfoActivity
import java.text.DecimalFormat

class MyCarRecyclerAdapter(private val context: Context, private val requestActivity: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<MyCarRecyclerAdapter.Holder>() {

    private var dataList = ArrayList<MyCarResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCarRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_mycar_recycler, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvCarNumber.text = dataList[position].carNumber
        holder.tvYear.text = dataList[position].year.toString()
        holder.tvBrand.text = dataList[position].brand
        holder.tvModel.text = dataList[position].model

        if(dataList[position].supporters == 1) {
            holder.row.background = ContextCompat.getDrawable(context, R.drawable.row_mycar_supporters)
        } else {
            holder.row.background = ContextCompat.getDrawable(context, R.drawable.row_mycar_not_supporters)
        }

        holder.row.setOnClickListener {
            val intent = Intent(context, CarInfoActivity::class.java)
            intent.putExtra("vehicle_id", dataList[position].vehicleId)
            intent.putExtra("checkboxAvailable", dataList.size >= 2)
            requestActivity.launch(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newList: ArrayList<MyCarResponse>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: ConstraintLayout = itemView.findViewById(R.id.rowMyCar)
        val tvCarNumber: TextView = itemView.findViewById(R.id.tvRowMyCarNumber)
        val tvYear: TextView = itemView.findViewById(R.id.tvRowMyCarYear)
        val tvBrand: TextView = itemView.findViewById(R.id.tvRowMyCarBrand)
        val tvModel: TextView = itemView.findViewById(R.id.tvRowMyCarModel)
    }
}