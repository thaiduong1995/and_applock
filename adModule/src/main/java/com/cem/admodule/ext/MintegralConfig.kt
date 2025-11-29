package com.cem.admodule.ext

import android.app.Application
import android.util.Log
import com.cem.admodule.data.Configuration
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.MBridgeSDK
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.mbridge.msdk.out.SDKInitStatusListener

object MintegralConfig {
    @JvmStatic
    fun initialize(
        app: Application,
        configuration: Configuration
    ) {
        //add privacy settings on Mintegral SDK
        val mBridgeSDK = MBridgeSDKFactory.getMBridgeSDK()
        mBridgeSDK.setConsentStatus(app, MBridgeConstans.IS_SWITCH_ON)
        mBridgeSDK.setDoNotTrackStatus(false)

        if (configuration.mintegralConfig != null) {
            val data = configuration.mintegralConfig ?: return
            Log.d("MintegralConfig", "initialize: $data")
            val map = mBridgeSDK.getMBConfigurationMap(data.appId, data.appKey)
            mBridgeSDK.init(map, app, object : SDKInitStatusListener {
                override fun onInitSuccess() {
                    Log.d("MintegralConfig", "onInitSuccess: ")
                }

                override fun onInitFail(p0: String?) {
                    Log.d("MintegralConfig", "onInitFail: $p0")

                }

            })
        }
    }
}