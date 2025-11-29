package com.cem.admodule.ads.applovin

import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.left
import arrow.core.right
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.cem.admodule.ads.admob.AdmobInterstitialAdManager
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.manager.CemInterstitialManager
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class ApplovinInterstitialAdManager @Inject constructor(
    private val unitId: String?
) : CemInterstitialAd {

    private var interstitialAd: MaxInterstitialAd? = null
    override fun load(activity: Activity, callback: InterstitialLoadCallback?): CemInterstitialAd {
        (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
            loadAdInternal(activity).map {
                Log.d(TAG, "load: onLoaded $unitId")
                interstitialAd = it
                callback?.onAdLoaded(this@ApplovinInterstitialAdManager)
            }.mapLeft {
                Log.d(TAG, "load: onLoadFailed $unitId")
                interstitialAd = null
                callback?.onAdFailedToLoaded(it)
            }
        }
        return this
    }

    private suspend fun loadAdInternal(activity: Activity) = suspendCancellableCoroutine { const ->
        if (unitId != null && interstitialAd == null) {
            interstitialAd = MaxInterstitialAd(unitId, activity)
            val callback = object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    Log.d(TAG, "onAdLoaded applovin: ${p0.adUnitId}")
                    if (const.isActive) {
                        const.resume(interstitialAd.right())
                    }
                }

                override fun onAdDisplayed(p0: MaxAd) {
                }

                override fun onAdHidden(p0: MaxAd) {
                }

                override fun onAdClicked(p0: MaxAd) {
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.d(TAG, "onAdLoadFailed applovin: ${p1.message}")
                    if (const.isActive) {
                        const.resume(
                            ErrorCode(
                                message = p1.message,
                                code = p1.code
                            ).left()
                        )
                    }
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                }
            }
            interstitialAd?.setListener(callback)
            interstitialAd?.loadAd()
        }else {
            if (const.isActive) {
                const.resume(
                    ErrorCode(
                        message = "unitId null",
                        code = -1
                    ).left()
                )
            }
        }
    }

    override val isLoaded: Boolean
        get() = interstitialAd != null && unitId != null

    override fun show(activity: Activity, callback: InterstitialShowCallback?) {
        if (unitId != null && interstitialAd != null){
            val callbackMax = object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    callback?.onAdShowedCallback(AdNetwork.APPLOVIN)
                }

                override fun onAdHidden(p0: MaxAd) {
                    callback?.onDismissCallback(AdNetwork.APPLOVIN)
                }

                override fun onAdClicked(p0: MaxAd) {
                    callback?.onAdClicked()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError){
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    callback?.onAdFailedToShowCallback(p1.code.toString())
                }
            }
            interstitialAd?.setListener(callbackMax)
            if (interstitialAd?.isReady == true){
                interstitialAd?.showAd()
            }else{
                callback?.onDismissCallback(AdNetwork.APPLOVIN)
            }
        }else{
            callback?.onDismissCallback(AdNetwork.APPLOVIN)
        }
    }

    companion object {
        var TAG : String? = CemInterstitialManager.TAG
        @JvmStatic
        fun newInstance(adUnit: String?): ApplovinInterstitialAdManager {
            return ApplovinInterstitialAdManager(adUnit)
        }
    }
}