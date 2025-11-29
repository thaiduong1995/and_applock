package  com.example.myapplication.extention

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader

fun Bitmap.createCircularClip(width: Int, height: Int): Bitmap {
    val inWidth = width
    val inHeight = height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint()
    paint.shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.isAntiAlias = true
    val srcRect = RectF(0f, 0f, inWidth.toFloat(), inHeight.toFloat())
    val dstRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val m = Matrix()
    m.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER)
    canvas.setMatrix(m)
    canvas.drawCircle(inWidth / 2f, inHeight / 2f, inWidth / 2f, paint)
    return output
}