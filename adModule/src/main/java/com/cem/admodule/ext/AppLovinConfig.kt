package com.cem.admodule.ext

import android.app.Application
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.cem.admodule.manager.ConfigManager
import java.util.Locale

object AppLovinConfig {
    @JvmStatic
    fun initialize(
        app : Application,
        callback : ((country : String)-> Unit)? = null
    ){
        AppLovinPrivacySettings.setHasUserConsent(true, app)
        AppLovinPrivacySettings.setIsAgeRestrictedUser(true, app)
        AppLovinPrivacySettings.setDoNotSell(true, app)
        //add sdk applovin
        AppLovinSdk.getInstance(app).mediationProvider = AppLovinMediationProvider.ADMOB
        AppLovinSdk.getInstance(app).initializeSdk {
            Log.d("CemAnalytics", "initialize: ${it.countryCode}")
            callback?.invoke(it.countryCode.lowercase(Locale.US))
        }
    }
}