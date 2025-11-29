package com.example.myapplication.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json


/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
data class User(

    @Json(name = "id")
    private var id: Int? = null,

    @Json(name = "email")
    private var email: String? = null,

    @Json(name = "first_name")
    var firstName: String? = null,

    @Json(name = "last_name")
    private var lastName: String? = null,

    @Json(name = "avatar")
    private var avatar: String? = null
)