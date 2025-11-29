package com.suntech.mytools.mytools.banner

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.google.android.gms.ads.*
import com.suntech.mytools.BuildConfig
import com.suntech.mytools.mytools.Constants
import com.suntech.mytools.mytools.datalocal.DataLocal
import com.suntech.mytools.mytools.datalocal.DataLocal.listKeyBanner
import com.suntech.mytools.tools.NetworkUtils


object BannerManager {
    var TAG = "BannerManager"

    fun loadBannerAdView(
        context: Context?,
        onLoaded: ((AdView) -> Unit)? = null,
        onFailedToLoadAll: (() -> Unit)? = null,
        currentIndex: Int = 0
    ) {
        if (context == null) {
            Log.d(TAG, "loadBannerAdView: 1")
            onFailedToLoadAll?.invoke()
            return
        }
        if (DataLocal.isVip()) {
            Log.d(TAG, "loadBannerAdView: 2")
            onFailedToLoadAll?.invoke()
            return
        }
        if (!NetworkUtils.isNetworkConnected(context)) {
            Log.d(TAG, "loadBannerAdView: 3")
            onFailedToLoadAll?.invoke()
            return
        }
        val adView = AdView(context)
        adView.setAdSize(AdSize.BANNER)
        //get currentIndex
        var indexKey = currentIndex
        //get key
        adView.adUnitId = if (BuildConfig.DEBUG) {
            Constants.ID_BANNER
        } else listKeyBanner()[indexKey]?.getIdBanner().toString()
        Log.d(TAG, "loadBannerAdView: ${adView.adUnitId}")
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                onLoaded?.invoke(adView)
                Log.d(TAG, "onAdLoaded")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                indexKey += 1
                if (indexKey < listKeyBanner().size) {
                    loadBannerAdView(
                        context = context,
                        onLoaded = onLoaded,
                        onFailedToLoadAll = onFailedToLoadAll,
                        currentIndex = indexKey
                    )
                } else {
                    onFailedToLoadAll?.invoke()
                }
                Log.d(TAG, "onAdFailedToLoad: ${p0.message}")
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    fun loadBannerMaxAdView(
        context: Context?,
        onLoaded: ((MaxAdView) -> Unit)? = null,
        onFailedToLoadAll: (() -> Unit)? = null
    ) {
        if (context == null) {
            Log.d(TAG, "loadBannerAdView: 1")
            onFailedToLoadAll?.invoke()
            return
        }
        if (DataLocal.isVip()) {
            Log.d(TAG, "loadBannerAdView: 2")
            onFailedToLoadAll?.invoke()
            return
        }
        if (!NetworkUtils.isNetworkConnected(context)) {
            Log.d(TAG, "loadBannerAdView: 3")
            onFailedToLoadAll?.invoke()
            return
        }
        val adUnitId = Constants.ID_BANNER_MAX
        val adView = MaxAdView(adUnitId, context)
        Log.d(TAG, "loadBannerAdView: ${adView.adUnitId}")
        adView.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(p0: MaxAd?) {
                onLoaded?.invoke(adView)
            }

            override fun onAdDisplayed(p0: MaxAd?) {
            }

            override fun onAdHidden(p0: MaxAd?) {
            }

            override fun onAdClicked(p0: MaxAd?) {
            }

            override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                onFailedToLoadAll?.invoke()
            }

            override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                onFailedToLoadAll?.invoke()
            }

            override fun onAdExpanded(p0: MaxAd?) {
            }

            override fun onAdCollapsed(p0: MaxAd?) {
            }

        })
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heightPx = context.resources.getDimensionPixelSize(com.suntech.mytools.R.dimen.size_50)
        adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        adView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        adView.loadAd()
    }

    fun loadBannerCollapsible(
        context: Context,
        adSize: AdSize,
        onLoaded: ((AdView) -> Unit)? = null,
        onFailedToLoadAll: (() -> Unit)? = null,
        currentIndex: Int = 0
    ) {
        if (DataLocal.isVip()) {
            onFailedToLoadAll?.invoke()
            return
        }
        if (!NetworkUtils.isNetworkConnected(context)) {
            onFailedToLoadAll?.invoke()
            return
        }
        val adView = AdView(context)
        adView.setAdSize(adSize)
        //get currentIndex
        var indexKey = currentIndex
        //get key
        adView.adUnitId = if (BuildConfig.DEBUG) {
            Constants.ID_BANNER
        } else listKeyBanner()[indexKey]?.getIdBanner().toString()

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                onLoaded?.invoke(adView)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                indexKey += 1
                if (indexKey < listKeyBanner().size) {
                    loadBannerCollapsible(
                        context = context,
                        adSize = adSize,
                        onLoaded = onLoaded,
                        onFailedToLoadAll = onFailedToLoadAll,
                        currentIndex = indexKey
                    )
                } else {
                    onFailedToLoadAll?.invoke()
                }
            }
        }
        val adRequest = GetCollapsibleRequest.getCollapsibleRequest().build()
        adView.loadAd(adRequest)

    }
}