package com.example.myapplication.extention

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.webkit.MimeTypeMap
import com.example.myapplication.R
import com.example.myapplication.data.model.FileApp
import com.example.myapplication.utils.Constants
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

fun String.getKnockCodeLength(): Int {
    var passwordLength = 0
    this.toIntArray().forEachIndexed { index, value ->
        if (value != -1) {
            passwordLength = index + 1
        }
    }

    return passwordLength
}

fun Location.getAddress(context: Context): String {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            return addresses[0].getAddressLine(0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return ""
    }
    return ""
}

fun Location.getFromLocationName(context: Context): String {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            Timber.d(Gson().toJson(addresses))
            return addresses.first().featureName ?: context.getString(R.string.location)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return context.getString(R.string.location)
    }
    return context.getString(R.string.location)
}

fun File.isCacheFolder(): Boolean {
    return name.lowercase().contains(Constants.CACHE_FOLDER_NAME.lowercase()) && isDirectory
}

fun File.convertToFileApp(): FileApp? {
    if (this.exists()) {
        val name =
            if (this.absolutePath == getInternalStoragePath()) Constants.HOME else this.name
        return if (!name.startsWith(".")) {
            val path = this.path
            val size = this.length()
            val dateModified = this.lastModified()
            val mimeType = this.getMimeType()
            FileApp(
                name = name,
                path = path,
                size = size,
                type = mimeType,
                dateModified = dateModified
            )
        } else null
    }
    return null
}

fun getInternalStoragePath() =
    if (File("/storage/emulated/0").exists()) "/storage/emulated/0" else Environment.getExternalStorageDirectory().absolutePath.trimEnd(
        '/'
    )

fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(lowercase()) }
        ?: fallback
}

fun FileApp.isPackageFolder(): Boolean {
    return type.lowercase() == Constants.PACKAGE_TYPE.lowercase()
}

@SuppressLint("Range")
fun Cursor.getStringValue(key: String) = getString(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getStringValueOrNull(key: String) =
    if (isNull(getColumnIndex(key))) null else getString(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getIntValue(key: String) = getInt(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getIntValueOrNull(key: String) =
    if (isNull(getColumnIndex(key))) null else getInt(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getLongValue(key: String) = getLong(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getLongValueOrNull(key: String) =
    if (isNull(getColumnIndex(key))) null else getLong(getColumnIndex(key))

@SuppressLint("Range")
fun Cursor.getBlobValue(key: String) = getBlob(getColumnIndex(key))

fun ArrayList<FileApp>.length(): Long {
    var size = 0L
    onEach { fileApp ->
        size += fileApp.size
    }
    return size
}

fun ArrayList<FileApp>.getSizeString(): String {
    var size = 0L
    onEach { fileApp ->
        size += fileApp.size
    }
    return size.sizeFormat()
}

fun Long.sizeFormat(): String {
    var result = this.toDouble() / 1024
    if (result < 1024) return "${result.roundToInt()} KB"
    result /= 1024
    if (result < 1024) return String.format("%.2f MB", result)
    result /= 1024
    return String.format("%.2f GB", result)
}

fun String.toMilliseconds(): Long {
    return try {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = dateFormat.parse(this)

        val calendar = Calendar.getInstance()
        calendar.time = date

        // Lấy giờ và phút từ calendar
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if (hour <= 12 && this.endsWith("PM")) {
            hour += 12
        }
        // Chuyển đổi sang milliseconds
        val milliseconds = (hour * 60 + minute) * 60 * 1000

        milliseconds.toLong()
    } catch (e: Exception) {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = dateFormat.parse(this)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        calendar.timeInMillis
    }
}

fun <T> List<T>.toArrayList(): ArrayList<T>{
    return ArrayList(this)
}