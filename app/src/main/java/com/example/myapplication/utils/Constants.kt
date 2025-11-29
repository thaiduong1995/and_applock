package com.example.myapplication.utils


/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
object Constants {

    const val MIN_PASSWORD_LENGTH = 4
    const val DEFAULT_THEME = 0
    const val TYPE_PIN_4_digit = 0
    const val TYPE_PIN_6_digit = 1
    const val TYPE_KNOCK_CODE = 2
    const val TYPE_PATTERN = 3

    const val INTERNAL_STORAGE_PATH: String = "/storage/emulated/0/Android/data"
    const val CACHE_FOLDER_NAME = "cache"
    const val PACKAGE_TYPE: String = "Package"
    const val HOME: String = "Home"
    const val APK_SUFFIX: String = ".apk"
    const val UNKNOW: String = "Unknown"


    //bundle key
    const val KEY_TYPE_PASSWORD = "TYPE_PASSWORD"
    const val KEY_NEED_RESULT = "NEED_RESULT"
    const val KEY_RESULT_DATA = "RESULT_DATA"
    const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"
    const val KEY_BUNDLE = "BUNDLE"
    const val KEY_UNLOCK_SUCCESS = "UNLOCK_SUCCESS"
    const val KEY_LONG_CLICK_SUCCESS = "KEY_LONG_CLICK_SUCCESS"
    const val KEY_INTRUDER = "INTRUDER"
    const val KEY_TYPE_BROWSER = "KEY_TYPE_BROWSER"
    const val KEY_RESULT_HISTORY = "KEY_RESULT_HISTORY"
    const val KEY_UPDATE_LOCATION = "UPDATE_LOCATION"
    const val KEY_THEME_TOPIC = "THEME_TOPIC"
    const val KEY_THEME_PREVIEW = "THEME_PREVIEW"
    const val KEY_THEME_PREVIEW_TYPE = "THEME_PREVIEW_TYPE"
    const val KEY_POSITION = "POSITION"

    // fragment result key
    const val KEY_RESULT_TO_SECURITY_FRAGMENT = "RESULT_TO_SECURITY_FRAGMENT"
    const val KEY_RESULT_BROWSER_FRAGMENT = "KEY_RESULT_BROWSER_FRAGMENT"
    const val KEY_RESULT_MULTIPLE_FRAGMENT = "KEY_RESULT_MULTIPLE_FRAGMENT"
    const val KEY_CHANGE_LOCK_CONDITION = "CHANGE_LOCK_CONDITION"
    const val KEY_CHANGE_LOCATION = "KEY_CHANGE_LOCATION"
    const val KEY_CHANGE_DATA_LOCATION = "KEY_CHANGE_DATA_LOCATION"

    val listReCommentApp = listOf(
        "com.facebook.katana",
        "com.sec.android.gallery3d",
        "com.google.android.gm",
        "com.zing.zalo",
        "com.facebook.orca",
        "com.google.android.youtube",
        "com.microsoft.skydrive",
        " com.grabtaxi.passenger",
        "com.binance.dev",
        "com.fplay.activity",
        "com.gojek.app",
        "com.lazada.android",
        "com.shopee.vn",
        "com.viettel.tv360",
        "org.telegram.messenger",
        "vn.vtv.vtvgo"
    )

    //folder theme
    const val GRADIENT_FOLDER: String = "gradient"
    const val NATURE_FOLDER: String = "nature"
    const val DARK_MODE_FOLDER: String = "dark"
    const val MINIMAL_FOLDER: String = "minimal"
    const val COMIC_FOLDER: String = "comic"
    const val NAME_ICON_THEME: String = "preview.png"
    const val ASSET_PATH: String = "file:///android_asset/"

    //short cut
    const val ACTION_LOCK_APP = "action_lock_app"
    const val ACTION_WEB = "action_web"
    const val ACTION_CLEAN = "action_clean"
    const val ACTION_THEME = "action_theme"
    const val REGEX_TYPE_LOCK = 0
    const val REGEX_TYPE_WEB = 1
    const val REGEX_TYPE_CLEAN = 2
    const val REGEX_TYPE_THEME = 3

    const val VIEW_ACCESS = "VIEW_ACCESS"
}