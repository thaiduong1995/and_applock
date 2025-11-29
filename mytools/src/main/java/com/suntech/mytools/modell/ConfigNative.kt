package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ConfigNative {
    @SerializedName("network")
    @Expose
    private var network: String? = null

    @SerializedName("id_native")
    @Expose
    private var idNative: String? = null

    @SerializedName("enable")
    @Expose
    private var enable: Boolean? = null

    constructor(enable: Boolean, idNative: String?, network: String) {
        this.enable = enable
        this.idNative = idNative
        this.network = network
    }

    fun getNetwork(): String? {
        return network
    }

    fun setNetwork(network: String) {
        this.network = network
    }

    fun getIdNative(): String? {
        return idNative
    }

    fun setIdNative(idNative: String?) {
        this.idNative = idNative
    }

    fun getEnable(): Boolean? {
        return enable
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }
}
