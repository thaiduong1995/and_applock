package com.cem.admodule.data

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonDataException

@Keep
data class TimeManager(
    @Json(name = "configKey") var configKey: String,
    @Json(name = "time") var time: Long
) : BaseResponseModel(){
    companion object{

        private val adapter = moshi.adapter(TimeManager::class.java)
        @JvmStatic
        fun fromJson(json: String?): TimeManager? {
            return try {
                if (json.isNullOrBlank()) return null
                adapter.fromJson(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        @JvmStatic
        fun convertTipManagerToJson(tipManager: TimeManager?): String {
            return try {
                val adapter = moshi.adapter(TimeManager::class.java)
                adapter.toJson(tipManager)
            } catch (e: JsonDataException) {
                e.printStackTrace()
                ""
            }
        }
    }
}
