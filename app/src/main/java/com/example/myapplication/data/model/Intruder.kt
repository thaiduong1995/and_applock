package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "intruder")
class Intruder(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var appName: String = "",
    var time: String = "",
    var imageUrl: String = "",
    var tryCount: Int = 0
) : Parcelable