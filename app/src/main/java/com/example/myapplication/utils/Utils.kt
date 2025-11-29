package com.example.myapplication.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.R
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.AppTheme
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_1
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_2
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_3
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_4
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_5
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_6
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_7
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_8
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_9
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_NORMAL
import com.example.myapplication.data.model.ThemeData.PATTERN_DOT_SELECTED
import com.example.myapplication.data.model.ThemeData.PREVIEW_KNOCK
import com.example.myapplication.data.model.ThemeData.PREVIEW_PATTERN
import com.example.myapplication.data.model.ThemeData.PREVIEW_PIN
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.data.model.TimeItem
import com.example.myapplication.extention.toMilliseconds
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    const val DATE_FORMAT_FULL = "dd-MM-yyyy hh:mm:ss"
    const val DATE_FORMAT = "dd-MM-yyyy"
    const val TIME_FORMAT = "hh:mm:ss"
    const val THEME = "theme/"

    fun getAssetUri(themeId: Int): String {
        return AppTheme.entries.find { it.themeId == themeId }?.themePath ?: ""
    }

    fun getAssetPath(themeId: Int): String {
        return AppTheme.entries.find { it.themeId == themeId }?.themePath ?: ""
    }

    fun getThemeTopicPath(topicName: String): String {
        return "$THEME$topicName/"
    }

    fun getImagePreviewThemeTopic(topicName: String): String {
        return "${getThemeTopicPath(topicName)}${Constants.NAME_ICON_THEME}"
    }

    fun getThemePreview(appTheme: AppTheme): ThemePreview {
        val imgPin = appTheme.themePath + PREVIEW_PIN
        val imgPattern = appTheme.themePath + PREVIEW_PATTERN
        val imgKnockCode = appTheme.themePath + PREVIEW_KNOCK
        return ThemePreview(appTheme.themeId, arrayListOf(imgPin, imgPattern, imgKnockCode))
    }

    fun getPatternDotDrawable(
        context: Context,
        themeId: Int, pos: Int
    ): Drawable? {
        when (pos) {
            0 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_1),
                null
            )

            1 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_2),
                null
            )

            2 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_3),
                null
            )

            3 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_4),
                null
            )

            4 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_5),
                null
            )

            5 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_6),
                null
            )

            6 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_7),
                null
            )

            7 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_8),
                null
            )

            8 -> return Drawable.createFromStream(
                context.assets.open(getAssetUri(themeId) + PATTERN_DOT_9),
                null
            )

            else -> return ColorDrawable(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }

    //
    fun getPatternDotDrawable(
        context: Context,
        themeId: Int,
        isSelected: Boolean
    ): Drawable? {
        try {
            if (isSelected) {
                return Drawable.createFromStream(
                    context.assets.open(getAssetUri(themeId) + PATTERN_DOT_SELECTED),
                    null
                )
            } else {
                return Drawable.createFromStream(
                    context.assets.open(getAssetUri(themeId) + PATTERN_DOT_NORMAL),
                    null
                )
            }
        } catch (e: java.lang.Exception) {
            Log.d("thinhvh", "getPatternDotDrawable error: ${e.message}")
        }

        return ColorDrawable(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
    }

    fun hideKeyboard(activity: FragmentActivity?, view: View?) {
        activity?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    fun getAppInfo(context: Context, pkgName: String): AppData? {
        try {
            if (pkgName.isNotEmpty()) {
                val app: ApplicationInfo =
                    context.packageManager.getApplicationInfo(pkgName, 0)
                val name: String =
                    context.packageManager.getApplicationLabel(app).toString()
                return AppData(pkgName, name)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
        return null
    }

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun getPatternString(ids: ArrayList<Int>): String {
        var result = ""
        for (id in ids) {
            result += id.toString()
        }
        return result
    }


    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)

            bitmap = BitmapFactory.decodeStream(inputStream)
        } finally {
            inputStream?.close()
        }
        return bitmap
    }

    fun getTimeComponents(timeString: String): Triple<Int, Int, String> {
        val timeComponents = timeString.split(" ")
        val hourMinute = timeComponents[0].split(":")
        val hour = hourMinute[0].toInt()
        val minute = hourMinute[1].toInt()
        val amPm = timeComponents[1]

        return Triple(hour, minute, amPm)
    }

    fun isCurrentTimeInsideTimeRange(timeItem: TimeItem): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val currentDayOfWeek = getDayOfWeekAsString(calendar.get(Calendar.DAY_OF_WEEK))

        val currentMilliseconds = formatTimeFromMilliseconds(calendar.timeInMillis)
        if (timeItem.day.contains(currentDayOfWeek)) {
            val startTime = timeItem.startTime.toMilliseconds()
            val endTime = timeItem.endTime.toMilliseconds()
            return currentMilliseconds.toMilliseconds() in startTime..endTime
        }
        return false
    }

    fun formatTimeFromMilliseconds(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return dateFormat.format(calendar.time)
    }

    fun getDayOfWeekAsString(dayOfWeek: Int): String {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        return daysOfWeek[dayOfWeek - 1]
    }

    private fun getTimeInMillis(time: String): Long {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = format.parse(time)
        return date?.time ?: 0
    }

    fun getWifiMacAddress(context: Context): String {
        val info = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return info.connectionInfo.bssid
    }

    fun getBitmapFromViewGroup(viewGroup: ViewGroup): Bitmap {
        val bitmap = Bitmap.createBitmap(
            viewGroup.width,
            viewGroup.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        viewGroup.draw(canvas)
        return bitmap
    }
}