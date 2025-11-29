package com.example.myapplication.data.model

import androidx.annotation.Keep

@Keep
data class BrowserItem(var title: Int = 0, val mList: MutableList<MediaItem> = mutableListOf())