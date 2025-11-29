package com.example.myapplication.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.cem.admodule.data.ErrorCode
import com.cem.admodule.ext.AdmobConfig
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.CemInterstitialAd
import com.cem.admodule.inter.InterstitialLoadCallback
import com.cem.admodule.manager.GoogleConsentManager
import com.example.myapplication.BuildConfig
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.databinding.ActivitySplashBinding
import com.example.myapplication.view_model.MainViewModel
import com.google.android.ump.ConsentDebugSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class StartViewActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<MainViewModel>()
    private var googleConsentManager: GoogleConsentManager? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()

//        CoroutineScope(Dispatchers.IO).launch {
//            PurchaseManager.instance?.isRemovedAds(this@StartViewActivity)
//        }
//        resetData()
//        NativeManager.createNativesAds(this)
//        if (viewModel.isSetupPassword()) {
//            AdsFullManager.loadInterstitial(this, onLoaded = {
//                AdsFullManager.showInterstitial(this,
//                    isShowRemote = RemoteManager.interSplash,
//                    onDismiss = {
//                        startLockActivity()
//                    })
//            }, onFailedToLoadAll = {
//            startLockActivity()
//            })
//        } else {
//            Handler(Looper.getMainLooper()).postDelayed({
//                startMainActivity()
//            }, 5000)
//        }
    }

    private fun fetchData() {
        cemAdManager.initMMKV()
        fetchConfigFirebase()
    }

    private fun fetchConfigFirebase() {
        this.lifecycleScope.launch {
            cemAdManager.fetchConfig(
                configKey = ConstAd.ADS_REMOTE,
                fileLocal = if (BuildConfig.DEBUG) ConstAd.ADS_CONFIG_TEST else ConstAd.ADS_CONFIG
            ).collect { response ->
                if (response) initGDPR()
            }
        }
    }

    private fun initGDPR() {
        if (!cemAdManager.isVip()) {
            googleConsentManager = GoogleConsentManager.getInstance(this)
            if (BuildConfig.DEBUG) {
                googleConsentManager?.resetConsent()
            }
            googleConsentManager?.gatherConsentDebugWithGeography(
                activity = this,
                geography = ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA,
                hashedId = "8ED38EB8914381CAA2D5D9B53F9427E8",
                appId = cemAdManager.getAdManager?.adConfig?.appId,
                onConsentGatheringCompleteListener = { consentError ->
                    if (consentError != null) {
                        initAdmobManager()
                        Timber.e("initGDPR con " + googleConsentManager?.isRequestAds + "--" + consentError.errorCode + "--" + consentError.message + " ")
                    }
                    if (googleConsentManager?.isRequestAds == true) {
                        Timber.e("initGDPR: 1" + googleConsentManager?.isRequestAds + " " + Thread.currentThread().stackTrace[2].lineNumber)
                        initAdmobManager()
                    }
                })
            if (googleConsentManager?.isRequestAds == true) {
                Timber.e("initGDPR:" + googleConsentManager?.isRequestAds + " " + Thread.currentThread().stackTrace[2].lineNumber)
                initAdmobManager()
            }
        } else {
            initAdmobManager()
        }
    }

    private fun initAdmobManager() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isMobileAdsInitializeCalled.getAndSet(true)) {
                return@launch
            }
            cemAdManager.getAdManager?.let {
                AdmobConfig.initialize(
                    application = this@StartViewActivity,
                    testDeviceIds = listOf(
                        "59B28BE4BF69F8F6DF58FD314FBC858B",
                        "8CC819096052C744497915C174693FBB",
                        "8ED38EB8914381CAA2D5D9B53F9427E8"
                    )
                )
            }
            loadingAdManager()
        }
    }

    private fun loadingAdManager() {
        cemAdManager
            .fetchOpenAds(ConstAd.OPEN_KEY)
            .loadNativeCallback(this@StartViewActivity, ConstAd.NATIVE_ADS)
            .loadNativeCallback(this@StartViewActivity, ConstAd.NATIVE_DETAIL)
            .loadInterstitial(
                activity = this@StartViewActivity,
                configKey = ConstAd.FULL_KEY_SPLASH,
                callback = object : InterstitialLoadCallback {
                    override fun onAdLoaded(cemInterstitialAd: CemInterstitialAd?) {
                        if (supportFragmentManager.isStateSaved || supportFragmentManager.isDestroyed) {
                            return
                        }
                        println("Đã vào đây rồi: onAdLoaded")
                        startApp()
                    }

                    override fun onAdFailedToLoaded(error: ErrorCode) {
                        println("Đã vào đây rồi: onAdFailedToLoaded")
                        startApp()
                    }

                },
                onTimeOut = {
                    println("Đã vào đây rồi: Timeout")
                    startApp()
                }
            )
            .loadInterstitial(this@StartViewActivity, ConstAd.FULL_KEY_DETAIL)
            .loadInterstitial(this@StartViewActivity, ConstAd.FULL_KEY_BACK)
            .loadRewardCallback(this@StartViewActivity, ConstAd.REWARD_ADS)
    }

    private fun startApp() {
        if (viewModel.isSetupPassword()) {
            startLockActivity()
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        //AdsFullManager.interstitialInterval = AppAdmob.remoteConfigViewModel.delayTimeFirst
        cemAdManager.showInterstitialCallback(
            activity = this,
            configKey = ConstAd.FULL_KEY_SPLASH,
            callback = {
                startActivity(Intent(this, MainActivity::class.java))
                finishActivity()
            }
        )
    }

    private fun startLockActivity() {
        //AdsFullManager.interstitialInterval = AppAdmob.remoteConfigViewModel.delayTimeFirst
        cemAdManager.showInterstitialCallback(
            activity = this,
            configKey = ConstAd.FULL_KEY_SPLASH,
            callback = {
                startActivity(LockActivity.newIntent(this@StartViewActivity, this.packageName))
                finishActivity()
            }
        )
    }

    private fun finishActivity() {
        finish()
    }

    private fun resetData() {
//        AppAdmob.isOpen = false
//        AppAdmob.isShowing = false
//        AppAdmob.interstitialAd = null
//        AppAdmob.listNativeAd.clear()
//        NativeManager.resetNative()
    }
}