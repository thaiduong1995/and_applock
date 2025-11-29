package com.cem.admodule.ads.admob

import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.left
import arrow.core.right
import com.cem.admodule.data.RewardAdItem
import com.cem.admodule.inter.CemRewardAd
import com.cem.admodule.inter.RewardItemCallback
import com.cem.admodule.inter.RewardLoadCallback
import com.cem.admodule.inter.RewardShowCallback
import com.cem.admodule.manager.CemRewardAdManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class AdmobRewardAdManager @Inject constructor(
    private val adUnit: String?
) : CemRewardAd {
    private var rewardAd: RewardedAd? = null
    override fun load(activity: Activity, callback: RewardLoadCallback?): CemRewardAd {
        (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
            loadAdInternal(activity).map {item ->
                Log.d(TAG, "load: onLoaded $adUnit")
                rewardAd = item
                callback?.onLoaded(this@AdmobRewardAdManager)
            }.mapLeft {
                rewardAd = null
                Log.d(TAG, "load: onLoadedFailed $adUnit")
                callback?.onLoadedFailed(it)
            }
        }
        return this
    }

    private suspend fun loadAdInternal(activity: Activity) = suspendCancellableCoroutine { cont ->
        if (adUnit != null) {
            RewardedAd.load(activity,
                adUnit,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) =
                        cont.resume(error.message.left())

                    override fun onAdLoaded(ad: RewardedAd) = cont.resume(ad.right())
                })
        }
    }

    override val isLoaded: Boolean
        get() = rewardAd != null || adUnit != null

    override fun show(
        activity: Activity, callback: RewardShowCallback?, callbackItem: RewardItemCallback?
    ) {
        if (adUnit != null && rewardAd != null) {
            Log.d(TAG, "show: adUnit and reward khác null")
            rewardAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    callback?.onAdFailedToShowFullScreenContent(p0.message)
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    callback?.onAdDismissedFullScreenContent()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    callback?.onAdShowedFullScreenContent()
                }
            }
            rewardAd?.show(activity) { rewardItem: RewardItem ->
                callbackItem?.onUserEarnedReward(
                    RewardAdItem(rewardItem.type, rewardItem.amount)
                )
            }
        }else{
            Log.d(TAG, "show: adUnit and reward  null")
            callback?.onAdFailedToShowFullScreenContent("No ads loading success")
        }
    }

    companion object {
        val TAG = CemRewardAdManager.TAG

        @JvmStatic
        fun newInstance(adUnit: String?): AdmobRewardAdManager {
            return AdmobRewardAdManager(adUnit)
        }
    }
}