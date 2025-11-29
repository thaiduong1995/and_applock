package com.example.myapplication.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.example.myapplication.R

@Keep
enum class Tools(@StringRes var resToolName: Int = 0, @DrawableRes var resIcon: Int = 0) {
    FAKE_COVER(R.string.fake_cover, R.drawable.ic_tools_fake_cover),

    //    CLEANER(R.string.cleaner, R.drawable.ic_tools_cleaner),
    FAKE_APP_ICONS(R.string.fake_app_icons, R.drawable.ic_tools_fake_app_icons),
    WEB_BROWSER(R.string.web_browser, R.drawable.ic_tools_web_browser),

    //    LOCATION_LOCK(R.string.location_based_lock, R.drawable.ic_tools_location_lock),
//    TIME_LOCK(R.string.time_based_lock, R.drawable.ic_tools_time_lock),
//    WIFI_LOCK(R.string.wifi_based_lock, R.drawable.ic_tools_wifi_lock),
    CAPTURE_INTRUDER(R.string.capture_intruder, R.drawable.ic_tools_capture_intruders),
}