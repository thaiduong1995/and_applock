package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FileApp(
    var id: Long = 0,
    var name: String = "",
    var path: String = "",
    var type: String = "",
    var size: Long = 0,
    var dateModified: Long = 0,
    var favorite: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    var isSelected: Boolean = false
}
