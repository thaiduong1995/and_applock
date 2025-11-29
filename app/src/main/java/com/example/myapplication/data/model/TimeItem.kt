package com.example.myapplication.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "time_lock")
class TimeItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "day")
    var day: String = "",

    @ColumnInfo(name = "start_time")
    var startTime: String = "",

    @ColumnInfo(name = "end_time")
    var endTime: String = "",

    @ColumnInfo(name = "enable")
    var enable: Boolean = true,
)