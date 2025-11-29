package com.example.myapplication.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "MultipleTabsBrowser")
data class TabBrowser(
    var image: String = "",
    var url: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}