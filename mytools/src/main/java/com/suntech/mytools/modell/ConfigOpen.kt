package com.suntech.mytools.modell

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ConfigOpen {
    @SerializedName("id_open")
    @Expose
    private var idOpen: String? = null

    @SerializedName("enable")
    @Expose
    private var enable: Boolean? = null

    constructor(enable: Boolean, idOpen: String?){
        this.enable = enable
        this.idOpen = idOpen
    }


    fun getIdOpen(): String? {
        return idOpen
    }

    fun setIdOpen(idNative: String?) {
        this.idOpen = idNative
    }

    fun getEnable(): Boolean? {
        return enable
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }
}