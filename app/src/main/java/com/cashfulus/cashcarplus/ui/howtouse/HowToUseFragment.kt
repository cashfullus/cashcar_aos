package com.cashfulus.cashcarplus.ui.howtouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.fragment_how_to_use.*

const val IMAGE_HOW_TO_USE = "image"
const val TITLE_HOW_TO_USE = "title"
const val TEXT_HOW_TO_USE = "text"

class HowToUseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_how_to_use, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(IMAGE_HOW_TO_USE) && it.containsKey(TEXT_HOW_TO_USE) }?.apply {
            ivHowToUse.setImageResource(getInt(IMAGE_HOW_TO_USE))
            tvHowToUseTitle.text = getString(TITLE_HOW_TO_USE)
            tvHowToUseContents.text = getString(TEXT_HOW_TO_USE)
        }

        /*skip_text_view.setOnClickListener {
            //Handle Skip Click event
        }*/
    }
}