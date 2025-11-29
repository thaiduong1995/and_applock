package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
enum class Filter(var isSelected: Boolean = false) {
    Locked,
    UnLocked,
    AZ,
    ZA,
    Newest,
    Oldest
}