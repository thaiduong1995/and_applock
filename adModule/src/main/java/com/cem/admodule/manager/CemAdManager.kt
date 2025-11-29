package com.cem.admodule.manager

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.cem.admodule.R
import com.cem.admodule.data.TimeManager
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.Callback
import com.cem.admodule.inter.CemNativeAdView
import com.cem.admodule.inter.CemRewardListener
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.inter.OpenLoadCallback
import com.cem.admodule.inter.RewardLoadCallback

class CemAdManager private constructor(activity: Context) {

    private val cemInterstitialManager by lazy {
        CemInterstitialManager.getInstance(activity)
    }

    private val cemBannerManager by lazy {
        CemBannerManager.getInstance()
    }

    private val cemBannerReloadManager by lazy {
        CemBannerReloadManager.getInstance()
    }

    private val cemNativeManager by lazy {
        CemNativeManager.getInstance(activity)
    }

    private val cemRewardAdManager by lazy {
        CemRewardAdManager.getInstance(activity)
    }

    private val configManager by lazy {
        ConfigManager.getInstance(activity)
    }

    val getAdManager
        get() = configManager.adManagement

    val getVersionManager
        get() = configManager.versionManagement

    val isShowingInterstitialAd: Boolean
        get() = cemInterstitialManager.isShowingAd || cemRewardAdManager.isShowingAd

    val timeReloadNativeConfig
        get() = configManager.timeReloadNativeConfig()

    fun setTimeReload(timeManager: TimeManager) = configManager.setTimeReload(timeManager)
    fun getTimeReload(configKey: String): TimeManager? = configManager.getTimeReload(configKey)

    //fetch config remote
    suspend fun fetchConfig(configKey: String, fileLocal: String) =
        configManager.fetchConfig(configKey, fileLocal)

    fun initMMKV() = apply {
        configManager.initMMKV()
    }

    fun setIsVip(value: Boolean) = apply {
        configManager.setIsVip(value)
    }

    fun isVip() = configManager.isVip()

    //load inter full
    fun loadInterstitial(
        activity: Activity, configKey: String, callback: InterstitialLoadCallback? = null
    ) = apply {
        cemInterstitialManager.loadAds(activity, configKey, callback)
    }

    //load inter full time out
    fun loadInterstitial(
        activity: Activity, configKey: String, callback: InterstitialLoadCallback? = null, onTimeOut: (() -> Unit)? = null
    ) = apply {
        cemInterstitialManager.loadAds(activity, configKey, callback, onTimeOut)
    }


    //show inter full
    fun showInterstitial(
        activity: Activity,
        configKey: String,
        callback: InterstitialShowCallback? = null,
        nameScreen: String? = null
    ) = apply {
        cemInterstitialManager.showAdsAsync(
            activity = activity, configKey = configKey, callback = callback, nameScreen = nameScreen
        )
    }

    fun showInterstitialReload(
        activity: Activity,
        configKey: String,
        callback: InterstitialShowCallback? = null,
        nameScreen: String? = null
    ) = apply {
        cemInterstitialManager.showAdsAndReloadAsync(
            activity = activity, configKey = configKey, callback = callback, nameScreen = nameScreen
        )
    }

    fun showInterstitialCallback(
        activity: Activity,
        configKey: String,
        callback: (() -> Unit)? = null,
        nameScreen: String? = null
    ) = apply {
        cemInterstitialManager.showAdsAndCallback(
            activity = activity, configKey = configKey, callback = callback, nameScreen = nameScreen
        )
    }

    fun showInterstitialReloadCallback(
        activity: Activity,
        configKey: String,
        callback: (() -> Unit)? = null,
        nameScreen: String? = null
    ) = apply {
        cemInterstitialManager.showAdsAndCallbackReload(
            activity = activity, configKey = configKey, callback = callback, nameScreen = nameScreen
        )
    }

    // Native

    //load and show native
    fun loadAndShowNative(context: Context, nativeAdView: CustomNativeView, configKey: String, layoutRes: Int) =
        apply {
            cemNativeManager.loadAndShowNative(context, nativeAdView, configKey, layoutRes)
        }

    //load native by cache
    fun loadNativeNativeView(
        context: Context, configKey: String, callback: Callback<CemNativeAdView>? = null
    ) = apply {
        cemNativeManager.loadNative(context, configKey, callback)
    }

    //load with callback
    fun loadNativeCallback(
        activity: Context, configKey: String, callback: ((Boolean) -> Unit)? = null
    ) = apply {
        cemNativeManager.loadNative(activity, configKey, callback)
    }

    //get native by cache
    fun getNative(
        activity: Context, configKey: String, reload: Boolean
    ) = apply {
        cemNativeManager.getNative(activity, configKey, reload)
    }

    //get native by cache config key
    fun getNativeByList(
        activity: Context, configKey: String, reload: Boolean
    ) = apply {
        cemNativeManager.getNativeByList(activity, configKey, reload)
    }

    //show native by cache
    fun showNative(
        configKey: String, view: CustomNativeView, layoutRes: Int = R.layout.admob_native_ad_view
    ) = apply {
        cemNativeManager.showNative(configKey, view, layoutRes)
    }

    //show native by cache config key
    fun showNativeByList(
        configKey: String, view: CustomNativeView, layoutRes: Int = R.layout.admob_native_ad_view
    ) = apply {
        cemNativeManager.showNativeByList(configKey, view, layoutRes)
    }

    //check native đã có chưa
    fun isNativeLoaded(
        configKey: String
    ) = apply {
        cemNativeManager.isNativeLoaded(configKey)
    }

    //reward
    //load reward with callback
    fun loadRewardCallback(
        activity: Activity, configKey: String, callbackLoaded: (() -> Unit)? = null
    ) = apply {
        cemRewardAdManager.load(activity, configKey, callbackLoaded)
    }

    //load reward with interface
    fun loadRewardInterface(
        activity: Activity, configKey: String, callback: RewardLoadCallback? = null
    ) = apply {
        cemRewardAdManager.loadAds(activity, configKey, callback)
    }

    //show reward not reload ads
    fun showRewardNotReload(
        activity: Activity, configKey: String, listener: CemRewardListener? = null
    ) = apply {
        cemRewardAdManager.showNotReload(activity, configKey, listener)
    }

    //show reward reload ads
    fun showRewardAndReload(
        activity: Activity, configKey: String, listener: CemRewardListener? = null
    ) = apply {
        cemRewardAdManager.showAndReload(activity, configKey, listener)
    }

    fun isRewardLoaded(
        configKey: String
    ): Boolean = cemRewardAdManager.isRewardLoaded(configKey)

    fun isReloadNativeLoaded(
        configKey: String
    ): Boolean = cemNativeManager.isNativeLoaded(configKey)

    //Banner
    //load banner by context
    fun loadBannerAndShowByContext(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        nameScreen: String? = null
    ): CemAdManager = apply {
        cemBannerManager.loadAndShowBannerByContext(
            context = context,
            viewGroup = viewGroup,
            configKey = configKey,
            nameScreen = nameScreen,
            position = position,
            callback = callback
        )
    }

    //load banner by activity
    fun loadBannerAndShowByActivity(
        activity: Activity,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        nameScreen: String? = null,
    ): CemAdManager = apply {
        cemBannerManager.loadBannerAndShowByActivity(
            activity = activity,
            viewGroup = viewGroup,
            configKey = configKey,
            nameScreen = nameScreen,
            position = position,
            callback = callback
        )
    }

    fun loadBannerShowNoCollapsible(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        callback: BannerAdListener? = null,
        nameScreen: String? = null
    ): CemAdManager = apply {
        cemBannerManager.loadBannerShowNoCollapsible(
            context = context,
            viewGroup = viewGroup,
            configKey = configKey,
            nameScreen = nameScreen,
            callback = callback
        )
    }

    //load banner by context reload
    fun loadBannerAndShowByContextReload(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        refreshTimeBanner: Int = 10
    ): CemAdManager = apply {
        cemBannerReloadManager.loadAndShowBannerByContextReload(
            context, viewGroup, configKey, position, callback, refreshTimeBanner
        )
    }

    //load banner by activity
    fun loadBannerAndShowByActivityReload(
        activity: Activity,
        viewGroup: ViewGroup,
        configKey: String,
        position: String? = null,
        callback: BannerAdListener? = null,
        refreshTimeBanner: Int = 10
    ): CemAdManager = apply {
        cemBannerReloadManager.loadBannerAndShowByActivityReload(
            activity, viewGroup, configKey, position, callback, refreshTimeBanner
        )
    }

    fun loadBannerShowNoCollapsibleReload(
        context: Context,
        viewGroup: ViewGroup,
        configKey: String,
        callback: BannerAdListener? = null,
        refreshTimeBanner: Int = 10
    ): CemAdManager = apply {
        cemBannerReloadManager.loadBannerShowNoCollapsibleReload(
            context, viewGroup, configKey, callback, refreshTimeBanner
        )
    }

    fun removeCallbackAndMessages(configKey: String, messages: String? = null): CemAdManager =
        apply {
            cemBannerReloadManager.removeRunnableAndCallback(configKey = configKey, messages)
        }

    fun removeBannerLoaded(nameScreen: String): CemAdManager = apply {
        cemBannerManager.removeBannerLoaded(nameScreen)
    }


    //get position activity
    fun getPosition(
        context: Context, configKey: String
    ): String? = cemBannerManager.getPosition(context, configKey)


    //OPEN
    fun fetchOpenAds(adConfigKey: String, callback: OpenLoadCallback? = null) = apply {
        CemAppOpenManager.instance.fetchOpenAds(adConfigKey, callback)
    }

    fun enableOpenAds() {
        CemAppOpenManager.instance.enableOpenAds()
    }

    fun blockOpenAds() {
        CemAppOpenManager.instance.blockOpenAds()
    }

    fun registerProcessLifecycle() = apply {
        CemAppOpenManager.instance.registerProcessLifecycle()
    }

    fun unregisterProcessLifecycle() = apply {
        CemAppOpenManager.instance.unregisterProcessLifecycle()
    }

    fun registerCallback(fullScreenContentCallback: InterstitialShowCallback?) = apply {
        CemAppOpenManager.instance.registerCallback(fullScreenContentCallback)
    }

    fun unregisterCallback() = apply {
        CemAppOpenManager.instance.unregisterCallback()
    }

    fun setIgnoreActivities(data: List<String>) = apply {
        CemAppOpenManager.instance.setIgnoreActivities(data)
    }

    companion object {
        val TAG: String = CemAdManager::class.java.simpleName
        private var mInstance: CemAdManager? = null

        fun getInstance(activity: Context): CemAdManager {
            return mInstance ?: synchronized(this) {
                mInstance ?: CemAdManager(activity).also { mInstance = it }
            }
        }
    }
}