package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SecurityQuestion(
    @StringRes
    var question: Int = 0,
    var selected: Boolean = false
) : Parcelable

