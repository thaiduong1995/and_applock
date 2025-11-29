package com.cem.admodule.data

import androidx.annotation.Keep
import com.google.android.gms.ads.AdSize
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

@Keep
data class AdUnitItem(
    @Json(name = "network") val adNetwork: String? = null,
    @Json(name = "unit_id") val adUnit: String? = null,
    @Json(name = "enable") var enable: Boolean = true,
    @Json(name = "position") val position: String? = null,
    @Json(name = "collapsible") val collapsible: Boolean = true,
    @Json(name = "show_direct") val showDirect: Boolean = false,
    @Json(name = "ad_size") val adSize: String? = null,
    @Json(name = "placement_id") val placementId: String? = null
) : BaseResponseModel() {

    companion object {
        private val typeList: Type =
            Types.newParameterizedType(List::class.java, AdUnitItem::class.java)
        private val adapterJSONArray: JsonAdapter<List<AdUnitItem?>?> = moshi.adapter(typeList)
        private val adapterJson = moshi.adapter<List<AdUnitItem>>(typeList)
        private val adapter: JsonAdapter<AdUnitItem> = moshi.adapter(AdUnitItem::class.java)


        @JvmStatic
        fun jsonFromArray(unitItem: List<AdUnitItem?>?): JSONArray? {
            if (unitItem.isNullOrEmpty()) return null
            return try {
                JSONArray(adapterJSONArray.toJson(unitItem))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun arrayFromJson(jsonArray: JSONArray?): List<AdUnitItem> {
            if (jsonArray == null) return emptyList()
            return try {
                adapterJson.fromJson(jsonArray.toString()) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

        @JvmStatic
        fun fromJson(jsonObject: JSONObject?): AdUnitItem? {
            if (jsonObject == null) return null
            return try {
                adapter.fromJson(jsonObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun toJson(adUnitItem: AdUnitItem?): JSONObject? {
            if (adUnitItem == null) return null
            return try {
                JSONObject(adapter.toJson(adUnitItem))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun getAdSize(adSize: String?): AdSize? {
            return when (adSize) {
                "banner" -> AdSize.BANNER
                "large_banner" -> AdSize.LARGE_BANNER
                "medium_banner" -> AdSize.MEDIUM_RECTANGLE
                "leaderboard" -> AdSize.LEADERBOARD
                "full_banner" -> AdSize.FULL_BANNER
                else -> null
            }
        }
    }
}
