package com.cem.admodule.manager

import android.content.Context
import android.util.Log
import android.view.View
import com.cem.admodule.ads.admob.AdmobNativeAdManager
import com.cem.admodule.ads.adx.AdxNativeAdManager
import com.cem.admodule.data.AdUnitItem
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.getAdCollection
import com.cem.admodule.ext.getAdUnit
import com.cem.admodule.ext.gone
import com.cem.admodule.ext.visible
import com.cem.admodule.inter.Callback
import com.cem.admodule.inter.CemNativeAdView
import com.cem.admodule.inter.NativeAdCallback
import com.google.gson.Gson

class CemNativeManager private constructor(
    context: Context
) {
    private val configManager: ConfigManager by lazy {
        ConfigManager.getInstance(context)
    }

    private val adsDataNative: MutableMap<String, CemNativeAdView> = HashMap()

    private val adsListNative: MutableMap<String, List<CemNativeAdView>> = HashMap()


    private fun createNative(adUnitItem: AdUnitItem?): CemNativeAdView? {
        if (adUnitItem == null) return null
        val adNetwork = adUnitItem.adNetwork
        return when (AdNetwork.getNetwork(adNetwork)) {
            AdNetwork.ADMOB -> AdmobNativeAdManager.newInstance(adUnitItem.adUnit)
//            AdNetwork.APPLOVIN -> ApplovinNativeAdManager.newInstance(adUnitItem.adUnit)
            AdNetwork.ADX -> AdxNativeAdManager.newInstance(adUnitItem.adUnit)
            else -> null
        }
    }
    private fun loadAndShow(
        context: Context,
        configKey: String,
        units: MutableList<AdUnitItem>,
        nativeAdView: CustomNativeView,
        layoutRes: Int
    ) {
        val adUnit = getAdUnit(units)
        if (adUnit == null) {
            Log.d(TAG, "loadAndShow: adunit null")
            nativeAdView.gone()
            return
        }

        val adManager = createNative(adUnit)
        if (adManager == null) {
            Log.d(TAG, "loadAndShow: ad manager null")
            nativeAdView.gone()
            return
        }

        adManager.load(context, object : NativeAdCallback {
            override fun onNativeLoaded(view: CemNativeAdView) {
                Log.d(TAG, "onNativeLoaded $configKey: ${Gson().toJson(adUnit)}")
                view.show(nativeAdView, layoutRes)
            }

            override fun onNativeFailed(errorCode: String?) {
                Log.d(TAG, "onNativeFailed $configKey $errorCode")
                units.remove(adUnit)
                loadAndShow(context, configKey, units, nativeAdView, layoutRes)
            }
        })
    }


    //load native mà hiển thị ngay
    fun loadAndShowNative(context: Context, nativeAdView: CustomNativeView, configKey: String, layoutRes: Int) {
        val adManagement = configManager.adManagement
        if (adManagement == null || !configManager.isEnable()) {
            nativeAdView.gone()
            return
        }
        val adCollection = getAdCollection(adManagement, configKey)
        if (adCollection == null) {
            nativeAdView.gone()
            return
        }
        val units: MutableList<AdUnitItem> = ArrayList(adCollection)
        if (units.isEmpty()) {
            nativeAdView.gone()
            return
        }

//        if (units.firstOrNull()?.enable == false){
//            Log.d(TAG, "loadAds: disable")
//            nativeAdView.gone()
//            return
//        }


        nativeAdView.visibility = View.VISIBLE
        loadAndShow(context, configKey, units, nativeAdView, layoutRes)
    }

    private fun loadNativeInternal(
        context: Context,
        configKey: String,
        units: MutableList<AdUnitItem>,
        callback: Callback<CemNativeAdView>?
    ) {
        val adUnit = getAdUnit(units)
        if (adUnit == null) {
            Log.d(TAG, "loadNativeInternal: ad unit null")
            callback?.onFailure(NullPointerException("adUnit null"))
            return
        }

        val adManager = createNative(adUnit)
        if (adManager == null) {
            Log.d(TAG, "loadNativeInternal: adManager null")
            callback?.onFailure(NullPointerException("adManager null"))
            return
        }

        adManager.load(context, object : NativeAdCallback {
            override fun onNativeLoaded(view: CemNativeAdView) {
                Log.d(TAG, "onNativeLoaded $configKey: ${Gson().toJson(adUnit)}")
                callback?.onSuccess(view)
            }

            override fun onNativeFailed(errorCode: String?) {
                units.remove(adUnit)
                Log.d(TAG, "onNativeFailed $configKey $errorCode")
                loadNativeInternal(context, configKey, units, callback)
            }
        })
    }

    //load native và lưu cache
    fun loadNative(
        context: Context, configKey: String, callback: Callback<CemNativeAdView>? = null
    ) {
        val adManagement = configManager.adManagement
        if (adManagement == null || !configManager.isEnable()) {
            callback?.onFailure(NullPointerException("Load native on ad disabled"))
            return
        }
        val adCollection = getAdCollection(adManagement, configKey)
        if (adCollection == null) {
            callback?.onFailure(NullPointerException("Load native on no ad config"))
            return
        }
        val units: MutableList<AdUnitItem> = ArrayList(adCollection)
        if (units.isEmpty()) {
            callback?.onFailure(NullPointerException("Load native on no ad config"))
            return
        }

//        if (units.firstOrNull()?.enable == false){
//            Log.d(TAG, "loadAds: disable")
//            callback?.onFailure(NullPointerException("loadAds: disable"))
//            return
//        }

        loadNativeInternal(context, configKey, units, object : Callback<CemNativeAdView> {
            override fun onSuccess(data: CemNativeAdView) {
                adsDataNative[configKey] = data
                addNativeWithList(configKey, data)
                callback?.onSuccess(data)
            }

            override fun onFailure(e: Exception?) {
                callback?.onFailure(e)
            }
        })
    }

    //show native với key config
    fun showNative(configKey: String, view: CustomNativeView, layoutRes: Int) {
        if (!configManager.isEnable()) {
            Log.d(TAG, "showNative: ${configManager.isEnable()}")
            view.gone()
            return
        }
        val nativeManager = adsDataNative[configKey]
        if (nativeManager != null) {
            Log.d(TAG, "showNative: ads exits $layoutRes")
            view.visibility = View.VISIBLE
            nativeManager.show(view, layoutRes)
        } else {
            Log.d(TAG, "showNative: ads null")
            view.gone()
        }
    }

    //show native với key config dạng list
    fun showNativeByList(configKey: String, view: CustomNativeView, layoutRes: Int) {
        if (!configManager.isEnable()) {
            Log.d(TAG, "showNative: ${configManager.isEnable()}")
            view.gone()
            return
        }

        val nativeManager = adsListNative[configKey]
        if (nativeManager != null) {
            Log.d(TAG, "showNative: ads exits")
            nativeManager.shuffled().lastOrNull()?.let {
                view.visible()
                it.show(view, layoutRes)
            }
        } else {
            Log.d(TAG, "showNative: ads null")
            view.gone()
        }
    }

    //load native  callback
    fun loadNative(activity: Context, configKey: String, callback: ((Boolean) -> Unit)? = null) {
        loadNative(activity, configKey, object : Callback<CemNativeAdView> {
            override fun onSuccess(data: CemNativeAdView) {
                callback?.invoke(true)
            }

            override fun onFailure(e: Exception?) {
                callback?.invoke(false)
            }
        })
    }

    //check xem native đã có chưa
    fun isNativeLoaded(configKey: String): Boolean {
        return adsDataNative.containsKey(configKey) || adsListNative.containsKey(configKey)
    }

    //get native cache
    fun getNative(
        activity: Context, configKey: String, reload: Boolean
    ): CemNativeAdView? {
        var nativeAds: CemNativeAdView? = null
        if (adsDataNative.containsKey(configKey)) nativeAds = adsDataNative[configKey]
        if (reload) loadNative(activity, configKey, callback = {

        })
        return nativeAds
    }

    //get native cache list
    fun getNativeByList(
        activity: Context, configKey: String, reload: Boolean
    ): CemNativeAdView? {
        var nativeAds: CemNativeAdView? = null
        if (adsListNative.containsKey(configKey)) nativeAds = adsListNative[configKey]?.lastOrNull()
        if (reload) loadNative(activity, configKey, callback = {

        })
        return nativeAds
    }

    fun addNativeWithList(
        configKey: String, view: CemNativeAdView
    ) {
        val newList = mutableListOf<CemNativeAdView>()
        val listData = adsListNative[configKey]
        if (listData != null) {
            newList.addAll(listData)
        }
        newList.add(view)
        newList.let {
            adsListNative[configKey] = newList
        }
    }

    companion object {
        val TAG = CemNativeManager::class.java.simpleName

        private var mInstance: CemNativeManager? = null

        fun getInstance(context: Context): CemNativeManager {
            return mInstance ?: synchronized(this) {
                val instance = CemNativeManager(context)
                mInstance = instance
                instance
            }
        }
    }
}