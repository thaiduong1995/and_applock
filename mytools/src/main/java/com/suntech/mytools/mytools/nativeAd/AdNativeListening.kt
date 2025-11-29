package com.suntech.mytools.mytools.nativeAd

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.nativead.NativeAd

abstract class AdNativeListening : AdListener(), NativeAd.OnNativeAdLoadedListener{
    private var adLoader: AdLoader? = null

    open fun getAdLoader(): AdLoader? {
        return adLoader
    }

    open fun setAdLoader(adLoader: AdLoader?) {
        this.adLoader = adLoader
    }
}