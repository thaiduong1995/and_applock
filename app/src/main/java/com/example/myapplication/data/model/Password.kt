package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Password(
    val id: Int = 0,
    val name: String = "",
    @DrawableRes
    val icon: Int = 0,
    val passwordLength: Int = 0,
    var selected: Boolean = false,
) : Parcelable