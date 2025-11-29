@file:Suppress("DEPRECATION")

package  com.example.myapplication.extention

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Parcelable> Bundle.getArgumentParcelable(key: String): T? {
    return try {
        if (Build.VERSION.SDK_INT >= 33) {
            getParcelable(key, T::class.java)
        } else {
            getParcelable(key) as? T?
        }
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T : Parcelable> Bundle.getArgumentParcelableArray(key: String): List<T> {
    return try {
        if (Build.VERSION.SDK_INT >= 33) {
            getParcelableArray(key, T::class.java)?.toList() ?: listOf()
        } else {
            val list = arrayListOf<T>()
            getParcelableArray(key)?.map {
                if (it is T) list.add(it)
            }
            list
        }
    } catch (e: Exception) {
        listOf()
    }
}

inline fun <reified T : Serializable> Bundle.getArgumentSerializable(key: String): T? {
    return try {
        if (Build.VERSION.SDK_INT >= 33) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T?
        }
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T : Parcelable> Intent.getDataParcelable(key: String): T? {
    return try {
        if (Build.VERSION.SDK_INT >= 33) {
            getParcelableExtra(key, T::class.java)
        } else {
            getParcelableExtra(key) as? T?
        }
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T : Serializable> Intent.getDataSerializable(key: String): T? {
    return try {
        if (Build.VERSION.SDK_INT >= 33) {
            getSerializableExtra(key, T::class.java)
        } else {
            getSerializableExtra(key) as? T?
        }
    } catch (e: Exception) {
        null
    }
}