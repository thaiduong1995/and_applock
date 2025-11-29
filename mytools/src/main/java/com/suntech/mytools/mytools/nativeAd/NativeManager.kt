package com.suntech.mytools.mytools.nativeAd

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.suntech.mytools.BuildConfig
import com.suntech.mytools.R
import com.suntech.mytools.mytools.AppAdmob.Companion.listNativeAd
import com.suntech.mytools.mytools.Constants
import com.suntech.mytools.mytools.datalocal.DataLocal
import com.suntech.mytools.tools.NetworkUtils

object NativeManager {

    var TAG = "NativeManager"
    private var indexNative = 0

    fun resetNative() {
        listNativeAd.clear()
        indexNative = 0
    }

    fun createNativesAds(
        activity: Activity,
        currentIndex: Int = 0,
        onLoaded: ((nativeAd: NativeAd) -> Unit)? = null,
        onLoadFailed: (() -> Unit)? = null
    ) {
        if (listNativeAd.size >= 4) {
            getNative()?.let {
                onLoaded?.invoke(it)
            }
            return
        }
        if (!DataLocal.isVip() && NetworkUtils.isNetworkConnected(activity)) {
            var countIndex = currentIndex
            val adUnitId = if (BuildConfig.DEBUG) {
                Constants.ID_NATIVE
            } else DataLocal.listKeyNative()[countIndex]?.getIdNative().toString()

            val builder: AdLoader.Builder = AdLoader.Builder(activity, adUnitId)
            builder.forNativeAd {
                onLoaded?.invoke(it)
                listNativeAd.add(it)
            }
            builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    countIndex += 1
                    if (countIndex < DataLocal.listKeyNative().size) {
                        createNativesAds(activity = activity, currentIndex = countIndex)
                    } else {
                        onLoadFailed?.invoke()
                    }
                    Log.d(TAG, "onAdFailedToLoad: ${p0.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "onAdLoaded Native")
                }
            })
            builder.withNativeAdOptions(
                NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            val adLoad = builder.build()
            adLoad.loadAd(AdRequest.Builder().build())
        }else{
            onLoadFailed?.invoke()
        }
    }


    fun getNative(): NativeAd? {
        val mNativeAd: NativeAd?
        return if (!DataLocal.isVip()) {
            if (listNativeAd.isNotEmpty()) {
                if (indexNative >= 0 && indexNative < listNativeAd.size) {
                    mNativeAd = listNativeAd[indexNative]
                    indexNative += 1
                } else {
                    indexNative = 0
                    mNativeAd = listNativeAd[indexNative]
                }
                mNativeAd
            } else null
        } else null
    }

    fun showNative(context: Context, native: NativeAd): NativeAdView {
        val viewNative = LayoutInflater.from(context)
            .inflate(R.layout.item_native_small_new, null) as NativeAdView
        populateNativeAdView(native, viewNative)
        return viewNative
    }


    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

        adView.headlineView = adView.findViewById(R.id.primary)
        adView.callToActionView = adView.findViewById(R.id.cta)
        adView.iconView = adView.findViewById(R.id.icon)
        adView.bodyView = adView.findViewById(R.id.body)
        adView.starRatingView = adView.findViewById(R.id.rating_bar)
        adView.storeView = adView.findViewById(R.id.secondary)
        adView.imageView = adView.findViewById(R.id.image)

        //name app
        (adView.headlineView as TextView).text = nativeAd.headline

        //Button or text field that encourages user to take action (e.g., Install or Visit Site).
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
            (adView.callToActionView as Button).isAllCaps = false
        }
        //Small icon image (e.g., app store image or advertiser logo).
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
            adView.imageView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            (adView.imageView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
            adView.imageView?.visibility = View.VISIBLE
        }

        //Secondary body text (e.g., app description or article description).
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        //Rating from 0-5 that represents the average rating of the app in a store.
        if (nativeAd.starRating != null) {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }
        //The app store where the user downloads the app.
        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.GONE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        adView.setNativeAd(nativeAd)
    }
}
