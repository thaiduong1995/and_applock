package com.cem.admodule.data


import androidx.annotation.Keep
import com.squareup.moshi.Json
import org.json.JSONObject

@Keep
data class VersionManager(
    @Json(name = "latest_version") var latestVersion: Int? = 1,
    @Json(name = "config") var versionTypeList: Map<String, List<VersionItem>>? = null
) : BaseResponseModel() {

    companion object {
        private val adapter = moshi.adapter(VersionManager::class.java)

        @JvmStatic
        fun fromJson(json: String?): VersionManager? {
            return try {
                if (json.isNullOrBlank()) return null
                adapter.fromJson(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JvmStatic
        fun toJson(adManager: VersionManager?): JSONObject? {
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