package com.cem.admodule.inter

import android.app.Activity

interface CemRewardAd {
    fun load(activity: Activity, callback: RewardLoadCallback?): CemRewardAd?
    val isLoaded: Boolean
    fun show(activity: Activity, callback: RewardShowCallback?, callbackItem: RewardItemCallback?)
}