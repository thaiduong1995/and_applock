package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
enum class OverlayValidateType(var value: Int = 0) {
    TYPE_PATTERN(0),
    TYPE_PIN(1),
    TYPE_KNOCK_CODE(2)
}