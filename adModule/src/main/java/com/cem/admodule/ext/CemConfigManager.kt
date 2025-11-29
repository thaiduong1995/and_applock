package com.cem.admodule.ext

import android.app.Application
import android.content.Context
import com.cem.admodule.data.Configuration
import com.cem.firebase_module.AnalyticsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CemConfigManager {
    @JvmStatic
    suspend fun initializeAds(
        app: Application,
        configuration: Configuration,
        callbackCountry: ((country: String) -> Unit)? = null
    ) {
        AppLovinConfig.initialize(app, callback = {
            callbackCountry?.invoke(it)
        })
        withContext(Dispatchers.Main) {
            MintegralConfig.initialize(
                app = app,
                configuration = configuration
            )
        }
    }

    @JvmStatic
    fun initQonVersion(context: Context, keySdk: String) {
        AnalyticsConfig.initQonVersion(context, keySdk)
    }

    @JvmStatic
    fun initFlurry(application: Application, flurryKey: String) {
        AnalyticsConfig.initializeFlurry(application, flurryKey)
    }

    @JvmStatic
    fun initAppsflyer(application: Application, flurryKey: String) {
        AnalyticsConfig.initAppsflyer(application, flurryKey)
    }
}