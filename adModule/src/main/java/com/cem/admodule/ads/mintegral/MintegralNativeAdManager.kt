//package com.cem.admodule.ads.mintegral
//
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import com.cem.admodule.R
//import com.cem.admodule.inter.CemNativeAdView
//import com.cem.admodule.inter.NativeAdCallback
//import com.cem.admodule.manager.CemNativeManager
//import com.cem.and_emoji_merge.ui.custom_view.CustomNativeView
//import com.mbridge.msdk.mbbid.out.BidListennning
//import com.mbridge.msdk.mbbid.out.BidManager
//import com.mbridge.msdk.mbbid.out.BidResponsed
//import com.mbridge.msdk.out.MBNativeAdvancedHandler
//import com.mbridge.msdk.out.MBridgeIds
//import com.mbridge.msdk.out.NativeAdvancedAdListener
//import javax.inject.Inject
//
//class MintegralNativeAdManager @Inject constructor(
//    private val adUnit: String?, private val placementId: String?
//) : CemNativeAdView {
//
//    private var mbNativeAdvancedHandler: MBNativeAdvancedHandler? = null
//    override fun load(context: Context, callback: NativeAdCallback?): CemNativeAdView {
//        var bidToken: String? = null
//        val bidManager = BidManager(
//            placementId, adUnit
//        )
//        bidManager.setBidListener(object : BidListennning {
//            override fun onFailed(p0: String?) {
//                bidToken = null
//                Log.d(MintegralInterstitialAdManager.TAG, "onFailed: $p0")
//            }
//
//            override fun onSuccessed(p0: BidResponsed?) {
//                Log.d(MintegralInterstitialAdManager.TAG, "onSuccessed: ${p0?.bidId}")
//                bidToken = p0?.bidId
//            }
//        })
//        bidManager.bid()
//        Log.d(TAG, "load: cos vào ko")
//        (context as Activity).let {
//            Log.d(TAG, "load: convert đc")
//            mbNativeAdvancedHandler = MBNativeAdvancedHandler(it, placementId, adUnit)
////            mbNativeAdvancedHandler?.setAdListener(object : NativeAdvancedAdListener {
////                override fun onLoadFailed(p0: MBridgeIds?, p1: String?) {
////                    callback?.onNativeFailed(p1.toString())
////                    Log.d(TAG, "onLoadFailed: ${p1.toString()}")
////                }
////
////                override fun onLoadSuccessed(p0: MBridgeIds?) {
////                    callback?.onNativeLoaded(this@MintegralNativeAdManager)
////                }
////
////                override fun onLogImpression(p0: MBridgeIds?) {
////                    Log.d(TAG, "onLogImpression: ")
////                }
////
////                override fun onClick(p0: MBridgeIds?) {
////                    Log.d(TAG, "onClick: ")
////                }
////
////                override fun onLeaveApp(p0: MBridgeIds?) {
////                    Log.d(TAG, "onLeaveApp: ")
////                }
////
////                override fun showFullScreen(p0: MBridgeIds?) {
////                    Log.d(TAG, "showFullScreen: ")
////                }
////
////                override fun closeFullScreen(p0: MBridgeIds?) {
////                    Log.d(TAG, "closeFullScreen: ")
////                }
////
////                override fun onClose(p0: MBridgeIds?) {
////                    Log.d(TAG, "onClose: ")
////                }
////            })
//            if (bidToken != null) {
//                mbNativeAdvancedHandler?.loadByToken(bidToken)
//            } else {
//                mbNativeAdvancedHandler?.load()
//            }
//        }
//        return this
//    }
//
//    override fun show(view: CustomNativeView) {
//        try {
//            view.setTemplateType(R.layout.native_view_ads)
//            if (mbNativeAdvancedHandler != null) {
//                Log.d(TAG, "show: vào show")
////                val advancedNativeAdView = mbNativeAdvancedHandler?.adViewGroup
////                advancedNativeAdView?.let {
////                    view.bindingLayout.root.removeAllViews()
////                    view.bindingLayout.root.addView(it)
////                }
//            }
//        } catch (e: Exception) {
//            Log.d(TAG, "show: ${e.message.toString()}")
//            e.printStackTrace()
//        }
//    }
//
//    override val isLoaded: Boolean
//        get() = mbNativeAdvancedHandler != null
//
//    companion object {
//        val TAG = CemNativeManager.TAG
//
//        @JvmStatic
//        fun newInstance(adUnit: String?, placementId: String?): MintegralNativeAdManager {
//            return MintegralNativeAdManager(adUnit, placementId)
//        }
//    }
//}