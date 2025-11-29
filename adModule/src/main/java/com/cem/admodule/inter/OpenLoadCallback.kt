package com.cem.admodule.inter

interface OpenLoadCallback {
    fun onAdLoaded(cemOpenAd: CemOpenAd?)
    fun onAdFailedToLoaded(error : Exception)
}