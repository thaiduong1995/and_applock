package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
enum class BrowserType(val value: Int) {
    HISTORY(0),
    BOOKMARK(1),
    MULTIPLE_TABS(2)
}