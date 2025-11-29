package com.cem.firebase_module

import android.app.Application
import android.content.Context
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.cem.firebase_module.qonversion.QOnVersionConfig
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance

object AnalyticsConfig { 
    @JvmStatic
    fun initializeFlurry(application: Application, flurryKey : String){
        FlurryAgent.Builder()
            .withDataSaleOptOut(false)
            .withCaptureUncaughtExceptions(true)
            .withIncludeBackgroundSessionsInMetrics(true)
            .withLogLevel(Log.VERBOSE)
            .withPerformanceMetrics(FlurryPerformance.ALL)
            .build(application,flurryKey)
    }

    @JvmStatic
    fun initQonVersion(context : Context, keySdk : String) {
        QOnVersionConfig.initQonVersion(context,keySdk)
    }

    @JvmStatic
    fun initAppsflyer(context: Context, keySdk: String){
        AppsFlyerLib.getInstance().init(keySdk, null, context)
        AppsFlyerLib.getInstance().start(context, keySdk, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d("Appsflyer", "Launch sent successfully")
            }

            override fun onError(errorCode: Int, errorDesc: String) {
                Log.e("Appsflyer", "Launch failed to be sent:\n" +
                        "Error code: " + errorCode + "\n"
                        + "Error description: " + errorDesc)
            }

        })
    }
}