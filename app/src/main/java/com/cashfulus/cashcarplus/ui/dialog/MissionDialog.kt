package com.cashfulus.cashcarplus.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import kotlinx.android.synthetic.main.dialog_mission.*
import java.lang.ClassCastException

class MissionDialog(private val title: String, private val message: String, private val btnText: String) : DialogFragment() {

    lateinit var listener: MissionDialogClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_mission, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as MissionDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDialogMissionTitle.text = title
        tvDialogMissionContents.text = message
        btnDialogMission.text = btnText

        btnDialogMissionX.setOnClickListener {
            listener.onNegativeClick()
            dismiss()
        }

        btnDialogMission.setOnClickListener {
            listener.onPositiveClick()
            dismiss()
        }
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

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}

interface MissionDialogClickListener {
    fun onPositiveClick()

    fun onNegativeClick()
}