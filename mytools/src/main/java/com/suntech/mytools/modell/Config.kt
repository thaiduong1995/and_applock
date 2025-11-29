package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class Config{
    @SerializedName("config_full")
    @Expose
    private var configFull: List<ConfigFull?>? = null

    @SerializedName("config_native")
    @Expose
    private var configNative: List<ConfigNative?>? = null

    @SerializedName("config_open")
    @Expose
    private var configOpen: List<ConfigOpen?>? = null

    @SerializedName("config_banner")
    @Expose
    private var configBanner: List<ConfigBanner?>? = null

    @SerializedName("config_reward")
    @Expose
    private var configReward: List<ConfigReward?>? = null

    fun getConfigFull(): List<ConfigFull?>? {
        return configFull
    }

    fun setConfigFull(configFull: List<ConfigFull?>?) {
        this.configFull = configFull
    }

    fun getConfigNative(): List<ConfigNative?>? {
        return configNative
    }

    fun setConfigNative(configNative: List<ConfigNative?>?) {
        this.configNative = configNative
    }


    fun getConfigOpen(): List<ConfigOpen?>? {
        return configOpen
    }

    fun setConfigOpen(configFull: List<ConfigOpen?>?) {
        this.configOpen = configFull
    }

    fun getConfigBanner(): List<ConfigBanner?>? {
        return configBanner
    }

    fun setConfigBanner(configNative: List<ConfigBanner?>?) {
        this.configBanner = configNative
    }

    fun getConfigReward(): List<ConfigReward?>? {
        return configReward
    }

    fun setConfigReward(configReward: List<ConfigReward?>?) {
        this.configReward = configReward
    }

}
