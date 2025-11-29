package com.cem.admodule.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
open class BaseResponseModel : Parcelable {
    companion object {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }
}