package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.cashfulus.cashcarplus.R

class SpinnerWithHintAdapter(context: Context, objects: List<String>): ArrayAdapter<String>(context, R.layout.row_spinner_origin, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        if(position == count) {
            view.findViewById<TextView>(R.id.tvRowSpinnerOrigin).text = ""
            view.findViewById<TextView>(R.id.tvRowSpinnerOrigin).hint = getItem(count)
        }

        return view
    }

    override fun getCount(): Int {
        return super.getCount() - 1
    }
}