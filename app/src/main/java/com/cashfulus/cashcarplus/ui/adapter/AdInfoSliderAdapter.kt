package com.cashfulus.cashcarplus.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.model.ImageUrl
import com.smarteist.autoimageslider.SliderViewAdapter


class AdInfoSliderAdapter(private val sliderItems: List<ImageUrl>): SliderViewAdapter<AdInfoSliderAdapter.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH? {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.row_ad_info_image_slider, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        Glide.with(viewHolder.itemView)
            .load(sliderItems[position].image)
            .fitCenter()
            .into(viewHolder.imageViewBackground) //.into<Target<Drawable>>(viewHolder.imageViewBackground)
    }

    override fun getCount(): Int {
        return sliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.rowAdInfoImageSlider)
    }
}