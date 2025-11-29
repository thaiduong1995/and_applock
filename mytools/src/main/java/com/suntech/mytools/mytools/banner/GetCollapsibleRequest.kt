package com.suntech.mytools.mytools.banner

import android.os.Bundle
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest

object GetCollapsibleRequest {
    fun getCollapsibleRequest(): AdRequest.Builder {
        val builder: AdRequest.Builder = AdRequest.Builder()
        val extras = Bundle()
        extras.putString("collapsible", "bottom")
        builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        return builder
    }
}