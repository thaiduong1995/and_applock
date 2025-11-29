package com.cem.admodule.ads.mintegral

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemBannerManager
import com.google.android.gms.ads.AdSize
import com.mbridge.msdk.mbbid.out.BannerBidRequestParams
import com.mbridge.msdk.mbbid.out.BidListennning
import com.mbridge.msdk.mbbid.out.BidManager
import com.mbridge.msdk.mbbid.out.BidResponsed
import com.mbridge.msdk.out.BannerSize
import com.mbridge.msdk.out.MBBannerView
import com.mbridge.msdk.out.MBridgeIds
import javax.inject.Inject

class MintegralBannerAdManager @Inject constructor(
    private val adSize: AdSize?, private val adUnit: String?, private val placementId: String?
) : BannerAdView {
    private val bannerSize: BannerSize
        get() {
            return when (adSize) {
                AdSize.BANNER -> BannerSize(BannerSize.DEV_SET_TYPE, 320, 100)
                AdSize.LARGE_BANNER -> BannerSize(BannerSize.DEV_SET_TYPE, 320, 100)
                AdSize.MEDIUM_RECTANGLE -> BannerSize(BannerSize.DEV_SET_TYPE, 320, 250)
                AdSize.LEADERBOARD -> BannerSize(BannerSize.DEV_SET_TYPE, 728, 120)
                AdSize.FULL_BANNER -> BannerSize(BannerSize.DEV_SET_TYPE, 468, 120)
                else -> BannerSize(BannerSize.DEV_SET_TYPE, 320, 100)
            }
        }


    override fun createByActivity(
        activity: Activity, listener: BannerAdListener?, position: String?
    ): View? {
        return createByContext(context = activity, listener, position)
    }

    override fun createByContext(
        context: Context, listener: BannerAdListener?, position: String?
    ): View? {
        if (adUnit == null || placementId == null) return null
        var bidToken: String? = null
        val bidManager = BidManager(
            BannerBidRequestParams(
                placementId, adUnit, bannerSize.width, bannerSize.height
            )
        )
        bidManager.setBidListener(object : BidListennning {
            override fun onFailed(p0: String?) {
                Log.d(TAG, "onFailed: $p0")
                bidToken = null
            }

            override fun onSuccessed(p0: BidResponsed?) {
                Log.d(TAG, "onSuccessed: ${p0?.bidId}")
                bidToken = p0?.bidId
            }
        })
        bidManager.bid()
        val mbBannerAdView = MBBannerView(context)
        mbBannerAdView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, bannerSize.height
        )
        mbBannerAdView.init(bannerSize, placementId, adUnit)
        mbBannerAdView.setBannerAdListener(object : com.mbridge.msdk.out.BannerAdListener {
            override fun onLoadFailed(p0: MBridgeIds?, p1: String?) {
                listener?.onBannerFailed(p1)
                Log.d(TAG, "onLoadFailed: $p1")
            }

            override fun onLoadSuccessed(p0: MBridgeIds?) {
                listener?.onBannerLoaded(this@MintegralBannerAdManager, mbBannerAdView)
                Log.d(TAG, "onLoadSuccessed: ")
            }

            override fun onLogImpression(p0: MBridgeIds?) {
                Log.d(TAG, "onLogImpression:")
            }

            override fun onClick(p0: MBridgeIds?) {
                Log.d(TAG, "onClick: ")
                listener?.onBannerClicked()
            }

            override fun onLeaveApp(p0: MBridgeIds?) {
                Log.d(TAG, "onLeaveApp: ")
            }

            override fun showFullScreen(p0: MBridgeIds?) {
                Log.d(TAG, "showFullScreen: ")
            }

            override fun closeFullScreen(p0: MBridgeIds?) {
                Log.d(TAG, "closeFullScreen: ")
            }

            override fun onCloseBanner(p0: MBridgeIds?) {
                Log.d(TAG, "onCloseBanner: ")
            }
        })
        if (bidToken == null) {
            mbBannerAdView.load()
        } else mbBannerAdView.loadFromBid(bidToken)
        return mbBannerAdView
    }

    companion object {
        var TAG: String = CemBannerManager.TAG

        @JvmStatic
        fun newInstance(
            adSize: AdSize?,
            adUnit: String?,
            placementId: String?
        ): MintegralBannerAdManager {
            return MintegralBannerAdManager(adSize, adUnit, placementId)
        }
    }
}