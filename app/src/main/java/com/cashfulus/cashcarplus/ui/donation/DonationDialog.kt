package com.cashfulus.cashcarplus.ui.donation

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.DonationListResponse
import com.cashfulus.cashcarplus.model.ImageUrl
import kotlinx.android.synthetic.main.dialog_donation.*

class DonationDialog(private val data: DonationListResponse): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_donation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        context?.let { Glide.with(it).load(data.logo).into(ivDonationDialogLogo) }
        tvDonationDialogName.text = data.name
        tvDonationDialogContents.text = Html.fromHtml(data.imageInformation[0].description)

        val adapter = DonationSliderAdapter()
        vpDonationDialog.adapter = adapter
        tlDonationDialog.setupWithViewPager(vpDonationDialog, true)

        // 최상단 슬라이드 자체 슬라이딩 시 아래 텍스트 변경
        vpDonationDialog.setOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                    tvDonationDialogContents.text = Html.fromHtml(data.imageInformation[position].description.replace("<p>", "").replace("</p>", ""))
                } else {
                    tvDonationDialogContents.text = Html.fromHtml(data.imageInformation[position].description.replace("<p>", "").replace("</p>", ""), Html.FROM_HTML_MODE_LEGACY)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        btnDonationDialogClose.setOnClickListener {
            dismiss()
        }
    }

    /*override fun onResume() {
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
    }*/

    inner class DonationSliderAdapter: PagerAdapter() {
        private var layoutInflater : LayoutInflater? = null

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return data.imageInformation.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = layoutInflater!!.inflate(R.layout.row_donation_slider, null)
            val image = v.findViewById<ImageView>(R.id.rowDonationSlider)

            Glide.with(context!!).load(data.imageInformation[position].image).into(image)
            val vp = container as ViewPager
            vp.addView(v, 0)

            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val vp = container as ViewPager
            val v = `object` as View
            vp.removeView(v)
        }
    }
}