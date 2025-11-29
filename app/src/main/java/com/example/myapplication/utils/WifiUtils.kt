package com.example.myapplication.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.myapplication.data.model.ItemWifi

val Context.currentWifi: ItemWifi?
    get() {
        val wifiManager = getSystemService(WifiManager::class.java)
        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo.bssid.isNullOrEmpty() || wifiInfo.ssid.isNullOrEmpty())
            return null
        Log.d("thinhvh", ": currentWifi ${wifiInfo.bssid}")
        return ItemWifi(wifiInfo.bssid, wifiInfo.ssid, System.currentTimeMillis())
    }

fun Context.scanWifi(callback: (List<ItemWifi>) -> Unit) {
    registerReceiver(object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val wifis = mutableListOf<ItemWifi>()
            if (intent?.action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                context?.run {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        val wifiManager = getSystemService(WifiManager::class.java)
                        val groupId = System.currentTimeMillis()
                        wifis.addAll(wifiManager.scanResults.map {
                            ItemWifi(it.BSSID, it.SSID, groupId)
                        })
                    }
                }
            }
            callback(wifis)
        }
    }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

    getSystemService(WifiManager::class.java).startScan()
}

val Context.isGPSEnabled: Boolean
    get() = getSystemService(LocationManager::class.java).isProviderEnabled(LocationManager.GPS_PROVIDER)
