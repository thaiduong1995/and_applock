package com.cem.admodule.inter

import android.app.Activity
import android.content.Context
import android.view.View

interface BannerAdView {

    fun createByActivity(
        activity: Activity,
        listener: BannerAdListener? = null,
        position: String? = null
    ): View?

    fun createByContext(
        context : Context,
        listener: BannerAdListener? = null,
        position : String? = null
    ) : View?

}