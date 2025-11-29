package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Entity(tableName = "custom_themes")
@Parcelize
data class CustomTheme(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "backgroundImagePath")
    var backgroundImagePath: String = "",
    @ColumnInfo(name = "previewImagePath")
    var previewImagePath: String = "",
    @ColumnInfo(name = "dotColor")
    var dotColor: Int = 0,
    @ColumnInfo(name = "numberColor")
    var numberColor: Int = 0,
    @ColumnInfo(name = "knockColor")
    var knockColor: Int = 0,
    @ColumnInfo(name = "lineColor")
    var lineColor: Int = 0,
    @ColumnInfo(name = "lockType")
    var lockType: Int = 0,
) : Parcelable