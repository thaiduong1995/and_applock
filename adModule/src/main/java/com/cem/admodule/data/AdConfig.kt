package com.cem.admodule.data

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import org.json.JSONObject

@Keep
data class AdConfig(
    @Json(name = "enable") var enable: Boolean = true,
    @Json(name = "open_interval") var openInterval: Int = 0,
    @Json(name = "interval") var adInterval: Int = 30,
    @Json(name = "banner_interval") var bannerInterval: Int = 10,
    @Json(name = "native_interval") var nativeInterval: Int = 10,
    @Json(name = "load_interval") var loadInterval: Int = 10,
    @Json(name = "app_id") var appId: String? = null,
    @Json(name = "isEnableGPDR") var isEnableGPDR: Boolean = false
) : BaseResponseModel() {

    companion object {
        private val adapter: JsonAdapter<AdConfig> = moshi.adapter(AdConfig::class.java)

        @JvmStatic
        val emptyValue = AdConfig(
            enable = true, adInterval = 30, openInterval = 0
        )

        @JvmStatic
        fun fromJson(config: JSONObject?): AdConfig {
            return if (config == null) return emptyValue
            else try {
                adapter.fromJson(config.toString()) ?: emptyValue
            } catch (e: Exception) {
                e.printStackTrace()
                emptyValue
            }
        }

        @JvmStatic
        fun toJson(adConfig: AdConfig?): JSONObject? {
            if (adConfig == null) return null
            return try {
                JSONObject(adapter.toJson(adConfig))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}