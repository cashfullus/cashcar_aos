package com.cashfulus.cashcarplus.util

import java.text.SimpleDateFormat
import java.util.*

fun mappingDateLength(startDate: String, endDate: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return ((sdf.parse(endDate)!!.time - sdf.parse(startDate)!!.time) / (60 * 60 * 24 * 1000)).toInt()
}

fun mappingDatePercent(startDate: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return ((Calendar.getInstance().time.time - sdf.parse(startDate).time) / (60 * 60 * 24 * 1000)).toInt()
}