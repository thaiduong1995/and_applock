package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Language(
    var name: String = "",
    var code: String = "",
    var isSelected: Boolean = false
) : Parcelable

@Keep
enum class LANGUAGEType(var languageName: String = "", var languageCode: String = "") {
    ENGLISH("English", "en"),
    VIETNAM("Vietnamese", "vi"),
    CHINES("中国人", "zh"),
    SPANISH("Español", "es"),
    PORTUGUESE("Japanese", "ja"),
    GEMANY("Deutsch", "de"),
    FRENCH("French", "fr"),
    INDO("Indonesia", "in"),
}
