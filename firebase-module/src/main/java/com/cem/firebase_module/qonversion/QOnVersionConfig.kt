package com.cem.firebase_module.qonversion

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionConfig
import com.qonversion.android.sdk.dto.QLaunchMode
import com.qonversion.android.sdk.dto.properties.QUserPropertyKey

object QOnVersionConfig {

    @JvmStatic
    fun initQonVersion(context : Context, keySdk : String) {
        val qOnVersionConfig = QonversionConfig.Builder(
            context,
            keySdk,
            QLaunchMode.Analytics
        ).build()
        Qonversion.initialize(qOnVersionConfig)
        Qonversion.shared.syncHistoricalData()

        FirebaseAnalytics.getInstance(context).appInstanceId.addOnCompleteListener { task ->
            task.result?.let { appInstanceId ->
                Log.d("QOnVersionConfig", "initQonVersion: $appInstanceId")
                Qonversion.shared.setUserProperty(
                    QUserPropertyKey.FirebaseAppInstanceId,
                    appInstanceId
                )
            }
        }
    }

    fun onVersionPurchase(){
        Qonversion.shared.syncPurchases()
    }
}