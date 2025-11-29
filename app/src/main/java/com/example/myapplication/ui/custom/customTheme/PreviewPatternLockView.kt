package com.example.myapplication.ui.custom.customTheme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.myapplication.ui.custom.toPx

class PreviewPatternLockView : View {

    private val listDot = ArrayList<PointF>()

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#C4CDE2")
    }

    private val dotSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#C4CDE2")
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#C4CDE2")
        strokeWidth = 2f.toPx
    }

    val path = Path()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 10f.toPx.toInt()
        val viewRect = Rect(padding, padding, w - padding, h - padding)

        val dotMargin = viewRect.width() / 2
        for (x in 0..2) {
            for (y in 0..2) {
                val point = PointF(
                    (viewRect.left + x * dotMargin).toFloat(),
                    (viewRect.top + y * dotMargin).toFloat()
                )
                listDot.add(point)
            }
        }

        path.reset()
        path.moveTo(listDot[2].x, listDot[2].y)
        path.lineTo(listDot[0].x, listDot[0].y)
        path.lineTo(listDot[8].x, listDot[8].y)
        path.lineTo(listDot[6].x, listDot[6].y)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, linePaint)
        listDot.forEachIndexed { index, point ->
            if (index == 3 || index == 5) {
                canvas.drawCircle(point.x, point.y, 5f.toPx, dotPaint)
            } else {
                canvas.drawCircle(point.x, point.y, 5f.toPx, dotSelectedPaint)
            }
        }
    }

    fun setDotColor(dotColor: Int) {
        dotSelectedPaint.color = dotColor
        invalidate()
    }

    fun setLineColor(lineColor: Int) {
        linePaint.color = lineColor
        invalidate()
    }
}