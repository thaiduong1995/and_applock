package com.example.myapplication.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.cem.admodule.manager.PurchaseManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.data.model.RegexModel
import com.example.myapplication.service.ServiceStarter
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.view_model.MainViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var preferences: PreferenceHelper

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (preferences.isAppLockEnabled()) {
            ServiceStarter.startServiceMain(this)
        }
        val isSetupPassword = viewModel.isSetupPassword()
        if (isSetupPassword) {
            setNavGraphId(R.navigation.home_graph)
        } else {
            setNavGraphId(R.navigation.setup_graph)
        }
        checkActionCLickNotification()
        CoroutineScope(Dispatchers.IO).launch {
            PurchaseManager.instance?.isRemovedAds(this@MainActivity)
        }
    }

    private fun checkActionCLickNotification() {
        when (intent.action) {
            Constants.ACTION_LOCK_APP -> {
                regexScreen(RegexModel(type = Constants.REGEX_TYPE_LOCK))
            }

            Constants.ACTION_WEB -> {
                regexScreen(RegexModel(type = Constants.REGEX_TYPE_WEB))
            }

            Constants.ACTION_CLEAN -> {
                regexScreen(RegexModel(type = Constants.REGEX_TYPE_CLEAN))
            }

            Constants.ACTION_THEME -> {
                regexScreen(RegexModel(type = Constants.REGEX_TYPE_THEME))
            }

            else -> {}
        }
    }

    private fun regexScreen(regexModel: RegexModel) {
        viewModel.setRegexScreen(regexModel)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}