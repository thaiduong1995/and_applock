package com.cem.admodule.ads.adx

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemOpenAd
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.inter.OpenLoadCallback
import com.cem.admodule.manager.CemAdManager
import com.cem.admodule.manager.CemAppOpenManager
import com.cem.admodule.manager.ConfigManager
import com.cem.firebase_module.config.RemoteConfigImpl
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AdxAppOpenAdManager private constructor(private val application: Application) : CemOpenAd,
    ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
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

        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(p0: AppOpenAd) {
                Log.d(TAG, "onAdLoaded adx: app open")
                appOpenAd = p0
                loadTime = Date().time
                adCallback?.onAdLoaded(this@AdxAppOpenAdManager)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d(TAG, "onAdFailedToLoad adx: ${p0.message}")
                adCallback?.onAdFailedToLoaded(NullPointerException("Load open ad ${p0.message}"))
            }
        }
        AppOpenAd.load(
            application,
            adUnitId,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback!!
        )
    }

    private val adRequest: AdRequest
        get() = AdManagerAdRequest.Builder().build()

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
            Log.d(TAG, "showAdIfAvailable adx: not time show")
            fullScreenContentCallback?.onAdFailedToShowCallback("not time show")
            return
        }


        if (!isShowingAd && isAdAvailable()) {
            val callback: FullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    fullScreenContentCallback?.onDismissCallback(AdNetwork.ADMOB)
                    lastShowAdsTime = System.currentTimeMillis()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    fullScreenContentCallback?.onAdFailedToShowCallback(p0.message)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    isShowingAd = true
                    lastShowAdsTime = System.currentTimeMillis()
                    fullScreenContentCallback?.onAdShowedCallback(AdNetwork.ADMOB)
                }
            }
            appOpenAd?.fullScreenContentCallback = callback
            Handler(Looper.getMainLooper()).postDelayed({
                appOpenAd?.show(currentActivity ?: return@postDelayed)
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
        var INSTANCE: AdxAppOpenAdManager? = null
        private var isShowingAd = false
        fun getInstance(application: Application): AdxAppOpenAdManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AdxAppOpenAdManager(application)
                INSTANCE = instance
                instance
            }
        }
    }


}