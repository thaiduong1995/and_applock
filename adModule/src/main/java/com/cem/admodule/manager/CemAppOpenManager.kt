package com.cem.admodule.manager

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.cem.admodule.ads.admob.AdmobAppOpenAdManager
import com.cem.admodule.ads.adx.AdxAppOpenAdManager
import com.cem.admodule.ads.applovin.ApplovinAppOpenAdManager
import com.cem.admodule.data.AdUnitItem
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.getAdCollection
import com.cem.admodule.ext.getAdUnit
import com.cem.admodule.inter.CemOpenAd
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.inter.OpenLoadCallback

class CemAppOpenManager private constructor(app: Application) : DefaultLifecycleObserver {

    private val adsCemOpenManager: MutableMap<String, CemOpenAd?> = HashMap()
    private var configKey: String? = null
    private val configManager: ConfigManager by lazy {
        ConfigManager.getInstance(app)
    }

    private val admobAppOpenAdManager: AdmobAppOpenAdManager by lazy {
        AdmobAppOpenAdManager.getInstance(app)
    }

    private val applovinAppOpenAdManager: ApplovinAppOpenAdManager by lazy {
        ApplovinAppOpenAdManager.getInstance(app)
    }

    private val adxAppOpenManager: AdxAppOpenAdManager by lazy {
        AdxAppOpenAdManager.getInstance(app)
    }

    fun isOpenAdsLoaded(configKey: String): Boolean {
        return admobAppOpenAdManager.isOpenAdsLoaded() || applovinAppOpenAdManager.isOpenAdsLoaded() || adxAppOpenManager.isOpenAdsLoaded()
    }

    fun registerProcessLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun unregisterProcessLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun enableOpenAds() {
        admobAppOpenAdManager.enableOpenAds()
        applovinAppOpenAdManager.enableOpenAds()
        adxAppOpenManager.enableOpenAds()
    }

    fun blockOpenAds() {
        admobAppOpenAdManager.blockOpenAds()
        applovinAppOpenAdManager.blockOpenAds()
        adxAppOpenManager.blockOpenAds()
    }

    private fun createOpen(adUnitItem: AdUnitItem?): CemOpenAd? {
        if (adUnitItem == null) return null
        return when (AdNetwork.getNetwork(adUnitItem.adNetwork)) {
            AdNetwork.ADMOB -> admobAppOpenAdManager
            AdNetwork.ADX -> adxAppOpenManager
            AdNetwork.APPLOVIN -> applovinAppOpenAdManager
            else -> null
        }
    }

    fun fetchOpenAds(adConfigKey: String, callback: OpenLoadCallback? = null) {
        this.configKey = adConfigKey
        val adManagement = configManager.adManagement
        if (adManagement == null || !configManager.isEnable()) {
            callback?.onAdFailedToLoaded(NullPointerException("ads open disable"))
            return
        }
        val adsCollection = getAdCollection(adManagement, adConfigKey)
        if (adsCollection == null) {
            Log.d(TAG, "loadAds: addCollection data null")
            callback?.onAdFailedToLoaded(NullPointerException("list data null"))
            return
        }

        val units: MutableList<AdUnitItem> = ArrayList(adsCollection)
        if (units.isEmpty()) {
            Log.d(TAG, "loadAds: list units null")
            callback?.onAdFailedToLoaded(NullPointerException("list data null"))
            return
        }
        if (adsCemOpenManager[adConfigKey] != null) {
            Log.d(TAG, "loadAds: quarng cáo tồn tại")
            callback?.onAdFailedToLoaded(NullPointerException("quarng cáo tồn tại"))
            return
        }
        fetchAdsList(configKey = adConfigKey, unitsId = units, object : OpenLoadCallback {
            override fun onAdLoaded(cemOpenAd: CemOpenAd?) {
                callback?.onAdLoaded(cemOpenAd)
            }

            override fun onAdFailedToLoaded(error: Exception) {
                callback?.onAdFailedToLoaded(error)
            }
        })
    }

    private fun fetchAdsList(
        configKey: String, unitsId: MutableList<AdUnitItem>, adCallback: OpenLoadCallback? = null
    ) {
        val adUnit = getAdUnit(unitsId)

        if (adUnit == null) {
            adCallback?.onAdFailedToLoaded(NullPointerException("not config ad unit"))
            return
        }

        val adInterstitialAd = createOpen(adUnit)

        if (adInterstitialAd == null) {
            adCallback?.onAdFailedToLoaded(NullPointerException("not config ad unit"))
            return
        }
        if (adUnit.adUnit == null) {
            unitsId.remove(adUnit)
            fetchAdsList(configKey, unitsId, adCallback)
            return
        }
        adInterstitialAd.onLoaded(adUnit.adUnit, object : OpenLoadCallback {
            override fun onAdLoaded(cemOpenAd: CemOpenAd?) {
                adsCemOpenManager[configKey] = cemOpenAd
                adCallback?.onAdLoaded(cemOpenAd)
            }

            override fun onAdFailedToLoaded(error: Exception) {
                unitsId.remove(adUnit)
                fetchAdsList(configKey, unitsId, adCallback)
            }
        })
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d(TAG, "onStart: ${owner.lifecycle.currentState}")
        showAdIfAvailable()
    }

    private fun showAdIfAvailable() {
        val cemOpenAd = adsCemOpenManager[configKey]
        if (cemOpenAd == null) {
            configKey?.let { fetchOpenAds(it) }
            return
        }
        cemOpenAd.onShowed(callback = object : InterstitialShowCallback {
            override fun onAdFailedToShowCallback(error: String) {
                configKey?.let {
                    adsCemOpenManager.remove(it)
                    fetchOpenAds(it)
                }
            }

            override fun onAdShowedCallback(network: AdNetwork) {
            }

            override fun onDismissCallback(network: AdNetwork) {
                configKey?.let {
                    adsCemOpenManager.remove(it)
                    fetchOpenAds(it)
                }
            }

            override fun onAdClicked() {
            }
        })
    }


    private fun isEnable(unitsId: List<AdUnitItem>): Boolean {
        return unitsId.firstOrNull()?.enable ?: true
    }

    fun registerCallback(fullScreenContentCallback: InterstitialShowCallback?) {
        admobAppOpenAdManager.registerCallback(fullScreenContentCallback)
        applovinAppOpenAdManager.registerCallback(fullScreenContentCallback)
        adxAppOpenManager.registerCallback(fullScreenContentCallback)
    }

    fun unregisterCallback() {
        admobAppOpenAdManager.unregisterCallback()
        applovinAppOpenAdManager.unregisterCallback()
        adxAppOpenManager.unregisterCallback()
    }

    fun setIgnoreActivities(data: List<String>) {
        admobAppOpenAdManager.setIgnoreActivities(data)
        applovinAppOpenAdManager.setIgnoreActivities(data)
        adxAppOpenManager.setIgnoreActivities(data)
    }

    fun setIgnoreDelayActivities(data: List<String>) {
        admobAppOpenAdManager.setIgnoreDelayActivities(data)
        applovinAppOpenAdManager.setIgnoreDelayActivities(data)
        adxAppOpenManager.setIgnoreDelayActivities(data)
    }

    companion object {
        val  TAG = CemAppOpenManager::class.java.simpleName
        private var _instance: CemAppOpenManager? = null
        val instance: CemAppOpenManager get() = _instance!!
        fun getInstance(app: Application): CemAppOpenManager {
            return _instance ?: synchronized(this) {
                AdmobAppOpenAdManager.getInstance(app)
                ApplovinAppOpenAdManager.getInstance(app)
                AdxAppOpenAdManager.getInstance(app)
                _instance = CemAppOpenManager(app)
                return instance
            }
        }
    }
}