package com.cem.admodule.inter

import com.cem.admodule.data.ErrorCode

interface InterstitialLoadCallback {
    fun onAdLoaded(cemInterstitialAd : CemInterstitialAd?)
    fun onAdFailedToLoaded(error : ErrorCode)
}