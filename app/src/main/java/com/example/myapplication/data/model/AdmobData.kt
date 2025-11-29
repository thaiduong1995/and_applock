package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
sealed class AdmobData {
    data object BannerView : AdmobData()
    data object NativeView : AdmobData()
}