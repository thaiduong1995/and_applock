package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ConfigFull {
    @SerializedName("network")
    @Expose
    private var network: String? = null

    @SerializedName("idFull")
    @Expose
    private var idFull: String? = null

    @SerializedName("enable")
    @Expose
    private var enable: Boolean? = null

    constructor(network: String, idFull: String?, enable: Boolean) {
        this.network = network
        this.idFull = idFull
        this.enable = enable
    }

    fun getNetwork(): String? {
        return network
    }

    fun setNetwork(network: String) {
        this.network = network
    }

    fun getIdFull(): String? {
        return idFull
    }

    fun setIdFull(idFull: String?) {
        this.idFull = idFull
    }

    fun getEnable(): Boolean? {
        return enable
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }
}
