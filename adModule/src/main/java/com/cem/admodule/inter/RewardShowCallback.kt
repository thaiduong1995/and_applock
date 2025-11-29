package com.cem.admodule.inter

interface RewardShowCallback {
    fun onAdFailedToShowFullScreenContent(error: String?)
    fun onAdShowedFullScreenContent()
    fun onAdDismissedFullScreenContent()
}