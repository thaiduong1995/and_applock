package com.cem.admodule.enums

import androidx.annotation.Keep

@Keep
enum class AdNetwork(val nameNetwork: String) {
    ADMOB(nameNetwork = "admob"),
    ADX(nameNetwork = "adx"),
    APPLOVIN(nameNetwork = "applovin"),
    MINTEGRAL(nameNetwork = "mintegral"),
    IRONSOURCE("ironsource");

    companion object {

        @JvmStatic
        fun getNetwork(adName: String?): AdNetwork? =
            AdNetwork.values().find { it.nameNetwork == adName }
    }
}




