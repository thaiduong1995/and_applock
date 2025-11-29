package com.cem.admodule.data

import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Created by Hưng Nguyễn on 20/12/2023
 * Phone: 0335236374
 * Email: nguyenhunghung2806@gmail.com
 */
@Keep
@Parcelize
data class BannerLoaded(
    var timeLoaded : Long,
    var isLoaded : Boolean,
    var isClosed : Boolean
) : BaseResponseModel()
