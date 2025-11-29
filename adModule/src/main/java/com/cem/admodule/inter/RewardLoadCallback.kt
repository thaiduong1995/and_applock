package com.cem.admodule.inter

interface RewardLoadCallback {
    fun onLoaded(rewardAds : CemRewardAd?)
    fun onLoadedFailed(error: String?)
}