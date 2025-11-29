package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
enum class LockAnimationType(var id: Int = 0) {
    SWIPE_TOP(0),
    SWIPE_DOWN(1),
    SWIPE_RIGHT(2),
    SWIPE_LEFT(3),
    FADE_OUT(4),
    RANDOM(5)
}