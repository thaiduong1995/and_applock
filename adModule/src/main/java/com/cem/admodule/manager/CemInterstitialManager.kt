package com.cem.admodule.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.cem.admodule.ads.admob.AdmobInterstitialAdManager
import com.cem.admodule.ads.adx.AdxInterstitialAdManager
import com.cem.admodule.ads.applovin.ApplovinInterstitialAdManager
import com.cem.admodule.ads.mintegral.MintegralInterstitialAdManager
import com.cem.admodule.data.AdUnitItem
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.getAdCollection
import com.cem.admodule.ext.getAdUnit
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.firebase_module.analytics.CemAnalytics
import com.google.gson.Gson
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class CemInterstitialManager @Inject constructor(
    private val activity: Context
) {
    private val configManager: ConfigManager by lazy {
        ConfigManager.getInstance(activity)
    }

    private val adsInterstitialManager: MutableMap<String, CemInterstitialAd?> = HashMap()

    private var lastShowAdsTime: Long = System.currentTimeMillis()

    var isShowingAd: Boolean = false
        private set

    private fun createInterstitial(adUnitItem: AdUnitItem?): CemInterstitialAd? {
        if (adUnitItem == null) return null
        return when (AdNetwork.getNetwork(adUnitItem.adNetwork)) {
            AdNetwork.ADMOB -> AdmobInterstitialAdManager.newInstance(adUnitItem.adUnit)

            AdNetwork.ADX -> AdxInterstitialAdManager.newInstance(
                adUnitItem.adUnit
            )

            AdNetwork.APPLOVIN -> ApplovinInterstitialAdManager.newInstance(adUnitItem.adUnit)

            AdNetwork.MINTEGRAL -> MintegralInterstitialAdManager.newInstance(
                adUnit = adUnitItem.adUnit,
                placementId = adUnitItem.placementId
            )

            else -> null
        }
    }

    private fun loadAdsAsync(
        activity: Activity,
        configKey: String,
        unitsId: MutableList<AdUnitItem>,
        onAdListener: InterstitialLoadCallback?
    ) {
        val adUnit = getAdUnit(unitsId)

        if (adUnit == null) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_adUnit_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "not config ad unit"))
            return
        }

        val adInterstitialAd = createInterstitial(adUnit)
        if (adInterstitialAd == null) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_network_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode("not ad network"))
            return
        }

        adInterstitialAd.load(activity, object : InterstitialLoadCallback {
            override fun onAdLoaded(cemInterstitialAd: CemInterstitialAd?) {
                Log.d(TAG, "onAdLoaded $configKey: ${Gson().toJson(adUnit)}")
                onAdListener?.onAdLoaded(cemInterstitialAd)
            }

            override fun onAdFailedToLoaded(error: ErrorCode) {
                Log.d(TAG, "onAdFailedToLoaded: $configKey : ${error.message}")
                val lastKey = adUnit.adUnit?.takeLast(4)
                val hashMap = hashMapCountry(configKey)
                val message = error.message.take(22).replace(" ", "_")
                hashMap["unit_error"] = "key_${lastKey}_code${error.code}_$message"
                CemAnalytics.logEventAndParams(
                    context = activity,
                    eventName = "${ADS_FULL_FAILED}_detail",
                    params = hashMap
                )
                unitsId.remove(adUnit)
                loadAdsAsync(activity, configKey, unitsId, onAdListener)
            }
        })
    }

    fun loadAds(
        activity: Activity,
        configKey: String,
        onAdListener: InterstitialLoadCallback? = null
    ) {
        val adManager = configManager.adManagement
        if (adManager == null) {
            Log.d(TAG, "loading ad manager null")
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_config_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "loading null"))
            return
        }

        if (!configManager.isEnable()) {
            Log.d(TAG, "loading isEnable false")
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "loading isEnable false"))
            return
        }

        val addCollection = getAdCollection(adManager, configKey)
        if (addCollection == null) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_config_key_null",
                params = hashMapCountry(configKey)
            )
            Log.d(TAG, "loadAds: addCollection data null")
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "list data null"))
            return
        }

        val units: MutableList<AdUnitItem> = ArrayList(addCollection)
        if (units.isEmpty()) {
            Log.d(TAG, "loadAds: list units null")
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "list data null"))
            return
        }

        if (adsInterstitialManager.containsKey(configKey)) {
            Log.d(TAG, "loadAds: ads exits")
            onAdListener?.onAdLoaded(adsInterstitialManager[configKey])
            return
        }

        loadAdsAsync(
            activity,
            configKey = configKey,
            unitsId = units,
            object : InterstitialLoadCallback {
                override fun onAdLoaded(cemInterstitialAd: CemInterstitialAd?) {
                    adsInterstitialManager[configKey] = cemInterstitialAd
                    onAdListener?.onAdLoaded(cemInterstitialAd)
                }

                override fun onAdFailedToLoaded(error: ErrorCode) {
                    Log.d(TAG, "onAdFailedToLoaded: ${error.message}")
                    CemAnalytics.logEventAndParams(
                        context = activity,
                        eventName = "${ADS_FULL_FAILED}_onAdFailedToLoaded",
                        params = hashMapCountry(configKey)
                    )
                    onAdListener?.onAdFailedToLoaded(error)
                }
            })
    }

    fun loadAds(
        activity: Activity,
        configKey: String,
        onAdListener: InterstitialLoadCallback? = null,
        onTimeout: (() -> Unit)? = null
    ) {
        val adManager = configManager.adManagement

        if (adManager == null) {
            Log.d(TAG, "loading ad manager null")
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_config_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "loading null"))
            return
        }

        if (!configManager.isEnable()) {
            Log.d(TAG, "loading isEnable false")
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "loading isEnable false"))
            return
        }

        val addCollection = getAdCollection(adManager, configKey)
        if (addCollection == null) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_config_key_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "list data null"))
            return
        }

        val units: MutableList<AdUnitItem> = ArrayList(addCollection)
        if (units.isEmpty()) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_FULL_FAILED}_list_data_null",
                params = hashMapCountry(configKey)
            )
            onAdListener?.onAdFailedToLoaded(ErrorCode(message = "list data null"))
            return
        }

        if (adsInterstitialManager.containsKey(configKey)) {
            Log.d(TAG, "loadAds: ads exits")
            onAdListener?.onAdLoaded(adsInterstitialManager[configKey])
            return
        }

        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                CemAnalytics.logEventAndParams(
                    context = activity,
                    eventName = "${ADS_FULL_FAILED}_timeout",
                    params = hashMapCountry(configKey)
                )
                println("Đã vào đây rồi: ${adManager.timeLoadSplashInterval}")
                onTimeout?.invoke()
                timer.cancel()
            }
        }
        timer.schedule(timerTask, adManager.timeLoadSplashInterval)

        loadAdsAsync(
            activity,
            configKey = configKey,
            unitsId = units,
            object : InterstitialLoadCallback {
                override fun onAdLoaded(cemInterstitialAd: CemInterstitialAd?) {
                    adsInterstitialManager[configKey] = cemInterstitialAd
                    onAdListener?.onAdLoaded(cemInterstitialAd)
                    timer.cancel()
                }

                override fun onAdFailedToLoaded(error: ErrorCode) {
                    Log.d(TAG, "onAdFailedToLoaded: ${error.message}")
                    CemAnalytics.logEventAndParams(
                        context = activity,
                        eventName = "${ADS_FULL_FAILED}_onAdFailedToLoaded",
                        params = hashMapCountry(configKey)
                    )
                    onAdListener?.onAdFailedToLoaded(error)
                    timer.cancel()
                }
            })
    }

    private fun loadReload(
        activity: Activity, configKey: String, callback: (() -> Unit)? = null
    ) {
        loadAds(activity, configKey, object : InterstitialLoadCallback {
            override fun onAdLoaded(cemInterstitialAd: CemInterstitialAd?) {
                callback?.invoke()
            }

            override fun onAdFailedToLoaded(error: ErrorCode) {
                callback?.invoke()
            }
        })
    }

    private fun showAds(
        activity: Activity,
        configKey: String,
        reload: Boolean,
        nameScreen: String? = configKey,
        callback: InterstitialShowCallback? = null
    ) {
        if (!configManager.isEnable()) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_SHOW_FAILED}_user_vip",
                params = hashMapCountry(configKey)
            )
            Log.d(TAG, "showAds enable: ${configManager.isEnable()}")
            callback?.onDismissCallback(AdNetwork.ADMOB)
            return
        }

        val adManager = configManager.adManagement
        val interval = adManager?.adInterval ?: (30 * 1000L)
        val currentTime = System.currentTimeMillis()

        val isNeedShow = if (isShowDirectFull(configKey)) {
            true
        } else {
            currentTime - lastShowAdsTime >= interval
        }

        if (!isNeedShow) {
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_SHOW_FAILED}_time_show_false",
                params = hashMapCountry(configKey)
            )
            Log.d(TAG, "not time show")
            callback?.onAdFailedToShowCallback("not time show")
            return
        }

        val cemInterstitialAd = adsInterstitialManager[configKey]
        if (cemInterstitialAd == null) {
            loadAds(activity, configKey)
            CemAnalytics.logEventAndParams(
                context = activity,
                eventName = "${ADS_SHOW_FAILED}_ads_null",
                params = hashMapCountry(configKey)
            )
            Log.d(TAG, "showAds: cemInterstitialAd null")
            callback?.onDismissCallback(AdNetwork.ADMOB)
            return
        }

        cemInterstitialAd.show(activity, object : InterstitialShowCallback {
            override fun onAdFailedToShowCallback(error: String) {
                val hashMap = hashMapCountry(configKey)
                hashMap["error_code"] = error
                CemAnalytics.logEventAndParams(
                    context = activity,
                    eventName = "${ADS_SHOW_FAILED}_failed_show",
                    params = hashMap
                )
                callback?.onAdFailedToShowCallback(error)
                isShowingAd = false
            }

            override fun onAdShowedCallback(network: AdNetwork) {
                callback?.onAdShowedCallback(network)
                lastShowAdsTime = System.currentTimeMillis()
                isShowingAd = true
                adsInterstitialManager.remove(configKey)
                if (reload) loadReload(activity, configKey)
                CemAnalytics.logEventAndParams(activity, "$ADS_FULL${nameScreen ?: configKey}")
            }

            override fun onDismissCallback(network: AdNetwork) {
                callback?.onDismissCallback(network)
                isShowingAd = false
                lastShowAdsTime = System.currentTimeMillis()
                CemAnalytics.logEventAndParams(
                    activity,
                    "$ADS_FULL${nameScreen ?: configKey}_dismiss"
                )

            }

            override fun onAdClicked() {
                CemAnalytics.logEventAndParams(
                    activity,
                    "$CLICK_ADS_FULL${nameScreen ?: configKey}"
                )
            }
        })

    }

    fun showAdsAndCallback(
        activity: Activity,
        configKey: String,
        nameScreen: String? = null,
        callback: (() -> Unit)? = null
    ) {
        showAds(
            activity = activity,
            configKey = configKey,
            reload = false,
            nameScreen = nameScreen,
            callback = object : InterstitialShowCallback {
                override fun onAdFailedToShowCallback(error: String) {
                    callback?.invoke()
                }

                override fun onAdShowedCallback(network: AdNetwork) {
                }

                override fun onDismissCallback(network: AdNetwork) {
                    callback?.invoke()
                }

                override fun onAdClicked() {

                }
            })
    }

    fun showAdsAndCallbackReload(
        activity: Activity,
        configKey: String,
        nameScreen: String? = null,
        callback: (() -> Unit)? = null
    ) {
        showAds(
            activity = activity,
            configKey = configKey,
            reload = true,
            nameScreen = nameScreen,
            callback = object : InterstitialShowCallback {
                override fun onAdFailedToShowCallback(error: String) {
                    callback?.invoke()
                }

                override fun onAdShowedCallback(network: AdNetwork) {
                }

                override fun onDismissCallback(network: AdNetwork) {
                    callback?.invoke()
                }

                override fun onAdClicked() {
                    CemAnalytics.logEventAndParams(
                        activity,
                        "$CLICK_ADS_FULL${nameScreen ?: configKey}"
                    )
                }
            })
    }

    fun showAdsAsync(
        activity: Activity,
        configKey: String,
        nameScreen: String? = null,
        callback: InterstitialShowCallback? = null
    ) {
        showAds(
            activity = activity,
            configKey = configKey,
            reload = false,
            nameScreen = nameScreen,
            callback = callback
        )
    }

    fun showAdsAndReloadAsync(
        activity: Activity,
        configKey: String,
        nameScreen: String? = null,
        callback: InterstitialShowCallback? = null
    ) {
        showAds(
            activity = activity,
            configKey = configKey,
            reload = true,
            nameScreen = nameScreen,
            callback = callback
        )
    }


    private fun isShowDirectFull(configKey: String): Boolean {
        return ConfigManager.getInstance(activity).adManagement?.adUnitList?.get(configKey)
            ?.firstOrNull()?.showDirect ?: false
    }

    private fun isEnableFull(configKey: String): Boolean {
        return ConfigManager.getInstance(activity).adManagement?.adUnitList?.get(configKey)
            ?.firstOrNull()?.enable ?: true
    }

    private fun hashMapCountry(
        configKey: String
    ): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["country"] = configManager.getCountry() ?: ""
        hashMap["config_key"] = configKey
        return hashMap
    }

    companion object {
        val TAG: String = CemInterstitialManager::class.java.simpleName
        const val ADS_FULL = "ads_full_"
        const val CLICK_ADS_FULL = "click_full_"
        const val ADS_FULL_FAILED = "ads_full_load_failed_"
        const val ADS_SHOW_FAILED = "ads_full_show_failed_"

        @SuppressLint("StaticFieldLeak")
        private var mInstance: CemInterstitialManager? = null

        fun getInstance(activity: Context): CemInterstitialManager {
            return mInstance ?: synchronized(this) {
                val instance = CemInterstitialManager(activity)
                mInstance = instance
                instance
            }
        }
    }
}