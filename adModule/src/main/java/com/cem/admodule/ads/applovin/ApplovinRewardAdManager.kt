package com.cem.admodule.ads.applovin

import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.const
import arrow.core.left
import arrow.core.right
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.cem.admodule.ads.admob.AdmobRewardAdManager
import com.cem.admodule.data.RewardAdItem
import com.cem.admodule.inter.CemRewardAd
import com.cem.admodule.inter.RewardItemCallback
import com.cem.admodule.inter.RewardLoadCallback
import com.cem.admodule.inter.RewardShowCallback
import com.cem.admodule.manager.CemRewardAdManager
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class ApplovinRewardAdManager @Inject constructor(
    private val adUnitId: String?
) : CemRewardAd {

    private var rewardAd: MaxRewardedAd? = null
    override fun load(activity: Activity, callback: RewardLoadCallback?): CemRewardAd {
        (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
            loadAdInternal(activity).map {
                Log.d(TAG, "load: onLoaded $adUnitId")
                rewardAd = it
                callback?.onLoaded(this@ApplovinRewardAdManager)
            }.mapLeft {
                Log.d(TAG, "load: onLoadedFailed $adUnitId")
                rewardAd = null
                callback?.onLoadedFailed(it)
            }
        }
        return this
    }

    private suspend fun loadAdInternal(activity: Activity) = suspendCancellableCoroutine { const ->
        if (adUnitId != null) {
            rewardAd = MaxRewardedAd.getInstance(adUnitId, activity)
            rewardAd?.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    if (const.isActive) const.resume(rewardAd.right())
                }

                override fun onAdDisplayed(p0: MaxAd) {
                }

                override fun onAdHidden(p0: MaxAd) {
                }

                override fun onAdClicked(p0: MaxAd) {
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    if (const.isActive) const.resume(p0.left())
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                }

                override fun onRewardedVideoStarted(p0: MaxAd) {
                }

                override fun onRewardedVideoCompleted(p0: MaxAd) {
                }
            })
            rewardAd?.loadAd()
        } else {
            const.resume("adUnit null".left())
        }
    }

    override val isLoaded: Boolean
        get() = adUnitId != null && rewardAd != null

    override fun show(
        activity: Activity, callback: RewardShowCallback?, callbackItem: RewardItemCallback?
    ) {
        if (rewardAd != null && adUnitId != null) {
            Log.d(TAG, "show: adUnit and reward  != null")
            rewardAd?.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    callback?.onAdShowedFullScreenContent()
                }

                override fun onAdHidden(p0: MaxAd) {
                    callback?.onAdDismissedFullScreenContent()
                }

                override fun onAdClicked(p0: MaxAd) {
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    callback?.onAdFailedToShowFullScreenContent(p1.message)
                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    callbackItem?.onUserEarnedReward(
                        RewardAdItem(type = p1.label, amount = p1.amount)
                    )
                }

                override fun onRewardedVideoStarted(p0: MaxAd) {
                }

                override fun onRewardedVideoCompleted(p0: MaxAd) {
                }
            })
            if (rewardAd?.isReady == true) {
                rewardAd?.showAd()
            } else {
                callback?.onAdDismissedFullScreenContent()
            }
        } else {
            Log.d(TAG, "show: adUnit and reward  null")
            callback?.onAdFailedToShowFullScreenContent("No ads loading success")
        }
    }

    companion object {
        val TAG = CemRewardAdManager.TAG

        @JvmStatic
        fun newInstance(adUnit: String?): ApplovinRewardAdManager {
            return ApplovinRewardAdManager(adUnit)
        }
    }
}