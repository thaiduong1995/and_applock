package com.cem.admodule.ads.admob

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemBannerManager
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import javax.inject.Inject

class AdmobBannerAdManager @Inject constructor(
    private val adSize: AdSize?, private val adUnit: String?
) : BannerAdView {

    override fun createByActivity(
        activity: Activity, listener: BannerAdListener?, position: String?
    ): View? {
        return createByContext(activity, listener, position)
    }

    override fun createByContext(
        context: Context, listener: BannerAdListener?, position: String?
    ): View? {
        if (adUnit == null) return null
        val adView = AdView(context)
        adView.setAdSize(adSize ?: getAdSize(context))
        adView.adUnitId = adUnit.toString().trim()
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                listener?.onBannerLoaded(this@AdmobBannerAdManager, adView)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                listener?.onBannerFailed(p0.message)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                listener?.onBannerClicked()
            }

            override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
                Log.d(TAG, "onAdSwipeGestureClicked: ")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG, "onAdOpened: ")
                listener?.onBannerOpen()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG, "onAdClosed: ")
                listener?.onBannerClose()
            }
        }
        var extras : Bundle? = null
        if (position != null) {
            Log.d(TAG, "createByContext: $position")
            extras = Bundle()
            extras.putString("collapsible", position)
        }
        val request = AdRequest.Builder()
        if (extras != null) request.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        adView.loadAd(request.build())
        return adView
    }

    private fun getAdSize(context: Context): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val outMetrics: DisplayMetrics = context.resources.displayMetrics
        val widthPixels: Int = outMetrics.widthPixels
        val density: Float = outMetrics.density
        var adWidthPixels = widthPixels.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context, adWidth
        )
    }

    companion object{
        var TAG = CemBannerManager.TAG
        @JvmStatic
        fun newInstance(adSize: AdSize?,adUnit : String?) : AdmobBannerAdManager{
            return AdmobBannerAdManager(adSize,adUnit)
        }
    }
}