package com.cem.admodule.inter

import com.cem.admodule.data.RewardAdItem

abstract class CemRewardListener : RewardShowCallback, RewardItemCallback {

    private var item: RewardAdItem? = null
    abstract fun onRewardAdded(rewardAdItem: RewardAdItem?)
    abstract fun onRewardFail(error: String?)

    override fun onAdDismissedFullScreenContent() {
        if (item != null) {
            onRewardAdded(item)
        } else {
            onRewardFail("")
        }
    }

    override fun onAdFailedToShowFullScreenContent(error: String?) {
        onRewardFail(error)
    }

    override fun onAdShowedFullScreenContent() {}
    override fun onUserEarnedReward(rewardAdItem: RewardAdItem?) {
        item = rewardAdItem
    }

}