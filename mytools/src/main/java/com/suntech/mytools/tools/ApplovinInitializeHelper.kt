package com.suntech.mytools.tools

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration

class ApplovinInitializeHelper : AppLovinSdk.SdkInitializationListener {
    override fun onSdkInitialized(p0: AppLovinSdkConfiguration?) {
        Log.d("ApplovinInitializeHelper", "onSdkInitialized: ${p0?.enabledAmazonAdUnitIds}")
    }

    companion object {
        const val TAG = "ApplovinInitializeHelper::class.java"

        fun initialize(context: Context?) {
            AppLovinSdk.getInstance(context).mediationProvider = AppLovinMediationProvider.MAX
            AppLovinSdk.getInstance(context).mediationProvider = AppLovinMediationProvider.ADMOB
            AppLovinSdk.initializeSdk(context, ApplovinInitializeHelper())
            AppLovinSdk.getInstance(context).initializeSdk {
                Log.d("ApplovinInitializeHelper", "initialize: ${it.countryCode}")
            }
        }
    }
}