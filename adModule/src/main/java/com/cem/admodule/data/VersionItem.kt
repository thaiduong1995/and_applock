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
data class VersionItem(
    @Json(name = "version_name") val versionName: String? = null,
    @Json(name = "version_code") val versionCode: Int? = 0
) : BaseResponseModel() {

    companion object {
        private val typeList: Type =
            Types.newParameterizedType(List::class.java, VersionItem::class.java)
        private val adapterJSONArray: JsonAdapter<List<VersionItem?>?> = moshi.adapter(typeList)
        private val adapterJson = moshi.adapter<List<VersionItem>>(typeList)
        private val adapter: JsonAdapter<VersionItem> = moshi.adapter(VersionItem::class.java)


        @JvmStatic
        fun jsonFromArray(unitItem: List<VersionItem?>?): JSONArray? {
            if (unitItem.isNullOrEmpty()) return null
            return try {
                JSONArray(adapterJSONArray.toJson(unitItem))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun arrayFromJson(jsonArray: JSONArray?): List<VersionItem> {
            if (jsonArray == null) return emptyList()
            return try {
                adapterJson.fromJson(jsonArray.toString()) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

        @JvmStatic
        fun fromJson(jsonObject: JSONObject?): VersionItem? {
            if (jsonObject == null) return null
            return try {
                adapter.fromJson(jsonObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun toJson(adUnitItem: VersionItem?): JSONObject? {
            if (adUnitItem == null) return null
            return try {
                JSONObject(adapter.toJson(adUnitItem))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
