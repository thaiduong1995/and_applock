package com.cem.admodule.manager


import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.cem.admodule.ads.admob.AdmobBannerAdManager
import com.cem.admodule.ads.adx.AdxBannerAdManager
import com.cem.admodule.ads.applovin.ApplovinBannerAdManager
import com.cem.admodule.ads.mintegral.MintegralBannerAdManager
import com.cem.admodule.data.AdUnitItem
import com.cem.admodule.data.BannerLoaded
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.getAdCollection
import com.cem.admodule.ext.getAdUnit
import com.cem.admodule.ext.gone
import com.cem.admodule.ext.visible
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.firebase_module.analytics.CemAnalytics
import com.google.gson.Gson

class CemBannerManager private constructor() {

    private var adsBannerManager: MutableMap<String, BannerLoaded> = HashMap()
    private fun getOrPutHandler(configKey: String): BannerLoaded {
        val bannerLoaded: BannerLoaded = adsBannerManager.getOrPut(configKey) {
            BannerLoaded(timeLoaded = 0, isLoaded = true, isClosed = true)
        }
        return bannerLoaded
    }

    private fun isLoadedSuccess(configKey: String, timeInterval: Long): Boolean {
        val bannerLoaded = getOrPutHandler(configKey)
        return bannerLoaded.isLoaded && System.currentTimeMillis() - bannerLoaded.timeLoaded >= timeInterval && bannerLoaded.isClosed
    }

    fun removeBannerLoaded(configKey: String) {
        adsBannerManager.remove(configKey)
    }

    private fun createBanner(adUnitItem: AdUnitItem?): BannerAdView? {
        if (adUnitItem == null) return null
        return when (AdNetwork.getNetwork(adUnitItem.adNetwork)) {
            AdNetwork.ADMOB -> AdmobBannerAdManager.newInstance(
                AdUnitItem.getAdSize(adUnitItem.adSize), adUnitItem.adUnit
            )

            AdNetwork.APPLOVIN -> ApplovinBannerAdManager.newInstance(
                adUnitItem.adUnit
            )

            AdNetwork.ADX -> AdxBannerAdManager.newInstance(
                AdUnitItem.getAdSize(adUnitItem.adSize), adUnitItem.adUnit
            )

            AdNetwork.MINTEGRAL -> MintegralBannerAdManager.newInstance(
                adSize = AdUnitItem.getAdSize(adUnitItem.adSize),
                adUnit = adUnitItem.adUnit,
                placementId = adUnitItem.placementId
            )

            else -> null
        }
    }

    private fun loadBannerShowByActivity(
        activity: Activity,
        configKey: String,
        units: MutableList<AdUnitItem>,
        viewGroup: ViewGroup,
        nameScreen: String? = null,
        position: String? = null,
        callback: BannerAdListener? = null
    ) {
        val adUnit = getAdUnit(units)
        if (adUnit == null) {
            Log.d(TAG, "loadBannerShowByActivity: adUnit null")
            viewGroup.gone()
            callback?.onBannerFailed("No adUnit")
            return
        }

        val bannerAdView = createBanner(adUnit)
        if (bannerAdView == null) {
            Log.d(TAG, "loadBannerShowByActivity: bannerAdView null")
            viewGroup.gone()
            callback?.onBannerFailed("No bannerAdView")
            return
        }
        bannerAdView.createByActivity(activity = activity,
            position = if (adUnit.collapsible) position else null,
            listener = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {
                    Log.d(TAG, "onBannerLoaded $configKey: ${Gson().toJson(adUnit)}")
                    CemAnalytics.logEventAndParams(activity, "$ADS_FULL${nameScreen ?: configKey}")
                    callback?.onBannerLoaded(banner, view)
                    viewGroup.removeAllViews()
                    viewGroup.visible()
                    viewGroup.addView(view)
                }

                override fun onBannerFailed(error: String?) {
                    Log.d(TAG, "onBannerFailed $configKey: $error")
                    units.remove(adUnit)
                    loadBannerShowByActivity(
                        activity, configKey, units, viewGroup, nameScreen, position, callback
                    )
                }

                override fun onBannerClicked() {
                    callback?.onBannerClicked()
                    CemAnalytics.logEventAndParams(
                        activity, "$CLICK_ADS_FULL${nameScreen ?: configKey}"
                    )
                }

                override fun onBannerOpen() {
                    callback?.onBannerOpen()
                }

                override fun onBannerClose() {
                    callback?.onBannerClose()
                }
            })
    }

    private fun loadBannerShowByContext(
        context: Context,
        configKey: String,
        units: MutableList<AdUnitItem>,
        viewGroup: ViewGroup,
        nameScreen: String? = null,
        position: String? = null,
        callback: BannerAdListener? = null
    ) {
        val adUnit = getAdUnit(units)
        if (adUnit == null) {
            Log.d(TAG, "loadBannerShowByActivity: adUnit null")
            viewGroup.gone()
            callback?.onBannerFailed("No adUnit")
            return
        }

        val bannerAdView = createBanner(adUnit)
        if (bannerAdView == null) {
            Log.d(TAG, "loadBannerShowByActivity: bannerAdView null")
            viewGroup.gone()
            callback?.onBannerFailed("No bannerAdView")
            return
        }
        bannerAdView.createByContext(context,
            position = if (adUnit.collapsible) position else null,
            listener = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {
                    Log.d(TAG, "onBannerLoaded $configKey:: ${Gson().toJson(adUnit)}")
                    CemAnalytics.logEventAndParams(context, "$ADS_FULL${nameScreen ?: configKey}")
                    callback?.onBannerLoaded(banner, view)
                    viewGroup.removeAllViews()
                    viewGroup.visible()
                    viewGroup.addView(view)
                }

                override fun onBannerFailed(error: String?) {
                    Log.d(TAG, "onBannerFailed $configKey: $error")
                    units.remove(adUnit)
                    loadBannerShowByContext(
                        context, configKey, units, viewGroup, nameScreen, position, callback
                    )
                }

                override fun onBannerClicked() {
                    callback?.onBannerClicked()
                    CemAnalytics.logEventAndParams(
                        context, "$CLICK_ADS_FULL${nameScreen ?: configKey}"
                    )
                }

                override fun onBannerOpen() {
                    callback?.onBannerOpen()
                }

                override fun onBannerClose() {
                    callback?.onBannerClose()
                }
            })
    }

    fun loadBannerShowNoCollapsible(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        nameScreen: String? = null,
        callback: BannerAdListener? = null
    ) {
        val configManager = ConfigManager.getInstance(context)
        val adManager = configManager.adManagement
        if (adManager == null || !configManager.isEnable()) {
            Log.d(TAG, "loadAndShowBannerByContext: ads null or disable ")
            viewGroup.gone()
            callback?.onBannerFailed("Ads is null or disable")
            return
        }
        if (!isLoadedSuccess(nameScreen ?: configKey, timeInterval = adManager.bannerInterval)) {
            return
        }
        val adCollection = getAdCollection(adManager, configKey)
        if (adCollection == null) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection null")
            viewGroup.gone()
            callback?.onBannerFailed("AdCollection null")
            return
        }
        Log.d(TAG, "loadBannerShowNoCollapsible: ${Gson().toJson(adCollection)}")
        if (adCollection.isEmpty()) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection empty or disable")
            viewGroup.gone()
            callback?.onBannerFailed("No ad banner enable")
            return
        }
        getOrPutHandler(nameScreen ?: configKey).isLoaded = false
        loadBannerShowByContext(context = context,
            configKey = configKey,
            units = adCollection.toMutableList(),
            viewGroup = viewGroup,
            nameScreen = nameScreen,
            position = null,
            callback = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {
                    callback?.onBannerLoaded(banner, view)
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isLoaded = true
                        it.timeLoaded = System.currentTimeMillis()
                    }
                }

                override fun onBannerFailed(error: String?) {
                    callback?.onBannerFailed(error)
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isLoaded = true
                        it.timeLoaded = 0
                    }
                }

                override fun onBannerClicked() {
                    callback?.onBannerClicked()
                }

                override fun onBannerOpen() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = false
                    }
                }

                override fun onBannerClose() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = true
                    }
                }
            })
    }


    fun loadAndShowBannerByContext(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        nameScreen: String? = null,
        position: String? = null,
        callback: BannerAdListener? = null
    ) {
        val configManager = ConfigManager.getInstance(context)
        val adManager = configManager.adManagement
        if (adManager == null || !configManager.isEnable()) {
            Log.d(TAG, "loadAndShowBannerByContext: ads null or disable ")
            viewGroup.gone()
            callback?.onBannerFailed("Ads is null or disable")
            return
        }

        if (!isLoadedSuccess(nameScreen ?: configKey, timeInterval = adManager.bannerInterval)) {
            return
        }

        val adCollection = getAdCollection(adManager, configKey)
        if (adCollection == null) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection null")
            viewGroup.gone()
            callback?.onBannerFailed("AdCollection null")
            return
        }

        if (adCollection.isEmpty()) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection empty or disable")
            viewGroup.gone()
            callback?.onBannerFailed("No ad banner enable")
            return
        }
        getOrPutHandler(nameScreen ?: configKey).isLoaded = false
        loadBannerShowByContext(context = context,
            configKey = configKey,
            units = adCollection.toMutableList(),
            viewGroup = viewGroup,
            nameScreen = nameScreen,
            position = adCollection.first().position ?: position,
            callback = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {
                    callback?.onBannerLoaded(banner, view)
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.timeLoaded = System.currentTimeMillis()
                        it.isLoaded = true
                    }
                }

                override fun onBannerFailed(error: String?) {
                    callback?.onBannerFailed(error)
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.timeLoaded = 0
                        it.isLoaded = true
                    }
                }

                override fun onBannerClicked() {
                    callback?.onBannerClicked()
                }

                override fun onBannerOpen() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = false
                    }
                }

                override fun onBannerClose() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = true
                    }
                }

            })
    }

    fun loadBannerAndShowByActivity(
        activity: Activity,
        viewGroup: ViewGroup,
        configKey: String,
        nameScreen: String? = null,
        position: String? = null,
        callback: BannerAdListener? = null
    ) {
        val configManager = ConfigManager.getInstance(activity)
        val adManager = configManager.adManagement
        if (adManager == null || !configManager.isEnable()) {
            Log.d(TAG, "loadAndShowBannerByContext: ads null or disable ")
            viewGroup.gone()
            callback?.onBannerFailed("Ads is null or disable")
            return
        }

        if (!isLoadedSuccess(nameScreen ?: configKey, timeInterval = adManager.bannerInterval)) {
            return
        }

        val adCollection = getAdCollection(adManager, configKey)
        if (adCollection == null) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection null")
            viewGroup.gone()
            callback?.onBannerFailed("AdCollection null")
            return
        }

        if (adCollection.isEmpty()) {
            Log.d(TAG, "loadAndShowBannerByContext: adCollection empty or disable")
            viewGroup.gone()
            callback?.onBannerFailed("No ad banner enable")
            return
        }
        getOrPutHandler(nameScreen ?: configKey).isLoaded = false
        loadBannerShowByActivity(activity = activity,
            configKey = configKey,
            units = adCollection.toMutableList(),
            viewGroup = viewGroup,
            nameScreen = nameScreen,
            position = adCollection.first().position ?: position,
            callback = object : BannerAdListener {
                override fun onBannerLoaded(banner: BannerAdView, view: View) {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.timeLoaded = System.currentTimeMillis()
                        it.isLoaded = true
                    }
                    CemAnalytics.logEventAndParams(activity, "$ADS_FULL${nameScreen ?: configKey}")
                    callback?.onBannerLoaded(banner, view)
                    viewGroup.removeAllViews()
                    viewGroup.visible()
                    viewGroup.addView(view)
                }

                override fun onBannerFailed(error: String?) {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.timeLoaded = 0
                        it.isLoaded = true
                    }
                    callback?.onBannerFailed(error)
                }

                override fun onBannerClicked() {
                    callback?.onBannerClicked()
                }

                override fun onBannerOpen() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = false
                    }
                }

                override fun onBannerClose() {
                    getOrPutHandler(nameScreen ?: configKey).let {
                        it.isClosed = true
                    }
                }
            })
    }

    fun getPosition(context: Context, configKey: String): String? {
        return ConfigManager.getInstance(context).adManagement?.adUnitList?.getOrElse(configKey) { null }
            ?.firstOrNull()?.position
    }

    companion object {
        val TAG: String = CemBannerManager::class.java.simpleName

        const val ADS_FULL = "ADS_BANNER_"
        const val CLICK_ADS_FULL = "CLICK_BANNER_"

        private var mInstance: CemBannerManager? = null

        fun getInstance(): CemBannerManager {
            return mInstance ?: synchronized(this) {
                val instance = CemBannerManager()
                mInstance = instance
                instance
            }
        }
    }
}