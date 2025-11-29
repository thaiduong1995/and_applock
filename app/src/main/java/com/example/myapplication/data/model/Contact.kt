package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
data class Contact(
    var id: String = "",
    var name: String = "",
    val phoneNumber: ArrayList<String> = arrayListOf()
)