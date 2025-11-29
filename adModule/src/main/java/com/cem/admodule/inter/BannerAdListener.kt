package com.cem.admodule.inter

import android.view.View

interface BannerAdListener {
    fun onBannerLoaded(banner : BannerAdView, view : View)
    fun onBannerFailed(error : String?)
    fun onBannerClicked()
    fun onBannerOpen()
    fun onBannerClose()
}