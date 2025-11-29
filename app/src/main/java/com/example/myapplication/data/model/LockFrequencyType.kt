package com.example.myapplication.data.model

import androidx.annotation.Keep

/**
 * Created by Thinhvh on 30/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
enum class LockFrequencyType(var id: Int = 0, var time: Long = 0) {
    ALWAYS(0, -1),
    AFTER_SCREEN_LOCK(1, -1),
    TEN_SECONDS(2, 10000),
    THIRTY_SECONDS(3, 30 * 1000),
    ONE_MINUTE(4, 60 * 1000),
    THREE_MINUTE(5, 3 * 60 * 1000),
    FIVE_MINUTE(6, 5 * 60 * 1000),
    TEN_MINUTE(7, 10 * 60 * 1000)
}