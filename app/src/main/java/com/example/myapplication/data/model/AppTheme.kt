package com.example.myapplication.data.model

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.example.myapplication.R
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Utils
import kotlinx.parcelize.Parcelize

/**
 * Created by Thinhvh on 07/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Keep
enum class AppTheme(
    var themePath: String = "",
    var themeId: Int = 0,
    var patterSelector: Boolean = false,
    var lineColor: Int = Color.WHITE,
    var lineWidth: Float = 6f,
    val dotSize: Float = 24f.toPx,
) {
    DEFAULT(Utils.getThemeTopicPath("default"), Constants.DEFAULT_THEME),

    GRADIANT_1(
        "${Utils.getThemeTopicPath("gradient")}gradient_1/",
        1,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    GRADIANT_2(
        "${Utils.getThemeTopicPath("gradient")}gradient_2/",
        2,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    GRADIANT_3(
        "${Utils.getThemeTopicPath("gradient")}gradient_3/",
        3,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    GRADIANT_4(
        "${Utils.getThemeTopicPath("gradient")}gradient_4/",
        4,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    GRADIANT_5(
        "${Utils.getThemeTopicPath("gradient")}gradient_5/",
        5,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),

    NATURE_1(
        "${Utils.getThemeTopicPath("nature")}nature_1/",
        6,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    NATURE_2(
        "${Utils.getThemeTopicPath("nature")}nature_2/",
        7,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    NATURE_3(
        "${Utils.getThemeTopicPath("nature")}nature_3/",
        8,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    NATURE_4(
        "${Utils.getThemeTopicPath("nature")}nature_4/",
        9,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    NATURE_5(
        "${Utils.getThemeTopicPath("nature")}nature_5/",
        10,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),


    DARK_1(
        "${Utils.getThemeTopicPath("dark")}dark_1/",
        11,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    DARK_2(
        "${Utils.getThemeTopicPath("dark")}dark_2/",
        12,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    DARK_3(
        "${Utils.getThemeTopicPath("dark")}dark_3/",
        13,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    DARK_4(
        "${Utils.getThemeTopicPath("dark")}dark_4/",
        14,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    DARK_5(
        "${Utils.getThemeTopicPath("dark")}dark_5/",
        15,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),

    MINIMAL_1(
        "${Utils.getThemeTopicPath("minimal")}minimal_1/",
        16,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    MINIMAL_2(
        "${Utils.getThemeTopicPath("minimal")}minimal_2/",
        17,
        true,
        lineColor = Color.parseColor("#804F40"),
        lineWidth = 6f
    ),
    MINIMAL_3(
        "${Utils.getThemeTopicPath("minimal")}minimal_3/",
        18,
        true,
        lineColor = Color.parseColor("#192A30"),
        lineWidth = 6f
    ),
    MINIMAL_4(
        "${Utils.getThemeTopicPath("minimal")}minimal_4/",
        19,
        true,
        lineColor = Color.parseColor("#666666"),
        lineWidth = 6f
    ),
    MINIMAL_5(
        "${Utils.getThemeTopicPath("minimal")}minimal_5/",
        20,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),

    COMIC_1(
        "${Utils.getThemeTopicPath("comic")}comic_1/",
        21,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    COMIC_2(
        "${Utils.getThemeTopicPath("comic")}comic_2/",
        22,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    COMIC_3(
        "${Utils.getThemeTopicPath("comic")}comic_3/",
        23,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    COMIC_4(
        "${Utils.getThemeTopicPath("comic")}comic_4/",
        24,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
    COMIC_5(
        "${Utils.getThemeTopicPath("comic")}comic_5/",
        25,
        true,
        lineColor = Color.WHITE,
        lineWidth = 6f
    ),
}

@Keep
enum class ThemeTopic(
    val id: Int, @StringRes val resName: Int, val folderName: String
) {
    GRADIENT(1, R.string.gradient, Constants.GRADIENT_FOLDER), NATURE(
        2,
        R.string.nature,
        Constants.NATURE_FOLDER
    ),
    DARK_MODE(3, R.string.dark_mode, Constants.DARK_MODE_FOLDER), MINIMAL(
        4,
        R.string.minimal,
        Constants.MINIMAL_FOLDER
    ),
    COMIC(5, R.string.comic, Constants.COMIC_FOLDER)
}

@Keep
@Parcelize
data class ThemePreview(
    var themeId: Int,
    var image: ArrayList<String> = arrayListOf()
) : Parcelable

@Keep
@Parcelize
data class ThemeDetailPreview(var isVip: Boolean, var image: String) : Parcelable

@Keep
enum class LockType(val id: Int) {
    PASS_CODE(1), PATTERN(0), KNOCK(2)
}