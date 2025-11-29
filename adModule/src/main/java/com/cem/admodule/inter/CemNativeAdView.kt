package com.cem.admodule.inter

import android.content.Context
import com.cem.admodule.R
import com.cem.admodule.manager.CustomNativeView

interface CemNativeAdView {
    fun load(context : Context, callback : NativeAdCallback?) : CemNativeAdView
    fun show(view : CustomNativeView, layoutRes: Int = R.layout.admob_native_ad_view)

    val isLoaded : Boolean
}