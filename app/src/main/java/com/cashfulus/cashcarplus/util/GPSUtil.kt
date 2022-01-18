package com.cashfulus.cashcarplus.util

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GPSUtil(activity: Activity) {

}

/*class GPSUtil(private val context: Context) : Service(), LocationListener {
    val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f
    val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    var locationManager: LocationManager? = null

    var location: Location? = null
    var latitude: Double? = null
    var longitude: Double? = null

    init {
        getLocation()
    }

    @JvmName("getLocation1")
    fun getLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Cashcarplus GPS", "permission denied")
            return null
        }

        try {
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(isGPSEnabled) {
                Log.d("Cashcarplus GPS", "isGPSEnabled")
                if(locationManager != null) {
                    Log.d("Cashcarplus GPS", "locationManager not null")
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this)
                    location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    if(location != null) {
                        Log.d("Cashcarplus GPS", "location not null")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                    }
                }
            }
            if(isNetworkEnabled) {
                Log.d("Cashcarplus GPS", "isNetworkEnabled")
                if(location == null && locationManager != null) {
                    Log.d("Cashcarplus GPS", "locationManager not null")
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                    location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if(location != null) {
                        Log.d("Cashcarplus GPS", "location not null")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Cashcarplus GPS", "error : "+e.localizedMessage)
            e.printStackTrace()
        }

        return location
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun stopUsingGPS() {
        if(locationManager != null)
            locationManager!!.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {}
}*/