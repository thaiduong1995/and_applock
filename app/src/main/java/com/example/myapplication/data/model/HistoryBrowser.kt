package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class HistoryBrowser(
    var idTabBrowser: Long = 0L,
    var image: String = "",
    var title: String = "",
    var url: String = "",
    var time: Long = 0L,
    var isBookMark: Boolean = false,
    var isDeleteHistory: Boolean = false
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
