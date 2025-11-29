package com.cem.admodule.ads.mintegral

import android.app.Activity
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.left
import arrow.core.right
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.inter.InterstitialShowCallback
import com.mbridge.msdk.mbbid.out.BidListennning
import com.mbridge.msdk.mbbid.out.BidManager
import com.mbridge.msdk.mbbid.out.BidResponsed
import com.mbridge.msdk.newinterstitial.out.MBBidNewInterstitialHandler
import com.mbridge.msdk.newinterstitial.out.MBNewInterstitialHandler
import com.mbridge.msdk.newinterstitial.out.NewInterstitialListener
import com.mbridge.msdk.out.MBridgeIds
import com.mbridge.msdk.out.RewardInfo
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@MainThread
@ActivityScoped
class MintegralInterstitialAdManager @Inject constructor(
    private val adUnit: String?, private val placementId: String?
) : CemInterstitialAd {

    private var mMBInterstitialHandler: MBNewInterstitialHandler? = null
    private var mBidInterstitialHandler: MBBidNewInterstitialHandler? = null
    private var callbackLoadAd: InterstitialLoadCallback? = null
    private var callbackShowAd: InterstitialShowCallback? = null

    override fun load(activity: Activity, callback: InterstitialLoadCallback?): CemInterstitialAd {
        callbackLoadAd = callback
        if (adUnit != null && placementId != null) {
            var bidToken: String? = null
            val bidManager = BidManager(
                placementId, adUnit
            )
            bidManager.setBidListener(object : BidListennning {
                override fun onFailed(p0: String?) {
                    bidToken = null
                    Log.d(TAG, "onFailed: $p0")
                }

                override fun onSuccessed(p0: BidResponsed?) {
                    Log.d(TAG, "onSuccessed: ${p0?.bidId}")
                    bidToken = p0?.bidId
                }
            })
            bidManager.bid()
            (activity as AppCompatActivity).lifecycleScope.launch(start = CoroutineStart.DEFAULT) {
                Log.d(TAG, "load: $bidToken")
                if (bidToken != null) {
                    Log.d(TAG, "load: bidding")
                    loadAdBidding(activity, bidToken!!).map {
                        mBidInterstitialHandler = it
                    }.mapLeft {
                        mBidInterstitialHandler = null
                    }
                } else {
                    Log.d(TAG, "load: traditional")
                    loadAdTraditional(activity).map {
                        mMBInterstitialHandler = it
                    }.mapLeft {
                        mMBInterstitialHandler = null
                    }
                }
            }
        } else callbackLoadAd?.onAdFailedToLoaded(ErrorCode(message = "adUnit or placementId null"))
        return this
    }

    private suspend fun loadAdTraditional(activity: Activity) =
        suspendCancellableCoroutine { const ->
            mMBInterstitialHandler = MBNewInterstitialHandler(activity, placementId, adUnit)
            mMBInterstitialHandler?.setInterstitialVideoListener(object : NewInterstitialListener {
                override fun onLoadCampaignSuccess(p0: MBridgeIds?) {
                    Log.d(TAG, "onLoadCampaignSuccess: ")
                }

                override fun onResourceLoadSuccess(p0: MBridgeIds?) {
                    Log.d(TAG, "onResourceLoadSuccess: ")
                    const.resume(mMBInterstitialHandler.right())
                    callbackLoadAd?.onAdLoaded(this@MintegralInterstitialAdManager)
                }

                override fun onResourceLoadFail(p0: MBridgeIds?, p1: String?) {
                    Log.d(TAG, "onResourceLoadFail: ")
                    const.resume(p1.left())
                    callbackLoadAd?.onAdFailedToLoaded(ErrorCode(message = p1.toString()))
                }

                override fun onAdShow(p0: MBridgeIds?) {
                    callbackShowAd?.onAdShowedCallback(AdNetwork.MINTEGRAL)
                    Log.d(TAG, "onAdShow: ")
                }

                override fun onAdClose(p0: MBridgeIds?, p1: RewardInfo?) {
                    callbackShowAd?.onDismissCallback(AdNetwork.MINTEGRAL)
                    Log.d(TAG, "onAdClose: ")
                }

                override fun onShowFail(p0: MBridgeIds?, p1: String?) {
                    callbackShowAd?.onAdFailedToShowCallback(p1.toString())
                    Log.d(TAG, "onShowFail: ")
                }

                override fun onAdClicked(p0: MBridgeIds?) {
                    callbackShowAd?.onAdClicked()
                    Log.d(TAG, "onAdClicked: ")
                }

                override fun onVideoComplete(p0: MBridgeIds?) {
                    Log.d(TAG, "onVideoComplete: ")
                }

                override fun onAdCloseWithNIReward(p0: MBridgeIds?, p1: RewardInfo?) {
                    Log.d(TAG, "onAdCloseWithNIReward: ")
                }

                override fun onEndcardShow(p0: MBridgeIds?) {
                    Log.d(TAG, "onEndcardShow: ")
                }
            })
            mMBInterstitialHandler?.load()
        }

    private suspend fun loadAdBidding(activity: Activity, bidToken: String) =
        suspendCancellableCoroutine { const ->
            mBidInterstitialHandler = MBBidNewInterstitialHandler(activity, placementId, adUnit)
            mBidInterstitialHandler?.setInterstitialVideoListener(object : NewInterstitialListener {
                override fun onLoadCampaignSuccess(p0: MBridgeIds?) {
                    Log.d(TAG, "onLoadCampaignSuccess: ")
                }

                override fun onResourceLoadSuccess(p0: MBridgeIds?) {
                    Log.d(TAG, "onResourceLoadSuccess: ")
                    callbackLoadAd?.onAdLoaded(this@MintegralInterstitialAdManager)
                    const.resume(mBidInterstitialHandler.right())
                }

                override fun onResourceLoadFail(p0: MBridgeIds?, p1: String?) {
                    Log.d(TAG, "onResourceLoadFail: ")
                    callbackLoadAd?.onAdFailedToLoaded(ErrorCode(message = p1.toString()))
                    const.resume(p1.left())
                }

                override fun onAdShow(p0: MBridgeIds?) {
                    callbackShowAd?.onAdShowedCallback(AdNetwork.MINTEGRAL)
                    Log.d(TAG, "onAdShow: ")
                }

                override fun onAdClose(p0: MBridgeIds?, p1: RewardInfo?) {
                    callbackShowAd?.onDismissCallback(AdNetwork.MINTEGRAL)
                    Log.d(TAG, "onAdClose: ")
                }

                override fun onShowFail(p0: MBridgeIds?, p1: String?) {
                    callbackShowAd?.onAdFailedToShowCallback(p1.toString())
                    Log.d(TAG, "onShowFail: ")
                }

                override fun onAdClicked(p0: MBridgeIds?) {
                    callbackShowAd?.onAdClicked()
                    Log.d(TAG, "onAdClicked: ")
                }

                override fun onVideoComplete(p0: MBridgeIds?) {
                    Log.d(TAG, "onVideoComplete: ")
                }

                override fun onAdCloseWithNIReward(p0: MBridgeIds?, p1: RewardInfo?) {
                    Log.d(TAG, "onAdCloseWithNIReward: ")
                }

                override fun onEndcardShow(p0: MBridgeIds?) {
                    Log.d(TAG, "onEndcardShow: ")
                }
            })
            mBidInterstitialHandler?.loadFromBid(bidToken)
        }

    override val isLoaded: Boolean
        get() = mMBInterstitialHandler != null || mBidInterstitialHandler != null

    override fun show(activity: Activity, callback: InterstitialShowCallback?) {
        callbackShowAd = callback
        if (mBidInterstitialHandler != null) {
            if (mBidInterstitialHandler?.isBidReady == true) {
                mBidInterstitialHandler?.showFromBid()
            } else {
                callbackShowAd?.onDismissCallback(AdNetwork.MINTEGRAL)
            }
        } else if (mMBInterstitialHandler != null) {
            if (mMBInterstitialHandler?.isReady == true) {
                mMBInterstitialHandler?.show()
            } else {
                callbackShowAd?.onDismissCallback(AdNetwork.MINTEGRAL)
            }
        } else {
            callbackShowAd?.onDismissCallback(AdNetwork.MINTEGRAL)
        }
    }

    companion object {
        var TAG = MintegralInterstitialAdManager::class.java.simpleName
        @JvmStatic
        fun newInstance(adUnit: String?, placementId: String?): MintegralInterstitialAdManager {
            return MintegralInterstitialAdManager(adUnit, placementId)
        }
    }

}