package com.example.myapplication.ui.custom.passwordView.patternLock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.example.myapplication.extention.dp2px

/**
 * Created by Thinhvh on 30/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
internal class Cell(
    context: Context,
    var index: Int,
    var regularCellBackground: Drawable?,
    var regularDotColor: Int,
    var regularDotRadiusRatio: Float,
    var selectedCellBackground: Drawable?,
    var selectedDotColor: Int,
    var selectedDotRadiusRatio: Float,
    var errorCellBackground: Drawable?,
    var errorDotColor: Int,
    var errorDotRadiusRatio: Float,
    var lineStyle: Int,
    var regularLineColor: Int,
    var errorLineColor: Int,
    var columnCount: Int,
    var indicatorSizeRatio: Float,
    var isCustomDraw: Boolean = false
) : View(context) {

    private var currentState: State = State.REGULAR
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentDegree: Float = -1f
    private var indicatorPath: Path = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var cellWidth = MeasureSpec.getSize(widthMeasureSpec) / columnCount
        var cellHeight = cellWidth
        setMeasuredDimension(cellWidth, cellHeight)
    }

    override fun onDraw(canvas: Canvas) {
//        canvas?.drawRect(Rect(0,0,width,height), Paint().apply {
//            style = Paint.Style.FILL
//            color = Color.RED
//        })
//
//        canvas?.drawRect(Rect(paddingLeft,paddingTop,width - paddingRight,height- paddingBottom), Paint().apply {
//            style = Paint.Style.FILL
//            color = Color.GREEN
//        })
        when (currentState) {
            State.REGULAR -> drawDot(
                canvas,
                regularCellBackground,
                regularDotColor,
                regularDotRadiusRatio
            )

            State.SELECTED -> drawDot(
                canvas,
                selectedCellBackground,
                selectedDotColor,
                selectedDotRadiusRatio
            )

            State.ERROR -> drawDot(canvas, errorCellBackground, errorDotColor, errorDotRadiusRatio)
        }
    }

    private fun drawDot(
        canvas: Canvas?,
        background: Drawable?,
        dotColor: Int,
        radiusRation: Float
    ) {
        var radius = getRadius()
        var centerX = width / 2
        var centerY = height / 2


        //draw bg white alpha
        if (currentState == State.SELECTED && isCustomDraw.not()) {
            var paint2 = Paint()
            paint2.color = adjustAlpha(selectedDotColor, 0.3f)
            canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius * 0.4f, paint2)
        }

        // draw cell white
        if (isCustomDraw.not()) {
            paint.color = dotColor
            paint.style = Paint.Style.FILL
            canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius * radiusRation, paint)
        }

        // draw line
        if (lineStyle == PatternLockView.LINE_STYLE_INDICATOR &&
            (currentState == State.SELECTED || currentState == State.ERROR)
        ) {
            drawIndicator(canvas)
        }

        if (background is ColorDrawable) {
            paint.color = background.color
            paint.style = Paint.Style.FILL
            canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
        } else {
            background?.setBounds(
                paddingLeft,
                paddingTop,
                context.dp2px(40f),
                context.dp2px(40f)
            )
            background?.let {
                canvas?.let { cv ->
                    cv.save()
                    cv.translate(
                        centerX.toFloat() - context.dp2px(20f) - paddingLeft / 2,
                        centerY.toFloat() - context.dp2px(20f) - paddingTop / 2
                    )
                    background.draw(cv)
                    cv.restore()
                }
            }
        }
    }

    private fun drawIndicator(canvas: Canvas?) {
        if (currentDegree != -1f) {
            if (indicatorPath.isEmpty) {
                indicatorPath.fillType = Path.FillType.WINDING
                val radius = getRadius()
                val height = radius * indicatorSizeRatio
                indicatorPath.moveTo(
                    (width / 2).toFloat(),
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + paddingTop
                )
                indicatorPath.lineTo(
                    (width / 2).toFloat() - height,
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop
                )
                indicatorPath.lineTo(
                    (width / 2).toFloat() + height,
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop
                )
                indicatorPath.close()
            }

            if (currentState == State.SELECTED) {
                paint.color = regularLineColor
            } else {
                paint.color = errorLineColor
            }
            paint.style = Paint.Style.FILL

            canvas?.save()
            canvas?.rotate(currentDegree, (width / 2).toFloat(), (height / 2).toFloat())
            canvas?.drawPath(indicatorPath, paint)
            canvas?.restore()
        }
    }

    fun getRadius(): Int {
        return (Math.min(width, height) - (paddingLeft + paddingRight)) / 2
    }


    fun getCenter(): Point {
        var point = Point()
        point.x = left + (right - left) / 2
        point.y = top + (bottom - top) / 2
        return point
    }

    fun setState(state: State) {
        currentState = state
        invalidate()
    }

    fun setDegree(degree: Float) {
        currentDegree = degree
    }

    fun reset() {
        setState(State.REGULAR)
        currentDegree = -1f
    }

    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}