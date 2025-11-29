package com.suntech.mytools.tools

import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager

object DimensUtil {
    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun screenWidth(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    fun screenHeight(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }


    val Context.screenSize: Size
        get() {
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = windowManager.currentWindowMetrics
                val windowInsets = metrics.windowInsets
                val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                )

                val insetsWidth: Int = insets.right + insets.left
                val insetsHeight: Int = insets.top + insets.bottom
                val bounds: Rect = metrics.bounds
                Size(
                    bounds.width() - insetsWidth,
                    bounds.height() - insetsHeight
                )
            } else {
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay?.getMetrics(displayMetrics)
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                Size(width, height)
            }
            return size
        }
}