package com.cem.admodule.ads.applovin

import android.content.Context
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.cem.admodule.R
import com.cem.admodule.inter.CemNativeAdView
import com.cem.admodule.inter.NativeAdCallback
import com.cem.admodule.manager.CemNativeManager
import com.cem.admodule.manager.CustomNativeView
import javax.inject.Inject

class ApplovinNativeAdManager @Inject constructor(
    private val adUnitId: String?
) : CemNativeAdView {
    private var nativeMaxAdView: MaxNativeAdView? = null
    private var maxNativeAd: MaxNativeAd? = null
    override fun load(context: Context, callback: NativeAdCallback?): CemNativeAdView {
        val nativeAdLoader = MaxNativeAdLoader(adUnitId, context)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                super.onNativeAdLoaded(p0, p1)
                nativeMaxAdView = p0
                maxNativeAd = p1.nativeAd
                callback?.onNativeLoaded(this@ApplovinNativeAdManager)
            }

            override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                super.onNativeAdLoadFailed(p0, p1)
                callback?.onNativeFailed(p0)
            }
        })
        nativeAdLoader.loadAd()
        return this
    }

    override fun show(view: CustomNativeView, layoutRes: Int) {
        try {
            view.setTemplateType(layoutRes)
            if (isLoaded) {
                view.findViewById<FrameLayout>(R.id.viewNative)?.let {
                    it.removeAllViews()
                    it.addView(nativeMaxAdView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val isLoaded: Boolean
        get() = nativeMaxAdView != null && maxNativeAd != null && adUnitId != null

    companion object {
        val TAG = CemNativeManager.TAG

        @JvmStatic
        fun newInstance(adUnit: String?): ApplovinNativeAdManager {
            return ApplovinNativeAdManager(adUnit)
        }
    }
}