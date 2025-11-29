package com.cem.admodule.data

import androidx.annotation.Keep

@Keep
data class RewardAdItem(
    val type: String, val amount: Int
) : BaseResponseModel() {
    companion object {
        @JvmField
        var DEFAULT = RewardAdItem("DEFAULT", 0)
    }
}
