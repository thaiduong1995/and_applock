package com.suntech.mytools.mytools.open

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.suntech.mytools.BuildConfig
import com.suntech.mytools.mytools.AppAdmob
import com.suntech.mytools.mytools.Constants
import com.suntech.mytools.mytools.datalocal.DataLocal
import com.suntech.mytools.mytools.datalocal.RemoteManager
import com.suntech.mytools.mytools.interstitial.AdsFullManager
import com.suntech.mytools.tools.NetworkUtils
import java.util.*

class AppOpenManager(private val myAppAdmob: AppAdmob) : ActivityLifecycleCallbacks,
    LifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var countIndex = 0
    private var timeDelay = AppAdmob.remoteConfigViewModel.delayOpenTime
    private var timeSuccess = System.currentTimeMillis() - timeDelay
    private var listIgnoreActivities = listOf<String>()

    init {
        myAppAdmob.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun fetchAd() {
        if (DataLocal.isVip()) {
            return
        }

        if (isAdAvailable) {
            return
        }

        if (!RemoteManager.openApp){
            return
        }

        val adUnitId = if (BuildConfig.DEBUG) {
            Constants.ID_OPEN
        } else {
            DataLocal.listKeyOpen()[countIndex]?.getIdOpen().toString()
        }
        
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                this@AppOpenManager.appOpenAd = appOpenAd
                loadTime = Date().time
                AppAdmob.isOpen = false
                countIndex = 0
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                countIndex += 1
                if (countIndex < DataLocal.listKeyOpen().size) {
                    fetchAd()
                } else {
                    countIndex = 0
                    AppAdmob.isOpen = false
                }
            }
        }
        val request = adRequest
        loadCallback?.let {
            AppOpenAd.load(
                myAppAdmob, adUnitId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, it
            )
        }
    }

    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()


    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }


    private val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    private fun timeSuccessDelay(): Boolean {
        return System.currentTimeMillis() - timeSuccess >= timeDelay
    }

    fun showAdIfAvailable(onCallBack : (()-> Unit)? = null) {
        if (isIgnoreActivity(currentActivity)) {
            return
        }
        if (!timeSuccessDelay()) {
            onCallBack?.invoke()
            return
        }
        if (AdsFullManager.isShowing()) {
            if (appOpenAd == null) {
                fetchAd()
            }
            onCallBack?.invoke()
            return
        }
        if (DataLocal.isVip()) {
            onCallBack?.invoke()
            return
        }
        if (!RemoteManager.openApp) {
            onCallBack?.invoke()
            return
        }
        if (NetworkUtils.isNetworkConnected(currentActivity!!)) {
            if (appOpenAd == null) {
                onCallBack?.invoke()
                fetchAd()
                return
            }
            if (!AppAdmob.isOpen && isAdAvailable) {
                val fullScreenContentCallback: FullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            AppAdmob.isOpen = false
                            timeSuccess = System.currentTimeMillis()
                            onCallBack?.invoke()
                            fetchAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            AppAdmob.isOpen = false
                            onCallBack?.invoke()
                        }

                        override fun onAdShowedFullScreenContent() {
                            AppAdmob.isOpen = true
                            timeSuccess = System.currentTimeMillis()
                        }
                    }
                appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
                appOpenAd!!.show(currentActivity!!)
            } else {
                onCallBack?.invoke()
                fetchAd()
            }
        }
    }


    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }


    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    fun setIgnoreActivities(data: List<String>) {
        listIgnoreActivities = data
    }

    private fun isIgnoreActivity(activity: Activity?): Boolean {
        if (activity == null) return false
        return listIgnoreActivities.find { activity::class.java.simpleName == it } != null
    }

}