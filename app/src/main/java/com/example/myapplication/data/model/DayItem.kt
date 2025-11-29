package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DayItem(
    var name: String = "",
    var isSelected: Boolean = false
) : Parcelable

//class DayItemConverter {
//    @TypeConverter
//    fun fromDayItem(item: DayItem): String {
//        return JSONObject().apply {
//            put("name", item.name)
//            put("isSelected", item.isSelected)
//        }.toString()
//    }
//
//    @TypeConverter
//    fun toDayItem(day: String): DayItem {
//        val json = JSONObject(day)
//        return DayItem(json.getString("name"), json.getBoolean("isSelected"))
//    }
//}