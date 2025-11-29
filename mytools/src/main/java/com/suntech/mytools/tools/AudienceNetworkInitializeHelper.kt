package com.suntech.mytools.tools

import android.content.Context
import android.util.Log
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.BuildConfig

class AudienceNetworkInitializeHelper : AudienceNetworkAds.InitListener {
    override fun onInitialized(result: AudienceNetworkAds.InitResult) {
        Log.d(AudienceNetworkAds.TAG, result.message)
    }

    companion object {
        fun initialize(context: Context?) {
            if (!AudienceNetworkAds.isInitialized(context)) {
                if (BuildConfig.DEBUG) {
                    AdSettings.turnOnSDKDebugger(context)
                }
                AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(AudienceNetworkInitializeHelper())
                    .initialize()
            }
        }
    }
}