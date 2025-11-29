package com.cem.admodule.data


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import org.json.JSONObject
import java.lang.reflect.Type

@Keep
data class AdManager(
    @Json(name = "configs") var adConfig: AdConfig? = null,
    @Json(name = "units") var adUnitList: Map<String, List<AdUnitItem>>? = null
) : BaseResponseModel() {
    var isEnable: Boolean
        get() = adConfig?.enable ?: true
        set(value) {
            adConfig?.enable = value
        }

    val adInterval: Long
        get() {
            return (adConfig?.adInterval ?: 30) * 1000L
        }

    val openInterval: Long
        get() {
            return (adConfig?.openInterval ?: 10) * 1000L
        }

    val bannerInterval: Long
        get() {
            return (adConfig?.bannerInterval ?: 10) * 1000L
        }

    val timeLoadSplashInterval: Long
        get() {
            return (adConfig?.loadInterval ?: 10) * 1000L
        }

    val nativeInterval: Long
        get() {
            return (adConfig?.nativeInterval ?: 10) * 1000L
        }

    companion object {
        private val adapter = moshi.adapter(AdManager::class.java)

        @JvmStatic
        fun fromJson(json: String?): AdManager? {
            return try {
                if (json.isNullOrBlank()) return null
                adapter.fromJson(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun toJson(adManager: AdManager?): JSONObject? {
            if (adManager == null) return null
            return try {
                JSONObject(adapter.toJson(adManager))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}