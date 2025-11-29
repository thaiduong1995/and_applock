package com.suntech.mytools.mytools

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.FirebaseApp
import com.suntech.mytools.RemoteConfigViewModel
import com.suntech.mytools.mytools.open.AppOpenManager
import com.tencent.mmkv.MMKV

open class AppAdmob : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    var currentActivity: Activity? = null
    var appOpenManager: AppOpenManager? = null
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        initMMKV()
        FirebaseApp.initializeApp(this)
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        //init firebase remote config
        initFirebaseRemoteConfig()
    }

    private fun initMMKV() {
        MMKV.initialize(this)
        dataStore = MMKV.defaultMMKV()
    }


    private fun initFirebaseRemoteConfig() {
        remoteConfigViewModel = RemoteConfigViewModel()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }


    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    companion object {
        @JvmStatic
        var interstitialAd: InterstitialAd? = null

        @JvmStatic
        var isShowing: Boolean = false

        @JvmStatic
        var isOpen: Boolean = false

        @JvmStatic
        var listNativeAd = mutableListOf<NativeAd>()

        @JvmField
        var lastTimeShowAdsFull = 0L

        lateinit var remoteConfigViewModel: RemoteConfigViewModel

        lateinit var appContext: Context

        lateinit var dataStore: MMKV
    }
}