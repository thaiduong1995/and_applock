package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.suntech.mytools.modell.Config


@Keep
class KeyConfig {
    @SerializedName("config")
    @Expose
    private var config: Config? = null

    fun getConfig(): Config? {
        return config
    }

    fun setConfig(config: Config) {
        this.config = config
    }
}