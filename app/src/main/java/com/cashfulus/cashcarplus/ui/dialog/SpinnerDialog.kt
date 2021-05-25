package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import kotlinx.android.synthetic.main.dialog_spinner.*

interface SpinnerDialogClickListener {
    fun onSelected(str: String)
}

class SpinnerDialog(private val list: Array<String>) : DialogFragment() {
    lateinit var clickListener: SpinnerDialogClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_spinner, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            clickListener = context as SpinnerDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSpinner.adapter = SpinnerRecyclerAdapter()
        rvSpinner.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()

        // 꼭 DialogFragment 클래스에서 선언하지 않아도 된다.
        val windowManager = App().context().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()

        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getSize(size)

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealSize(size)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getSize(size)
        }*/

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        val deviceWidth = size.x

        dialog!!.window!!.setLayout((deviceWidth * 0.9).toInt(), lp.height)
    }

    inner class SpinnerRecyclerAdapter() : RecyclerView.Adapter<SpinnerRecyclerAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinnerRecyclerAdapter.Holder {
            val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.tvStr.text = list[position]

            holder.tvStr.setOnClickListener {
                clickListener.onSelected(list[position])
                dismiss()
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvStr: TextView = itemView.findViewById(android.R.id.text1)
        }
    }
}