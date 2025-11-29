package com.cem.admodule.ads.applovin

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.cem.admodule.R
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.google.android.gms.ads.AdSize
import javax.inject.Inject

class ApplovinBannerAdManager @Inject constructor(
    private val adUnit: String?
) : BannerAdView {
    override fun createByActivity(
        activity: Activity, listener: BannerAdListener?, position: String?
    ): View? {
        return createByContext(
            context = activity, listener = listener, position = position
        )
    }

    override fun createByContext(
        context: Context, listener: BannerAdListener?, position: String?
    ): View? {
        if (adUnit == null) return null
        val adView = MaxAdView(adUnit, context)
        val callbackAdView = object : MaxAdViewAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                listener?.onBannerLoaded(this@ApplovinBannerAdManager, adView)
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
                listener?.onBannerClicked()
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                listener?.onBannerFailed(p0)
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                listener?.onBannerFailed(p1.message)
            }

            override fun onAdExpanded(p0: MaxAd) {
            }

            override fun onAdCollapsed(p0: MaxAd) {
            }

        }
        adView.setListener(callbackAdView)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        if (position != null) {
            val adSize = getAdSize(context)
            // Get the adaptive banner height.
            val heightDp = MaxAdFormat.BANNER.getAdaptiveSize(adSize.height, context).height
            val heightPx = AppLovinSdkUtils.dpToPx(context, heightDp)
            adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
            adView.setExtraParameter("adaptive_banner", "true")
            adView.setLocalExtraParameter("adaptive_banner_width", adSize.width)
            adView.adFormat.getAdaptiveSize(adSize.height, context).height
        } else {
            val heightPx = context.resources.getDimensionPixelSize(R.dimen.banner_height)
            adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        }
        adView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        adView.loadAd()
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

    companion object {
        @JvmStatic
        fun newInstance(adUnit: String?): ApplovinBannerAdManager {
            return ApplovinBannerAdManager(adUnit)
        }
    }
}