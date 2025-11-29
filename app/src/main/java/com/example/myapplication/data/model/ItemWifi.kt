package com.example.myapplication.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "wifi", primaryKeys = ["bssid", "groupid"])
data class ItemWifi(

    // wifi address
    @ColumnInfo(name = "bssid")
    val bssId: String,

    // wifi name
    @ColumnInfo(name = "ssid")
    val ssid: String,

    @ColumnInfo(name = "groupid")
    val groupId: Long,
    @ColumnInfo(name = "enable")
    var enabled: Boolean = false
)
