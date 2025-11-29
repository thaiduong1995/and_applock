package com.cem.admodule.ext

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AdmobConfig {
    private fun getProcessName(context: Context?): String? {
        if (context == null) return null
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            manager.runningAppProcesses?.forEach { processInfo ->
                if (processInfo.pid == android.os.Process.myPid()) {
                    return processInfo.processName
                }
            }
        } catch (ex: java.lang.Exception) {
            return null
        }
        return null
    }

    private fun fixBugCrashAndroid9(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName(context) ?: return
            if (context.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    @JvmStatic
    suspend fun initialize(
        application: Context,
        testDeviceIds: List<String> = emptyList(),
        callback: (() -> Unit)? = null
    ) = suspendCancellableCoroutine { const ->
        fixBugCrashAndroid9(application)
        MobileAds.initialize(application) {
            val statusMap: Map<String, AdapterStatus> = it.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d(
                    "TAG::", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass,
                        status!!.description,
                        status.latency
                    )
                )
            }
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            )
            callback?.invoke()
            const.resume(it)
        }
    }
}