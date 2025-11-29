package com.suntech.mytools.mytools.datalocal

import com.suntech.mytools.mytools.AppAdmob

object RemoteManager {

    private fun getValue(key: String): Boolean {
        return AppAdmob.remoteConfigViewModel.remoteConfig?.getBoolean(key) ?: true
    }

    val openApp: Boolean
        get() {
            return getValue("Open_App")
        }
    val interSplash: Boolean
        get() {
            return getValue("Inter_Splash")
        }
    val interSwitch: Boolean
        get() {
            return getValue("Inter_Switch")
        }
    val interSetup: Boolean
        get() {
            return getValue("Inter_Setup")
        }
    val nativeChooseApp: Boolean
        get() {
            return getValue("Native_Choose_App")
        }
    val bannerForgotPattern: Boolean
        get() {
            return getValue("Banner_Forgot_Pattern")
        }
    val interSearch: Boolean
        get() {
            return getValue("Inter_Search")
        }
    val interLock: Boolean
        get() {
            return getValue("Inter_Lock")
        }
    val nativeLock: Boolean
        get() {
            return getValue("Native_Lock")
        }
    val interUnLock: Boolean
        get() {
            return getValue("Inter_Unlock")
        }
    val nativeSecurity: Boolean
        get() {
            return getValue("Native_Security")
        }
    val bannerChangePattern: Boolean
        get() {
            return getValue("Banner_Change_Pattern")
        }
    val interChangePattern: Boolean
        get() {
            return getValue("Inter_Change_Pattern")
        }
    val bannerTimeBased: Boolean
        get() {
            return getValue("Banner_Time_Based")
        }
    val bannerHistory: Boolean
        get() {
            return getValue("Banner_History")
        }
    val bannerBookmark: Boolean
        get() {
            return getValue("Banner_Bookmark")
        }
    val bannerLocationBased: Boolean
        get() {
            return getValue("Banner_Location_Based")
        }
    val bannerWifiBased: Boolean
        get() {
            return getValue("Banner_Wifi_Based")
        }
    val nativeCaptureIntruder: Boolean
        get() {
            return getValue("Native_Capture_Intruder")
        }
    val bannerIntruderRecord: Boolean
        get() {
            return getValue("Banner_Intruder_Record")
        }
    val rewardIntruderRecord: Boolean
        get() {
            return getValue("Reward_Intruder_Record")
        }
    val bannerIntruderDetail: Boolean
        get() {
            return getValue("Banner_Intruder_Detail")
        }
    val bannerFakeIcon: Boolean
        get() {
            return getValue("Banner_Fake_Icon")
        }
    val interstitialFakeIcon: Boolean
        get() {
            return getValue("Interstitial_Fake_Icon")
        }
    val rewardFakeIcon: Boolean
        get() {
            return getValue("Reward_Fake_Icon")
        }
    val bannerFakeCleanser: Boolean
        get() {
            return getValue("Banner_Fake_Cleanser")
        }
    val interCleanser: Boolean
        get() {
            return getValue("Inter_Cleanser")
        }
}