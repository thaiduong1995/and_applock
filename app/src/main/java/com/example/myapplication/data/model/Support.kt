package com.example.myapplication.data.model

import androidx.annotation.Keep

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
data class Support(
    @com.squareup.moshi.Json(name = "url")
    private var url: String? = null,
    @com.squareup.moshi.Json(name = "text")
    private var text: String? = null
)