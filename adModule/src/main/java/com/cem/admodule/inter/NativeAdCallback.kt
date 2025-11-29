package com.cem.admodule.inter

interface NativeAdCallback {
    fun onNativeLoaded(view: CemNativeAdView)
    fun onNativeFailed(errorCode: String?)
}