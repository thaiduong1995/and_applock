package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
sealed class Step {
    data object Step1 : Step()
    data object Step2 : Step()
}