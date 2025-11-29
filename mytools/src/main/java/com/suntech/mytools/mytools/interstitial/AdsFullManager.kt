package com.suntech.mytools.mytools.interstitial

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.suntech.mytools.BuildConfig
import com.suntech.mytools.mytools.AppAdmob
import com.suntech.mytools.mytools.Constants
import com.suntech.mytools.mytools.datalocal.DataLocal
import com.suntech.mytools.tools.NetworkUtils

object AdsFullManager {
    var interstitialInterval = 5000L
    var isAdLoading: Boolean = false
    var TAG = "AdsFullManager"

    fun loadInterstitial(
        context: Context?,
        onLoaded: ((InterstitialAd) -> Unit)? = null,
        onFailedToLoadAll: (() -> Unit)? = null,
        currentIndex: Int = 0
    ) {
        if (context == null) {
            onFailedToLoadAll?.invoke()
            return
        }
        context.let {
            if (DataLocal.isVip()) {
                onFailedToLoadAll?.invoke()
                return
            }
            if (NetworkUtils.isNetworkConnected(context)) {
                if (AppAdmob.interstitialAd == null) {
                    var index = currentIndex
                    if (index >= DataLocal.listKeyFull().size) {
                        onFailedToLoadAll?.invoke()
                        return
                    }
                    if (index == 0 && isAdLoading) {
                        onFailedToLoadAll?.invoke()
                        return
                    }
                    isAdLoading = true
                    val adRequest = AdRequest.Builder().build()
                    val adUnitId = if (BuildConfig.DEBUG) {
                        Constants.ID_INTERSTITIAL
                    } else DataLocal.listKeyFull()[index]?.getIdFull().toString()
                    InterstitialAd.load(it,
                        adUnitId,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(p0: InterstitialAd) {
                                Log.d(TAG, p0.responseInfo.mediationAdapterClassName.toString())
                                AppAdmob.interstitialAd = p0
                                AppAdmob.isShowing = false
                                isAdLoading = false
                                onLoaded?.invoke(p0)
                            }

                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                index += 1
                                if (index < DataLocal.listKeyFull().size) {
                                    loadInterstitial(
                                        context = context,
                                        onLoaded = onLoaded,
                                        onFailedToLoadAll = onFailedToLoadAll,
                                        currentIndex = index
                                    )
                                } else {
                                    isAdLoading = false
                                    AppAdmob.interstitialAd = null
                                    AppAdmob.isShowing = false
                                    onFailedToLoadAll?.invoke()
                                }
                                Log.d(TAG, "onAdFailedToLoad: ${p0.message}")
                            }
                        })
                } else {
                    onFailedToLoadAll?.invoke()
                }
            } else {
                onFailedToLoadAll?.invoke()
            }
        }
    }

    fun isShowing(): Boolean {
        return AppAdmob.isShowing
    }

    fun showInterstitial(
        context: Context?,
        isShowRemote: Boolean = true,
        onDismiss: (() -> Unit)? = null
    ) {

        if (context == null){
            onDismiss?.invoke()
            return
        }

        if (DataLocal.isVip()) {
            onDismiss?.invoke()
            return
        }

        if (!isShowRemote){
            onDismiss?.invoke()
            return
        }

        if (AppAdmob.isOpen) {
            onDismiss?.invoke()
            return
        }
        if (AppAdmob.interstitialAd == null) {
            onDismiss?.invoke()
            return
        }
        if (System.currentTimeMillis() - AppAdmob.lastTimeShowAdsFull < interstitialInterval) {
            onDismiss?.invoke()
            return
        }
        AppAdmob.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onDismiss?.invoke()
                super.onAdDismissedFullScreenContent()
                isAdLoading = false
                AppAdmob.interstitialAd = null
                AppAdmob.isShowing = false
                interstitialInterval = AppAdmob.remoteConfigViewModel.delayTimeDefault
                AppAdmob.lastTimeShowAdsFull = System.currentTimeMillis()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                onDismiss?.invoke()
                super.onAdFailedToShowFullScreenContent(p0)
                AppAdmob.interstitialAd = null
                AppAdmob.isShowing = false
                isAdLoading = false
                interstitialInterval = AppAdmob.remoteConfigViewModel.delayTimeDefault
                AppAdmob.lastTimeShowAdsFull = System.currentTimeMillis()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AppAdmob.interstitialAd = null
                AppAdmob.isShowing = true
                isAdLoading = false
            }
        }
        AppAdmob.interstitialAd?.show(context as Activity)

    }
}