package com.suntech.mytools.mytools.datalocal

import com.suntech.mytools.modell.ConfigBanner
import com.suntech.mytools.modell.ConfigFull
import com.suntech.mytools.modell.ConfigNative
import com.suntech.mytools.modell.ConfigOpen
import com.suntech.mytools.mytools.AppAdmob

object DataLocal {

    fun setIsVip(vip: Boolean) {
        AppAdmob.dataStore.putBoolean(Params.KEY_IS_VIP, vip)
    }

    fun isVip(): Boolean {
        return AppAdmob.dataStore.getBoolean(Params.KEY_IS_VIP, false)
    }

    var language: String?
        get() {
            return AppAdmob.dataStore.getString(Params.LANGUAGE, "")
        }
        set(value) {
            AppAdmob.dataStore.putString(Params.LANGUAGE, value)
        }

    fun listKeyFull(): List<ConfigFull?> {
        return if (AppAdmob.remoteConfigViewModel.remoteConfigAdmob.isNotEmpty()) {
            AppAdmob.remoteConfigViewModel.remoteConfigAdmob.first().getConfigFull()
                ?: getKeyFull()
        } else getKeyFull()
    }

    fun listKeyNative(): List<ConfigNative?> {
        return if (AppAdmob.remoteConfigViewModel.remoteConfigAdmob.isNotEmpty()) {
            AppAdmob.remoteConfigViewModel.remoteConfigAdmob.first().getConfigNative()
                ?: getKeyNative()
        } else getKeyNative()
    }

    fun listKeyOpen(): List<ConfigOpen?> {
        return if (AppAdmob.remoteConfigViewModel.remoteConfigAdmob.isNotEmpty()) {
            AppAdmob.remoteConfigViewModel.remoteConfigAdmob.first().getConfigOpen()
                ?: getKeyOpen()
        } else getKeyOpen()
    }

    fun listKeyBanner(): List<ConfigBanner?> {
        return if (AppAdmob.remoteConfigViewModel.remoteConfigAdmob.isNotEmpty()) {
            AppAdmob.remoteConfigViewModel.remoteConfigAdmob.first().getConfigBanner()
                ?: getKeyBanner()
        } else getKeyBanner()
    }


    private fun getKeyFull(): List<ConfigFull> {
        return mutableListOf(
            ConfigFull(
                enable = true, idFull = "ca-app-pub-5179928085266214/5843502701", network = "admob"
            ), ConfigFull(
                enable = true, idFull = "ca-app-pub-5179928085266214/6984413733", network = "admob"
            ), ConfigFull(
                enable = true, idFull = "ca-app-pub-5179928085266214/9591176020", network = "admob"
            )
        )
    }

    private fun getKeyNative(): List<ConfigNative> {
        return mutableListOf(
            ConfigNative(
                enable = true,
                idNative = "ca-app-pub-5179928085266214/8596878307",
                network = "admob"
            ), ConfigNative(
                enable = true,
                idNative = "ca-app-pub-5179928085266214/6006358077",
                network = "admob"
            ), ConfigNative(
                enable = true,
                idNative = "ca-app-pub-5179928085266214/9949472516",
                network = "admob"
            )
        )
    }

    private fun getKeyOpen(): List<ConfigOpen> {
        return mutableListOf(
            ConfigOpen(enable = true, idOpen = "ca-app-pub-5179928085266214/9949472516"),
            ConfigOpen(enable = true, idOpen = "ca-app-pub-5179928085266214/4282212868"),
            ConfigOpen(enable = true, idOpen = "ca-app-pub-5179928085266214/2969131197")
        )
    }

    private fun getKeyBanner(): List<ConfigBanner> {
        return mutableListOf(
            ConfigBanner(
                enable = true,
                idBanner = "ca-app-pub-5179928085266214/3408911056",
                network = "admob"
            ), ConfigBanner(
                enable = true,
                idBanner = "ca-app-pub-5179928085266214/8420768424",
                network = "admob"
            ), ConfigBanner(
                enable = true,
                idBanner = "ca-app-pub-5179928085266214/3160702889",
                network = "admob"
            )
        )
    }


}