package com.cem.admodule.ads.admob


import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.left
import arrow.core.right
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.manager.CemInterstitialManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class AdmobInterstitialAdManager @Inject constructor(
    private val unitId: String?
) : CemInterstitialAd {
    private var interstitialAd: InterstitialAd? = null
    private var callbackLoadAd: InterstitialLoadCallback? = null
    private var callbackShowAd: InterstitialShowCallback? = null


        override fun load(activity: Activity, callback: InterstitialLoadCallback?): CemInterstitialAd {
        (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
            callbackLoadAd = callback
            loadAdInternal(activity).map {
                Log.d(TAG, "load: onLoaded $unitId")
                interstitialAd = it
                callbackLoadAd?.onAdLoaded(this@AdmobInterstitialAdManager)
            }.mapLeft {
                Log.d(TAG, "load: onLoadFailed $unitId")
                interstitialAd = null
                callbackLoadAd?.onAdFailedToLoaded(it)
            }
        }
        return this
    }

    override val isLoaded: Boolean
        get() = interstitialAd != null || unitId != null


    private suspend fun loadAdInternal(activity: Activity) = suspendCancellableCoroutine { cont ->
        if (unitId != null) {
            InterstitialAd.load(activity,
                unitId.toString().trim(),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        if (cont.isActive) {
                            cont.resume(
                                ErrorCode(
                                    message = error.message,
                                    code = error.code
                                ).left()
                            )
                        }
                    }


                    override fun onAdLoaded(ad: InterstitialAd) {
                        if (cont.isActive) {
                            cont.resume(ad.right())
                        }
                    }
                })
        }else{
            if (cont.isActive) {
                cont.resume(
                    ErrorCode(
                        message = "unitId null",
                        code = -1
                    ).left()
                )
            }
        }
    }

    override fun show(activity: Activity, callback: InterstitialShowCallback?) {
        if (unitId != null && interstitialAd != null) {
            this.callbackShowAd = callback
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d(TAG, "onAdDismissedFullScreenContent:$unitId")
                    callbackShowAd?.onDismissCallback(AdNetwork.ADMOB)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Log.d(TAG, "onAdShowedFullScreenContent:$unitId")
                    callbackShowAd?.onAdShowedCallback(AdNetwork.ADMOB)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Log.d(TAG, "onAdFailedToShowFullScreenContent:$unitId")
                    callbackShowAd?.onAdFailedToShowCallback(p0.code.toString())
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    callbackShowAd?.onAdClicked()
                }
            }
            interstitialAd?.show(activity)
        } else {
            callbackShowAd?.onDismissCallback(AdNetwork.ADMOB)
        }
    }

    companion object {
        private var TAG = CemInterstitialManager.TAG

        @JvmStatic
        fun newInstance(adUnit: String?): AdmobInterstitialAdManager {
            return AdmobInterstitialAdManager(adUnit)
        }
    }
}