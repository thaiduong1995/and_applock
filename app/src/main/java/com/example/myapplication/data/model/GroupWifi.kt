package com.example.myapplication.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity(tableName = "groupwifi")
data class GroupWifi(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "count")
    var childWifiCount: Int = 0,
    @ColumnInfo(name = "enable")
    var enabled: Boolean = true
) : Serializable
