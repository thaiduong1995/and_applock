package com.cem.admodule.inter

import android.app.Activity

interface CemInterstitialAd {
    fun load(activity: Activity,callback: InterstitialLoadCallback?): CemInterstitialAd?
    val isLoaded : Boolean
    fun show(activity: Activity,callback: InterstitialShowCallback?)
}