package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.BannerData

class SplashBannerAdapter(private val context: Context, private val banners : ArrayList<BannerData>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.banner_item, container, false)

        val imageView = view.findViewById<ImageView>(R.id.ivBanner)
        Glide.with(context).load(banners[position].imageUrl).into(imageView)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return banners.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}