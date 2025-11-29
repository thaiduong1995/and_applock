package com.cem.admodule.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.cem.admodule.ads.admob.AdmobRewardAdManager
import com.cem.admodule.ads.adx.AdxRewardAdManager
import com.cem.admodule.ads.applovin.ApplovinRewardAdManager
import com.cem.admodule.data.AdUnitItem
import com.cem.admodule.data.RewardAdItem
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.getAdCollection
import com.cem.admodule.ext.getAdUnit
import com.cem.admodule.inter.CemRewardAd
import com.cem.admodule.inter.CemRewardListener
import com.cem.admodule.inter.RewardLoadCallback
import com.google.gson.Gson
import javax.inject.Inject

class CemRewardAdManager @Inject constructor(
    private val context: Context
) {
    private val configManager: ConfigManager by lazy {
        ConfigManager.getInstance(context)
    }

    private val adsListRewardAd: MutableMap<String, CemRewardAd?> = HashMap()
    var isShowingAd: Boolean = false
        private set

    private fun createRewardManager(adUnitItem: AdUnitItem?): CemRewardAd? {
        if (adUnitItem == null) return null
        return when (AdNetwork.getNetwork(adUnitItem.adNetwork)) {
            AdNetwork.ADMOB -> AdmobRewardAdManager.newInstance(adUnitItem.adUnit)
            AdNetwork.APPLOVIN -> ApplovinRewardAdManager.newInstance(adUnitItem.adUnit)
            AdNetwork.ADX -> AdxRewardAdManager.newInstance(adUnitItem.adUnit)
            else -> null
        }
    }

    private fun loadAdsReward(
        activity: Activity,
        configKey: String,
        units: MutableList<AdUnitItem>,
        callback: RewardLoadCallback? = null
    ) {
        val adUnit = getAdUnit(units)
        if (adUnit == null) {
            callback?.onLoadedFailed("ad unit null")
            return
        }

        val adManager = createRewardManager(adUnit)
        if (adManager == null) {
            callback?.onLoadedFailed("ad manager null")
            return
        }

        adManager.load(activity, object : RewardLoadCallback {
            override fun onLoaded(rewardAds: CemRewardAd?) {
                Log.d(TAG, "loadAdsReward $configKey: ${Gson().toJson(adUnit)}")
                callback?.onLoaded(rewardAds)
            }

            override fun onLoadedFailed(error: String?) {
                Log.d(TAG, "onLoadedFailed $configKey $error")
                units.remove(adUnit)
                loadAdsReward(activity, configKey, units, callback)
            }
        })
    }

    fun loadAds(
        activity: Activity, configKey: String, callback: RewardLoadCallback? = null
    ) {
        val adManager = configManager.adManagement
        if (adManager == null || !configManager.isEnable()) {
            callback?.onLoadedFailed("load ad error or disable")
            return
        }

        val adCollection = getAdCollection(adManager, configKey)
        if (adCollection == null) {
            callback?.onLoadedFailed("ad list null")
            return
        }

        val unitsItem: MutableList<AdUnitItem> = adCollection.toMutableList()
        if (unitsItem.isEmpty()) {
            callback?.onLoadedFailed("ad list null")
            return
        }
//
//        if (unitsItem.firstOrNull()?.enable == false){
//            Log.d(CemInterstitialManager.TAG, "loadAds: disable")
//            callback?.onLoadedFailed("disable ads")
//            return
//        }

        if (adsListRewardAd.containsKey(configKey)) {
            callback?.onLoadedFailed("load add error, Ads already loaded")
            return
        }

        loadAdsReward(activity, configKey, unitsItem, object : RewardLoadCallback {
            override fun onLoaded(rewardAds: CemRewardAd?) {
                adsListRewardAd[configKey] = rewardAds
                callback?.onLoaded(rewardAds)
            }

            override fun onLoadedFailed(error: String?) {
                callback?.onLoadedFailed(error)
            }
        })
    }

    @JvmOverloads
    fun load(activity: Activity, configKey: String, callback: (() -> Unit)? = null) {
        loadAds(activity, configKey, object : RewardLoadCallback {
            override fun onLoaded(rewardAds: CemRewardAd?) {
                callback?.invoke()
            }

            override fun onLoadedFailed(error: String?) {
                callback?.invoke()
            }
        })
    }

    private fun showAsync(
        activity: Activity, configKey: String, reload: Boolean, callback: CemRewardListener? = null
    ) {
        if (!configManager.isEnable()) {
            callback?.onRewardFail("show ads failed")
            return
        }

        if (!adsListRewardAd.containsKey(configKey)) {
            callback?.onRewardFail("not ads when show")
            return
        }

        val rewardAds = adsListRewardAd[configKey]
        if (rewardAds == null) {
            callback?.onRewardFail("not ads when show")
            return
        }
        val rewardListener = object : CemRewardListener() {
            override fun onRewardAdded(rewardAdItem: RewardAdItem?) {
                isShowingAd = false
                callback?.onRewardAdded(rewardAdItem)
            }

            override fun onRewardFail(error: String?) {
                isShowingAd = false
                callback?.onRewardFail(error)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                adsListRewardAd.remove(configKey)
                if (reload) load(activity, configKey)
            }
        }
        rewardAds.show(activity, rewardListener, rewardListener)
    }

    fun showNotReload(
        activity: Activity, configKey: String, callback: CemRewardListener? = null
    ) {
        showAsync(activity, configKey, false, callback)
    }

    fun showAndReload(
        activity: Activity, configKey: String, callback: CemRewardListener? = null
    ) {
        showAsync(activity, configKey, true, callback)
    }

    fun isRewardLoaded(configKey: String): Boolean {
        if (!adsListRewardAd.containsKey(configKey)) return false
        val cemRewardAd = adsListRewardAd[configKey]
        return cemRewardAd != null && cemRewardAd.isLoaded
    }


    companion object {
        val TAG = "CemRewardAdManager"

        @SuppressLint("StaticFieldLeak")
        private var mInstance : CemRewardAdManager? = null

        fun getInstance(activity: Context): CemRewardAdManager {
            return mInstance ?: synchronized(this) {
                val instance = CemRewardAdManager(activity)
                mInstance = instance
                instance
            }
        }
    }
}