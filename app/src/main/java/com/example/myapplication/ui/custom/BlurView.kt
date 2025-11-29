package com.example.myapplication.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class BlurView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


//    private fun takeScreenShot(activity: Activity): Bitmap? {
//        val view = activity.window.decorView
//        view.isDrawingCacheEnabled = true
//        view.buildDrawingCache()
//        val b1 = view.drawingCache
//        val frame = Rect()
//        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
//        val statusBarHeight = frame.top
//        val width = activity.windowManager.defaultDisplay.width
//        val height = activity.windowManager.defaultDisplay.height
//
//        val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
//        view.destroyDrawingCache()
//        return b
//    }
//
//    fun blur(activity: Activity) {
//        val bitmap: Bitmap? = takeScreenShot(activity)
//        val renderScript = RenderScript.create(activity)
//        val input = Allocation.createFromBitmap(
//            renderScript,
//            bitmap
//        )
//        val output = Allocation.createTyped(renderScript, input.type)
//        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
//        script.setRadius(16f)
//        script.setInput(input)
//        script.forEach(output)
//        output.copyTo(bitmap)
//        setImageBitmap(bitmap)
//    }
}