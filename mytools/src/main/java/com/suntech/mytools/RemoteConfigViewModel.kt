package com.suntech.mytools

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.suntech.mytools.modell.Config
import com.suntech.mytools.modell.KeyConfig

class RemoteConfigViewModel : ViewModel() {

    var remoteConfig: FirebaseRemoteConfig? = null
    var listUnSupportedVersion = mutableListOf<String>()
    var delayTimeDefault = 10000L
    var delayTimeFirst = 5000L
    var delayOpenTime = 5000L
    var remoteConfigAdmob = mutableListOf<Config>()

    init {
        initRemote()
    }

    private fun initRemote() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds =
                if (BuildConfig.DEBUG) DEBUG_TIME_FETCH_CONFIG else RELEASE_TIME_FETCH_CONFIG
        }
        remoteConfig?.setConfigSettingsAsync(configSettings)
        if (BuildConfig.DEBUG) {
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_default_test)
        } else {
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
        }
        remoteConfig?.fetch()?.addOnCompleteListener {
            if (it.isSuccessful) {
                val updated = it.result
                Log.e("RemoteConfigViewModel", "$updated")
            }
            getRemoteAdmob()
            getVersionAppUpdate()
        }
    }

    private fun getRemoteAdmob() {
        remoteConfig?.fetchAndActivate()?.addOnSuccessListener {
            val json = remoteConfig?.getString("configAdmob")
            val units = Gson().fromJson(json, KeyConfig::class.java)
            units.getConfig()?.let {
                remoteConfigAdmob.add(it)
            }

            val delayTime = remoteConfig?.getString("delayTimeDefault")
            delayTime?.let {
                this.delayTimeDefault = it.trim().toLong()
            }

            val delayTimeFirst = remoteConfig?.getString("delayTimeFirst")
            delayTimeFirst?.let {
                this.delayTimeFirst = it.trim().toLong()
            }

            val delayOpenTime = remoteConfig?.getString("delayTimeOpen")
            delayOpenTime?.let {
                this.delayOpenTime = it.trim().toLong()
            }
        }
    }

    private fun getVersionAppUpdate() {
        val unSupportedVersion = remoteConfig?.getString("unsupported_version")
        unSupportedVersion?.let {
            if (unSupportedVersion.isNotEmpty()) {
                listUnSupportedVersion =
                    Gson().fromJson(unSupportedVersion, Array<String>::class.java).toMutableList()
            }
        }
    }


    companion object {
        const val DEBUG_TIME_FETCH_CONFIG = 10L
        const val RELEASE_TIME_FETCH_CONFIG = 3600L
    }
}