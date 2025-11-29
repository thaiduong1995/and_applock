package com.example.myapplication.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.util.Calendar

fun Calendar.clearTime(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}

fun String?.stringToBitMap(): Bitmap? {
    val imageBytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}