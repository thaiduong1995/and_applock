package com.example.myapplication.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
data class Question(
    @StringRes val question: Int = 0,
    @DrawableRes val icon: Int = 0,
    @StringRes val description: Int = 0,
    var isExpanded: Boolean = false
)