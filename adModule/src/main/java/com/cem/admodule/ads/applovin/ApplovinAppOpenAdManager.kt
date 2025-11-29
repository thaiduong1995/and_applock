package com.cem.admodule.ads.applovin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemOpenAd
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.inter.OpenLoadCallback
import com.cem.admodule.manager.CemAdManager
import com.cem.admodule.manager.CemAppOpenManager
import com.cem.admodule.manager.ConfigManager
import com.cem.firebase_module.config.RemoteConfigImpl
import java.util.Date

class ApplovinAppOpenAdManager private constructor(private val application: Application) :
    CemOpenAd, ActivityLifecycleCallbacks {

    private var appOpenAd: MaxAppOpenAd? = null
    private var loadCallback: MaxAdListener? = null
    private var loadTime: Long = 0
    private var adConfigKey: String? = null
    private var blockAds: Boolean = false
    private var currentActivity: Activity? = null

    private var fullScreenContentCallback: InterstitialShowCallback? = null

    private var lastShowAdsTime: Long = 0

    private var listIgnoreActivities = listOf<String>()
    private var listIgnoreDelayActivities = listOf<String>()


    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun registerCallback(interstitialShowCallback: InterstitialShowCallback?) {
        this.fullScreenContentCallback = interstitialShowCallback
    }

    fun unregisterCallback() {
        this.fullScreenContentCallback = null
    }

    fun enableOpenAds() {
        blockAds = false
    }

    fun blockOpenAds() {
        blockAds = true
    }

    private fun fetchAd(adUnitId: String?, adCallback: OpenLoadCallback? = null) {
        if (adUnitId == null) {
            adCallback?.onAdFailedToLoaded(NullPointerException("Unit id null or empty"))
            return
        }

        Log.d(TAG, "fetchAd: ${isAdAvailable()}")
        if (isAdAvailable()) {
            appOpenAd?.let {
                adCallback?.onAdLoaded(this)
            }
            return
        }
        appOpenAd = MaxAppOpenAd(adUnitId, application)
        loadCallback = object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                appOpenAd?.let {
                    adCallback?.onAdLoaded(this@ApplovinAppOpenAdManager)
                }
                loadTime = Date().time
                Log.d(TAG, "onAdLoaded: ${p0.adUnitId}")
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                Log.d(TAG, "onAdLoadFailed applovin: ${p1.message}")
                adCallback?.onAdFailedToLoaded(NullPointerException(p1.message))
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
            }
        }
        appOpenAd?.setListener(loadCallback)
        appOpenAd?.loadAd()
    }

    private fun showAdIfAvailable() {
        val adManager = ConfigManager.getInstance(application).adManagement
        val interval = adManager?.openInterval ?: (10 * 1000L)
        val intervalDelayActivity =
            RemoteConfigImpl().getLong("delay_time_open_activity") ?: (60 * 1000L)
        val currentTime = System.currentTimeMillis()

        if (CemAdManager.getInstance(application).isVip()) {
            fullScreenContentCallback?.onAdFailedToShowCallback("user vip")
            return
        }

        if (isIgnoreActivity(currentActivity)) {
            return
        }

        val isNeedShow = if (isShowDirectFull(adConfigKey)) {
            true
        } else if (isIgnoreDelayActivity(currentActivity)) {
            currentTime - lastShowAdsTime >= intervalDelayActivity
        } else {
            currentTime - lastShowAdsTime >= interval
        }

        if (!isNeedShow) {
            Log.d(TAG, "showAdIfAvailable: not time show")
            fullScreenContentCallback?.onAdFailedToShowCallback("not time show")
            return
        }

        if (!isShowingAd && isAdAvailable()) {
            val callback = object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    fullScreenContentCallback?.onAdShowedCallback(AdNetwork.APPLOVIN)
                }

                override fun onAdHidden(p0: MaxAd) {
                    appOpenAd = null
                    fullScreenContentCallback?.onDismissCallback(AdNetwork.APPLOVIN)

                }

                override fun onAdClicked(p0: MaxAd) {
                    fullScreenContentCallback?.onAdClicked()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    fullScreenContentCallback?.onAdFailedToShowCallback(p1.message)
                }
            }
            appOpenAd?.setListener(callback)
            Handler(Looper.getMainLooper()).postDelayed({
                if (appOpenAd?.isReady == true) {
                    appOpenAd?.showAd()
                } else {
                    appOpenAd = null
                    fullScreenContentCallback?.onDismissCallback(AdNetwork.APPLOVIN)
                }
            }, 100)
        }
    }

    fun isOpenAdsLoaded(): Boolean {
        return isAdAvailable()
    }

    private fun isAdAvailable(): Boolean {
        var isShowingInterstitial: Boolean
        var isShowingReward: Boolean
        try {
            isShowingInterstitial = CemAdManager.getInstance(application).isShowingInterstitialAd
            isShowingReward = CemAdManager.getInstance(application).isShowingInterstitialAd
        } catch (e: Exception) {
            e.printStackTrace()
            isShowingReward = false
            isShowingInterstitial = false
        }
        val isAdEnable = ConfigManager.getInstance(application).isEnable()
        return appOpenAd != null && isAdEnable && wasLoadTimeLessThanNHoursAgo(4) && !isShowingInterstitial && !isShowingReward && !blockAds
    }

    private fun isShowDirectFull(configKey: String?): Boolean {
        if (configKey == null) return false
        return ConfigManager.getInstance(application).adManagement?.adUnitList?.getOrElse(configKey) { null }
            ?.firstOrNull()?.showDirect ?: false
    }

    fun setIgnoreActivities(data: List<String>) {
        listIgnoreActivities = data
    }

    private fun isIgnoreActivity(activity: Activity?): Boolean {
        if (activity == null) return false
        return listIgnoreActivities.find { activity::class.java.simpleName == it } != null
    }

    fun setIgnoreDelayActivities(data: List<String>) {
        listIgnoreDelayActivities = data
    }

    private fun isIgnoreDelayActivity(activity: Activity?): Boolean {
        if (activity == null) return false
        return listIgnoreDelayActivities.find { activity::class.java.simpleName == it } != null
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    override fun onLoaded(adUnit: String, callback: OpenLoadCallback): CemOpenAd {
        fetchAd(adUnit, object : OpenLoadCallback {
            override fun onAdLoaded(cemOpenAd: CemOpenAd?) {
                callback.onAdLoaded(cemOpenAd)
            }

            override fun onAdFailedToLoaded(error: Exception) {
                callback.onAdFailedToLoaded(error)
            }
        })
        return this
    }

    override var isLoaded: Boolean = false
    override fun onShowed(callback: InterstitialShowCallback?) {
        fullScreenContentCallback = callback
        showAdIfAvailable()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    companion object {
        val TAG: String = CemAppOpenManager.TAG

        @SuppressLint("StaticFieldLeak")
        var INSTANCE: ApplovinAppOpenAdManager? = null
        private var isShowingAd = false
        fun getInstance(application: Application): ApplovinAppOpenAdManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ApplovinAppOpenAdManager(application)
                INSTANCE = instance
                instance
            }
        }
    }

}