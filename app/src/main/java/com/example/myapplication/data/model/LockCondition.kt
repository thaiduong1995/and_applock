package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
enum class LockCondition {
    TIME_LOCK,
    LOCATION_LOCK,
    WIFI_LOCK
}