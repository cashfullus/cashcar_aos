package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cashfulus.cashcarplus.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_point.*

interface PointBottomDialogClickListener {
    fun onClick(category: String)
}

class PointBottomDialog() : BottomSheetDialogFragment() {
    lateinit var clickListener: PointBottomDialogClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_point, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            clickListener = context as PointBottomDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBottomSheetPointX.setOnClickListener {
            dismiss()
        }

        // 'donate', 'positive', 'negative'
        btnBottomSheetPointAll.setOnClickListener {
            clickListener.onClick("all")
            dismiss()
        }
        btnBottomSheetPointSave.setOnClickListener {
            clickListener.onClick("positive")
            dismiss()
        }
        btnBottomSheetPointWithdraw.setOnClickListener {
            clickListener.onClick("negative")
            dismiss()
        }
        btnBottomSheetPointDonate.setOnClickListener {
            clickListener.onClick("donate")
            dismiss()
        }
    }
}