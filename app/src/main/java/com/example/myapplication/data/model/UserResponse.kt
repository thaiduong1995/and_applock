package com.example.myapplication.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
data class UserResponse(
    @Json(name = "data")
    var user: User? = null,
    @Json(name = "support")
    var support: Support? = null
)