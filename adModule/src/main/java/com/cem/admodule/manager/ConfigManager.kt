package com.cem.admodule.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.cem.admodule.BuildConfig
import com.cem.admodule.data.AdManager
import com.cem.admodule.data.TimeManager
import com.cem.admodule.data.VersionManager
import com.cem.admodule.ext.ConstAd
import com.cem.firebase_module.config.RemoteConfigImpl
import com.getkeepsafe.relinker.ReLinker
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ConfigManager private constructor(
    private val context: Context
) {

    private var _adManagement: AdManager? = null
    private var _versionManagement: VersionManager? = null

    var dataStore: MMKV? = null

    var adManagement: AdManager?
        set(value) {
            _adManagement = value
        }
        get() {
            return _adManagement ?: AdManager.fromJson(
                if (BuildConfig.DEBUG) loadData(ConstAd.ADS_CONFIG_TEST)
                else if (!getCacheConfig().isNullOrEmpty()) {
                    getCacheConfig()
                } else loadData(ConstAd.ADS_CONFIG)
            )
        }

    var versionManagement: VersionManager?
        set(value) {
            _versionManagement = value
        }
        get() {
            return _versionManagement ?: VersionManager.fromJson(
                if (BuildConfig.DEBUG) loadData(ConstAd.VERSION_CONFIG_TEST)
                 else loadData(ConstAd.VERSION_CONFIG)
            )
        }


    init {
        initMMKV()
    }

    fun isEnable() = adManagement?.isEnable == true && !isVip()
    fun timeLoadSplash(): Long? = adManagement?.timeLoadSplashInterval
    fun timeReloadNativeConfig(): Long? = adManagement?.nativeInterval

    suspend fun fetchConfig(remoteKey: String?, fileLocal: String) = flow {
        val remoteConfig = RemoteConfigImpl()
        remoteConfig.fetchConfig().collect {
            if (it) {
                val adConfigResponse = remoteConfig.getString(remoteKey.orEmpty())
                if (!adConfigResponse.isNullOrEmpty()) {
                    setCacheConfig(adConfigResponse)
                }
                Log.d("ConfigManager", "adConfigResponse: ${adConfigResponse.toString()}")
                _adManagement = AdManager.fromJson(
                    if (BuildConfig.DEBUG) loadData(ConstAd.ADS_CONFIG_TEST) else {
                        if (!adConfigResponse.isNullOrEmpty()) {
                            adConfigResponse
                        } else if (!getCacheConfig().isNullOrEmpty()) {
                            getCacheConfig()
                        } else loadData(fileLocal)
                    }
                )
                val versionConfigResponse = remoteConfig.getString("version_config")
                Log.d("ConfigManager", "versionConfigResponse: $versionConfigResponse")
                _versionManagement = VersionManager.fromJson(
                    if (BuildConfig.DEBUG) loadData(ConstAd.VERSION_CONFIG_TEST) else {
                    if (!versionConfigResponse.isNullOrEmpty()) {
                        versionConfigResponse
                    } else loadData(if(BuildConfig.DEBUG) ConstAd.VERSION_CONFIG_TEST else ConstAd.VERSION_CONFIG)
                })
                Log.d("Đã vào đây rồi:", "versionManagement: $_versionManagement")
                kotlinx.coroutines.delay(500)
                emit(true)
            }
        }
    }.flowOn(Dispatchers.IO)


    private fun loadData(fileName: String): String {
        return try {
            val stream = context.assets.open(fileName)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun initMMKV() {
        if (dataStore == null) {
            val dir = context.filesDir.absolutePath + "/mmkv"
            MMKV.initialize(context, dir) { libName ->
                ReLinker.loadLibrary(context, libName)
            }
            dataStore = MMKV.defaultMMKV()
        }
    }

    fun setIsVip(vip: Boolean) {
        dataStore?.putBoolean(KEY_IS_VIP, vip)
    }

    fun isVip(): Boolean {
        return if(BuildConfig.DEBUG) false else dataStore?.getBoolean(KEY_IS_VIP, false) ?: false
    }

    fun setCountry(value: String) {
        dataStore?.putString(COUNTRY_CODE, value)
    }

    fun getCountry(): String? {
        return dataStore?.getString(COUNTRY_CODE, null)
    }

    private fun setCacheConfig(value: String) {
        dataStore?.putString(CACHE_CONFIG, value)
    }

    private fun getCacheConfig(): String? {
        return dataStore?.getString(CACHE_CONFIG, "")
    }

    fun setTimeReload(timeManager: TimeManager){
        val json = TimeManager.convertTipManagerToJson(timeManager)
        dataStore?.putString("TIME_RELOAD_${timeManager.configKey}", json)
    }
    fun getTimeReload(configKey: String): TimeManager? {
        return TimeManager.fromJson(dataStore?.getString("TIME_RELOAD_$configKey", ""))
    }

    companion object {

        const val KEY_IS_VIP = "KEY_IS_VIP"
        const val CACHE_CONFIG = "CACHE_CONFIG"
        const val COUNTRY_CODE = "country_code"
        const val TIME_RELOAD = "TIME_RELOAD"


        @SuppressLint("StaticFieldLeak")
        private var mInstance: ConfigManager? = null

        fun getInstance(context: Context): ConfigManager {
            return mInstance ?: synchronized(this) {
                val instance = ConfigManager(context)
                mInstance = instance
                instance
            }
        }
    }
}