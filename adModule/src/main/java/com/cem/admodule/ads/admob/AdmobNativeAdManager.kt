package com.cem.admodule.ads.admob

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.cem.admodule.R
import com.cem.admodule.inter.CemNativeAdView
import com.cem.admodule.inter.NativeAdCallback
import com.cem.admodule.manager.CemNativeManager
import com.cem.admodule.manager.CustomNativeView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AdmobNativeAdManager private constructor(
    private val adUnitId: String?
) : CemNativeAdView {

    private var mNativeAd: NativeAd? = null

    override val isLoaded: Boolean
        get() = adUnitId != null && mNativeAd != null

    override fun load(context: Context, callback: NativeAdCallback?): CemNativeAdView {
        AdLoader.Builder(context, adUnitId ?: return this).forNativeAd { ads: NativeAd ->
            mNativeAd = ads
            callback?.onNativeLoaded(this)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad: ${p0.message} adUnitId")
                super.onAdFailedToLoad(p0)
                callback?.onNativeFailed(p0.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "onAdLoaded: success")
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
        ).build().loadAds(AdRequest.Builder().build(), 2)
        return this
    }

    override fun show(view: CustomNativeView, layoutRes: Int) {
        try {
            view.setTemplateType(layoutRes)
            if (isLoaded) {
                val store = mNativeAd?.store
                val advertiser = mNativeAd?.advertiser
                val headline = mNativeAd?.headline
                val body = mNativeAd?.body
                val cta = mNativeAd?.callToAction
                val starRating = mNativeAd?.starRating
                val icon = mNativeAd?.icon

                val secondaryText : String?
                val  nativeAdView = view.findViewById<NativeAdView>(R.id.native_ad_view)
                nativeAdView?.let {
                    if (view.callToActionView != null){
                        nativeAdView.callToActionView = view.callToActionView
                    }
                    if (view.primaryView != null){
                        nativeAdView.headlineView = view.primaryView
                    }
                    if (view.mediaView != null){
                        nativeAdView.mediaView = view.mediaView
                    }

                    if (view.secondaryView != null) view.secondaryView!!.visibility = View.VISIBLE
                    if (view.adHasOnlyStore(mNativeAd)) {
                        nativeAdView.storeView = view.secondaryView
                        secondaryText = store
                    } else if (!TextUtils.isEmpty(advertiser)) {
                        nativeAdView.advertiserView = view.secondaryView
                        secondaryText = advertiser
                    } else {
                        secondaryText = ""
                    }
                    if (view.primaryView != null) view.primaryView!!.text = headline
                    if (view.callToActionView != null) view.callToActionView!!.text = cta

                    if ((starRating != null) && (starRating > 0)) {
                        if (view.secondaryView != null) view.secondaryView!!.visibility = View.GONE
                        if (view.ratingBar != null) view.ratingBar!!.visibility = View.VISIBLE
                        if (view.ratingBar != null) view.ratingBar!!.max = 5
                        if (view.ratingBar != null) nativeAdView.starRatingView =
                            view.ratingBar
                    } else {
                        if (view.secondaryView != null) view.secondaryView!!.text = secondaryText
                        if (view.secondaryView != null) view.secondaryView!!.visibility = View.VISIBLE
                        if (view.ratingBar != null) view.ratingBar!!.visibility = View.GONE
                    }
                    if (icon != null) {
                        if (view.iconView != null) view.iconView!!.visibility = View.VISIBLE
                        if (view.iconView != null) view.iconView!!.setImageDrawable(icon.drawable)
                    } else {
                        if (view.iconView != null) view.iconView!!.visibility = View.INVISIBLE
                    }
                    if (view.tertiaryView != null) {
                        view.tertiaryView!!.text = body
                        nativeAdView.bodyView = view.tertiaryView
                    }
                    nativeAdView.setNativeAd(mNativeAd ?: return)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "show: ${e.message}")
            e.printStackTrace()
        }
    }

    companion object{
        val TAG = CemNativeManager.TAG
        @JvmStatic
        fun newInstance(adUnit : String?) : AdmobNativeAdManager{
            return AdmobNativeAdManager(adUnit)
        }
    }
}