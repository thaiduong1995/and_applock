package com.cem.admodule.inter

import com.cem.admodule.data.RewardAdItem

interface RewardItemCallback {
    fun onUserEarnedReward(rewardAdItem: RewardAdItem?)
}