package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ConfigBanner {
    @SerializedName("network")
    @Expose
    private var network: String? = null

    @SerializedName("id_banner")
    @Expose
    private var idBanner: String? = null

    @SerializedName("enable")
    @Expose
    private var enable: Boolean? = null

    constructor(enable: Boolean, idBanner: String?, network: String){
        this.enable = enable
        this.idBanner = idBanner
        this.network = network
    }

    fun getNetwork(): String? {
        return network
    }

    fun setNetwork(network: String) {
        this.network = network
    }

    fun getIdBanner(): String? {
        return idBanner
    }

    fun setIdBanner(idNative: String?) {
        this.idBanner = idNative
    }

    fun getEnable(): Boolean? {
        return enable
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }
}