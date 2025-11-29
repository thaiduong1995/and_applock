package com.example.myapplication.base

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    protected val cemAdManager by lazy { CemAdManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        Timber.d("____Activity" + this@BaseActivity::class.java)
        configLanguage()
    }

    override fun onResume() {
        super.onResume()
        cemAdManager.loadInterstitial(this, ConstAd.FULL_KEY_BACK)
        loadAds()
        initUI()
        initListener()
        initObservers()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            try {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val navHostFragment: NavHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    navHostFragment.childFragmentManager.fragments.onEach {
                        if (it is BaseFragment) {
                            it.dispatchTouchEvent(event)
                        }
                    }
                } else {
                    // do nothing
                }
            }catch (e: Exception){
                Log.d("thinhvh", "dispatchTouchEvent: ")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    open fun initUI() {}
    open fun initListener() {}
    open fun initObservers() {}

    open fun loadAds() {}

    fun setNavGraphId(navGraphId: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(navGraphId)
        navHostFragment.navController.graph = graph
    }

    private fun configLanguage() {
        val config = resources.configuration
        val lang = PreferenceHelper(this).getLanguageCode()
        if (lang.isNotEmpty()) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.locale = locale
            createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}
