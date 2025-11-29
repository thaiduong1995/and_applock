package com.cem.admodule.ads.adx

import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.left
import arrow.core.right
import com.cem.admodule.ads.admob.AdmobInterstitialAdManager
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.manager.CemInterstitialManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class AdxInterstitialAdManager @Inject constructor(
    private val adUnitId: String?
) : CemInterstitialAd {
    private var interstitialAd: AdManagerInterstitialAd? = null
    private var callbackLoadAd: InterstitialLoadCallback? = null
    private var callbackShowAd: InterstitialShowCallback? = null
    override fun load(activity: Activity, callback: InterstitialLoadCallback?): CemInterstitialAd {
        (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
            callbackLoadAd = callback
            loadAdInternal(activity).map {
                Log.d(TAG, "load: onLoaded $adUnitId")
                interstitialAd = it
                callbackLoadAd?.onAdLoaded(this@AdxInterstitialAdManager)
            }.mapLeft {
                Log.d(TAG, "load: onLoaded $adUnitId")
                interstitialAd = null
                callbackLoadAd?.onAdFailedToLoaded(it)
            }
        }
        return this
    }

    private suspend fun loadAdInternal(activity: Activity) = suspendCancellableCoroutine { const ->
        if (adUnitId != null) {
            AdManagerInterstitialAd.load(activity,
                adUnitId,
                AdManagerAdRequest.Builder().build(),
                object : AdManagerInterstitialAdLoadCallback() {
                    override fun onAdLoaded(p0: AdManagerInterstitialAd) {
                        if (const.isActive) {
                            const.resume(p0.right())
                        }
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        if (const.isActive) {
                            const.resume(
                                ErrorCode(
                                    message = p0.message,
                                    code = p0.code
                                ).left()
                            )
                        }
                    }
                })
        } else {
            if (const.isActive) {
                const.resume(
                    ErrorCode(
                        message = "adUnit null",
                        code = -1
                    ).left()
                )
            }
        }
    }

    override val isLoaded: Boolean
        get() = interstitialAd != null && adUnitId != null

    override fun show(activity: Activity, callback: InterstitialShowCallback?) {
        if (adUnitId != null && interstitialAd != null) {
            this.callbackShowAd = callback
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d(TAG, "onAdDismissedFullScreenContent:$adUnitId")
                    callbackShowAd?.onDismissCallback(AdNetwork.ADX)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Log.d(TAG, "onAdShowedFullScreenContent:$adUnitId")
                    callbackShowAd?.onAdShowedCallback(AdNetwork.ADX)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Log.d(TAG, "onAdFailedToShowFullScreenContent:$adUnitId")
                    callbackShowAd?.onAdFailedToShowCallback(p0.code.toString())
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    callbackShowAd?.onAdClicked()
                }
            }
            interstitialAd?.show(activity)
        } else {
            callbackShowAd?.onDismissCallback(AdNetwork.ADX)
        }
    }

    companion object {
        private var TAG = CemInterstitialManager.TAG

        @JvmStatic
        fun newInstance(adUnit: String?): AdxInterstitialAdManager {
            return AdxInterstitialAdManager(adUnit)
        }
    }
}