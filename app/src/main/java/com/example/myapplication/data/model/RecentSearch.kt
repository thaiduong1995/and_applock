package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity
data class RecentSearch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var addressName: String = "",
    var road: String = "",
    var type: Int = 0
) : Parcelable
