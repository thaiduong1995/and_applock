package com.suntech.mytools.mytools.reward

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.suntech.mytools.BuildConfig
import com.suntech.mytools.modell.ConfigReward
import com.suntech.mytools.mytools.AppAdmob
import com.suntech.mytools.mytools.Constants
import com.suntech.mytools.mytools.datalocal.DataLocal
import com.suntech.mytools.tools.NetworkUtils

object RewardedAdUtils {

    var rewardedAdAdmob: RewardedAd? = null
    var TAG = "RewardedAdUtils"

    fun loadRewardedAd(
        context: Activity,
        onLoaded: (() -> Unit)? = null,
        onFailed: (() -> Unit)? = null,
        countIndex: Int = 0,
    ) {
        if (DataLocal.isVip()) {
            onFailed?.invoke()
            return
        }

        if (NetworkUtils.isNetworkConnected(context)) {
            if (rewardedAdAdmob == null) {
                var countCurrentIndex = countIndex
                if (countCurrentIndex >= getListKeyReward().size) {
                    onFailed?.invoke()
                    return
                }
                val adRequest = AdRequest.Builder().build()
                val idRewardedAd = if (BuildConfig.DEBUG) {
                    Constants.ID_REWARD
                } else getListKeyReward()[countCurrentIndex]?.idReward.toString()
                RewardedAd.load(context,
                    idRewardedAd,
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            countCurrentIndex += 1
                            if (countCurrentIndex >= getListKeyReward().size) {
                                onFailed?.invoke()
                            } else {
                                loadRewardedAd(context, onLoaded, onFailed, countCurrentIndex)
                            }
                        }

                        override fun onAdLoaded(p0: RewardedAd) {
                            rewardedAdAdmob = p0
                            onLoaded?.invoke()
                        }
                    })
            }
        }
    }

    fun showRewardAd(
        context: Activity?,
        isShowReward: Boolean = true,
        onDismiss: (() -> Unit)? = null,
        onUserEarnedRewardListener: ((rewardItem: RewardItem) -> Unit)? = null,
    ) {
        if (context == null) {
            onDismiss?.invoke()
            return
        }

        if (DataLocal.isVip()) {
            onDismiss?.invoke()
            return
        }

        if (!isShowReward) {
            onDismiss?.invoke()
            return
        }

        if (rewardedAdAdmob != null) {
            rewardedAdAdmob?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAdAdmob = null
                    onDismiss?.invoke()
                    AppAdmob.isShowing = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onDismiss?.invoke()
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: ${p0.message}")
                    rewardedAdAdmob = null
                    AppAdmob.isShowing = false
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    AppAdmob.isShowing = true
                }
            }
            rewardedAdAdmob?.show(context) {
                onUserEarnedRewardListener?.invoke(it)
            }
        } else {
            loadRewardedAd(context)
            onDismiss?.invoke()
        }
    }

    private fun getListKeyReward(): List<ConfigReward?> {
        return if (AppAdmob.remoteConfigViewModel.remoteConfigAdmob.isNotEmpty()) {
            AppAdmob.remoteConfigViewModel.remoteConfigAdmob.first().getConfigReward()
                ?: listKeyRewardLocal()
        } else listKeyRewardLocal()
    }

    private fun listKeyRewardLocal(): List<ConfigReward> {
        return mutableListOf(
            ConfigReward(idReward = "ca-app-pub-3940256099942544/5224354917"),
            ConfigReward(idReward = "ca-app-pub-3940256099942544/5224354917"),
            ConfigReward(idReward = "ca-app-pub-3940256099942544/5224354917")
        )
    }
}