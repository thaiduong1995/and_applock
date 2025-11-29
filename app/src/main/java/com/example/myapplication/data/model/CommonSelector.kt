package com.example.myapplication.data.model

import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
sealed class CommonSelector(
    var name: String = "",
    var value: Int = 0,
    var isSelected: Boolean = false,
    @StringRes var idString: Int? = null,
)

@Keep
class Frequency(
    idString: Int, name: String, value: Int, isSelected: Boolean = false
) : CommonSelector(name, value, isSelected, idString)

@Keep
class LockAnimation(
    idString: Int, name: String, value: Int, isSelected: Boolean = false
) : CommonSelector(name, value, isSelected, idString)

@Keep
class RecommendSignal(
    name: String, @StringRes var resId: Int, isSelected: Boolean = false
) : CommonSelector(name = name, idString = resId, isSelected = isSelected, value = -1)

@Keep
class UnlockCount(
    name: String,
    @StringRes var resId: Int,
    value: Int,
    isSelected: Boolean = false
) : CommonSelector(name = name, value = value, isSelected = isSelected, idString = resId)