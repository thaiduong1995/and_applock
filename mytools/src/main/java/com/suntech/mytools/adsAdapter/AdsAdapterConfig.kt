package com.suntech.mytools.adsAdapter

data class AdsAdapterConfig(
    val itemThresholds: Int = DEFAULT_THRESHOLDS,
    val showAdsInCenter: Boolean= true,
    val showBanner: Boolean = true,
    val showNative: Boolean = false,
    val loadMore: Boolean = false
) {
    companion object {
        const val DEFAULT_THRESHOLDS = 7
    }
}