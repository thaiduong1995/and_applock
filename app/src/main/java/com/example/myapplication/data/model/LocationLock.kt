package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
@Entity(tableName = "location")
@Parcelize
data class LocationLock(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "locationName")
    var locationName: String = "",
    @ColumnInfo(name = "address")
    var address: String = "",
    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,
    @ColumnInfo(name = "radius")
    var radius: Int = 0,
    @ColumnInfo(name = "isInverse")
    var isInverse: Boolean = false,
    @ColumnInfo(name = "enable")
    var enabled: Boolean = true
) : Serializable, Parcelable
