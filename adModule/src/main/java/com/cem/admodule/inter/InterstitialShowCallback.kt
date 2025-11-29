package com.cem.admodule.inter

import com.cem.admodule.enums.AdNetwork

interface InterstitialShowCallback {
    fun onAdFailedToShowCallback(error : String)
    fun onAdShowedCallback(network : AdNetwork)
    fun onDismissCallback(network: AdNetwork)

    fun onAdClicked()
}