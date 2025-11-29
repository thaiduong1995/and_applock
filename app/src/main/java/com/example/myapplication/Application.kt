package com.example.myapplication

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.cem.admodule.data.Configuration
import com.cem.admodule.ext.CemConfigManager
import com.cem.admodule.manager.CemAdManager
import com.cem.admodule.manager.CemAppOpenManager
import com.cem.admodule.manager.ConfigManager
import com.example.myapplication.service.ServiceStarter
import com.example.myapplication.ui.activities.LockActivity
import com.example.myapplication.ui.activities.LockFakeCoverActivity
import com.example.myapplication.ui.activities.StartViewActivity
import com.example.myapplication.utils.DebugTree
import com.example.myapplication.utils.PreferenceHelper
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.plant
import javax.inject.Inject

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@HiltAndroidApp
class Application : Application() {

    @Inject
    lateinit var preferences: PreferenceHelper

    override fun onCreate() {
        super.onCreate()
        configTimber()
        if (preferences.isAppLockEnabled()) {
            ServiceStarter.startService(this)
        }
        initAds()
        FirebaseApp.initializeApp(this)
    }

    private fun initAds() {
        CoroutineScope(Dispatchers.IO).launch {
            CemConfigManager.initAppsflyer(
                application = this@Application,
                flurryKey = "k7fyBqQm2ys9iZowhKjhBn"
            )
            CemConfigManager.initializeAds(
                app = this@Application,
                configuration = Configuration(testDeviceIds = listOf("8ED38EB8914381CAA2D5D9B53F9427E8")),
                callbackCountry = {
                    ConfigManager.getInstance(this@Application).setCountry(it)
                })
        }
        CemAppOpenManager.getInstance(this).apply {
            setIgnoreActivities(
                listOf(
                    StartViewActivity::class.java.simpleName,
                    LockActivity::class.java.simpleName,
                    LockFakeCoverActivity::class.java.simpleName
                )
            )
            registerProcessLifecycle()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val isSetupPassword =
            preferences.getPatternCode().isNotEmpty() || preferences.getKnockCode()
                .isNotEmpty() || preferences.getPinCode().isNotEmpty()
        if (CemAdManager.getInstance(this).isVip()) {
            if (!LockActivity.IS_SHOWING && isSetupPassword) {
                startActivity(
                    LockActivity.newIntent(this, BuildConfig.APPLICATION_ID, false)
                )
            }
            return
        }
//        appOpenManager?.showAdIfAvailable {
//            if (!LockActivity.IS_SHOWING && isSetupPassword) {
//                startActivity(
//                    LockActivity.newIntent(this, BuildConfig.APPLICATION_ID, false)
//                )
//            }
//        }
    }


    private fun configTimber() {
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }
}