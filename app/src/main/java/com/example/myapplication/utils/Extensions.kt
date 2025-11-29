package com.example.myapplication.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

object Extensions {

    inline fun <T : Any, R> T?.withNotNull(block: (T) -> R): R? {
        return this?.let(block)
    }

//    val nullableValue: String? = null
//    nullableValue.withNotNull { value ->
//        // Code here will only be executed if nullableValue is not null
//    }

//    fun <T> Flow<T>.toLiveData(): LiveData<T> {
//        return liveData {
//            collect {
//                emit(it)
//            }
//        }
//    }

//    val flow = flowOf("Hello", "World")
//    val liveData = flow.toLiveData()

    fun <T> Collection<T>?.notEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }
//    val list: List<Int> = emptyList()
//    if (list.notEmpty()) {
//        // Code here will only be executed if list is not empty
//    }

    fun <K, V> Map<K, V>.getOrThrow(key: K): V {
        return this[key] ?: throw NoSuchElementException("Key $key not found in map")
    }

//    val map = mapOf("key1" to "value1", "key2" to "value2")
//    val value = map.getOrThrow("key3")

    fun Int.toFormattedString(): String {
        return NumberFormat.getInstance().format(this)
    }

    fun Long.toFormattedString(): String {
        return NumberFormat.getInstance().format(this)
    }

    fun Date.toFormattedString(): String {
        return SimpleDateFormat.getDateInstance().format(this)
    }

    fun View.onClick(debounceDuration: Long = 500L, action: (View) -> Unit) {
        setOnClickListener(DebouncedOnClickListener(debounceDuration) {
            action(it)
        })
    }

    class DebouncedOnClickListener(
        private val debounceDuration: Long,
        private val clickAction: (View) -> Unit
    ) : View.OnClickListener {

        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime >= debounceDuration) {
                lastClickTime = now
                clickAction(v)
            }
        }
    }

//    button.onClick(debounceDuration = 500L) {
//        // Code here will only be executed if 500 milliseconds have passed since the last click
//    }

    fun Drawable.toBitmap(): Bitmap {
        if (this is BitmapDrawable) {
            return bitmap
        }

        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)

        return bitmap
    }

    inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
        return if (condition) {
            this.apply(block)
        } else {
            this
        }
    }

    val number = 5
    val formattedNumber = number.applyIf(number > 10) {
        toFormattedString()
    }
}