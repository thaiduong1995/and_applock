package com.example.myapplication.ui.custom.passwordView.patternLock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.data.model.AppTheme
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.extention.dp2px
import com.example.myapplication.ui.custom.passwordView.InputPasswordListener
import com.example.myapplication.utils.Constants.DEFAULT_THEME
import com.example.myapplication.utils.Utils
import com.google.gson.Gson

/**
 * Created by Thinhvh on 30/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class PatternLockView : GridLayout {
    private var regularCellBackground: Drawable? = null
    private var regularDotColor: Int = 0
    private var regularDotRadiusRatio: Float = 0f

    private var selectedCellBackground: Drawable? = null
    private var selectedDotColor: Int = 0
    private var selectedDotRadiusRatio: Float = 0f

    private var errorCellBackground: Drawable? = null
    private var errorDotColor: Int = 0
    private var errorDotRadiusRatio: Float = 0f
    private var isCanTouch = true

    private var lineStyle: Int = 0

    private var lineWidth: Int = 0
    private var regularLineColor: Int = 0
    private var errorLineColor: Int = 0

    private var spacing: Int = 0

    private var plvRowCount: Int = 0
    private var plvColumnCount: Int = 0

    private var errorDuration: Int = 0
    private var hitAreaPaddingRatio: Float = 0f
    private var indicatorSizeRatio: Float = 0f

    private var cells: ArrayList<Cell> = ArrayList()
    private var selectedCells: ArrayList<Cell> = ArrayList()

    private var linePaint: Paint = Paint()
    private var linePath: Path = Path()

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private var isSecureMode = false

    private var inputPasswordListener: InputPasswordListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.PatternLockView)
        regularDotColor = ta.getColor(
            R.styleable.PatternLockView_plv_regularDotColor,
            ContextCompat.getColor(context, R.color.regularColor)
        )
        regularDotRadiusRatio =
            ta.getFloat(R.styleable.PatternLockView_plv_regularDotRadiusRatio, DEFAULT_RADIUS_RATIO)

        selectedDotColor = ta.getColor(
            R.styleable.PatternLockView_plv_selectedDotColor,
            ContextCompat.getColor(context, R.color.selectedColor)
        )
        selectedDotRadiusRatio = ta.getFloat(
            R.styleable.PatternLockView_plv_selectedDotRadiusRatio, DEFAULT_RADIUS_RATIO
        )

        errorDotColor = ta.getColor(
            R.styleable.PatternLockView_plv_errorDotColor,
            ContextCompat.getColor(context, R.color.errorColor)
        )
        errorDotRadiusRatio =
            ta.getFloat(R.styleable.PatternLockView_plv_errorDotRadiusRatio, DEFAULT_RADIUS_RATIO)

        lineStyle = ta.getInt(R.styleable.PatternLockView_plv_lineStyle, 1)
        lineWidth = ta.getDimensionPixelSize(
            R.styleable.PatternLockView_plv_lineWidth, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, context.resources.displayMetrics
            ).toInt()
        )
        regularLineColor = ta.getColor(
            R.styleable.PatternLockView_plv_regularLineColor,
            ContextCompat.getColor(context, R.color.selectedColor)
        )
        errorLineColor = ta.getColor(
            R.styleable.PatternLockView_plv_errorLineColor,
            ContextCompat.getColor(context, R.color.errorColor)
        )

        spacing = ta.getDimensionPixelSize(
            R.styleable.PatternLockView_plv_spacing, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SPACING, context.resources.displayMetrics
            ).toInt()
        )

        plvRowCount = ta.getInteger(R.styleable.PatternLockView_plv_rowCount, DEFAULT_ROW_COUNT)
        plvColumnCount =
            ta.getInteger(R.styleable.PatternLockView_plv_columnCount, DEFAULT_COLUMN_COUNT)

        errorDuration =
            ta.getInteger(R.styleable.PatternLockView_plv_errorDuration, DEFAULT_ERROR_DURATION)
        hitAreaPaddingRatio = ta.getFloat(
            R.styleable.PatternLockView_plv_hitAreaPaddingRatio, DEFAULT_HIT_AREA_PADDING_RATIO
        )
        indicatorSizeRatio = ta.getFloat(
            R.styleable.PatternLockView_plv_indicatorSizeRatio, DEFAULT_INDICATOR_SIZE_RATIO
        )

        ta.recycle()

        rowCount = plvRowCount
        columnCount = plvColumnCount

        setupCells()
        initPathPaint()
        setThemeId(DEFAULT_THEME)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isCanTouch) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    var hitCell = getHitCell(event.x.toInt(), event.y.toInt())
                    if (hitCell == null) {
                        return false
                    } else {
                        inputPasswordListener?.onStartInput()
                        notifyCellSelected(hitCell)
                    }
                }

                MotionEvent.ACTION_MOVE -> handleActionMove(event)

                MotionEvent.ACTION_UP -> onFinish()

                MotionEvent.ACTION_CANCEL -> reset()

                else -> return false
            }
        }
        return true
    }

    private fun handleActionMove(event: MotionEvent) {
        var hitCell = getHitCell(event.x.toInt(), event.y.toInt())
        if (hitCell != null) {
            if (!selectedCells.contains(hitCell)) {
                notifyCellSelected(hitCell)
            }
        }

        lastX = event.x
        lastY = event.y

        invalidate()
    }

    private fun notifyCellSelected(cell: Cell) {
        inputPasswordListener?.onInputting()
        selectedCells.add(cell)
        if (isSecureMode) return
        cell.setState(State.SELECTED)
        val center = cell.getCenter()
        if (selectedCells.size == 1) {
            if (lineStyle == LINE_STYLE_COMMON) {
                linePath.moveTo(center.x.toFloat(), center.y.toFloat())
            }
        } else {
            if (lineStyle == LINE_STYLE_COMMON) {
                linePath.lineTo(center.x.toFloat(), center.y.toFloat())
            } else if (lineStyle == LINE_STYLE_INDICATOR) {
                var previousCell = selectedCells[selectedCells.size - 2]
                var previousCellCenter = previousCell.getCenter()
                var diffX = center.x - previousCellCenter.x
                var diffY = center.y - previousCellCenter.y
                var radius = cell.getRadius()
                var length = Math.sqrt((diffX * diffX + diffY * diffY).toDouble())

                linePath.moveTo(
                    (previousCellCenter.x + radius * diffX / length).toFloat(),
                    (previousCellCenter.y + radius * diffY / length).toFloat()
                )
                linePath.lineTo(
                    (center.x - radius * diffX / length).toFloat(),
                    (center.y - radius * diffY / length).toFloat()
                )

                val degree = Math.toDegrees(Math.atan2(diffY.toDouble(), diffX.toDouble())) + 90
                previousCell.setDegree(degree.toFloat())
                previousCell.invalidate()
            }
        }
    }


    override fun dispatchDraw(canvas: Canvas) {
        if (isSecureMode) return super.dispatchDraw(canvas)
        Log.d("thinhvh12", "dispatchDraw: ")
        canvas.drawPath(linePath, linePaint)
        if (selectedCells.size > 0 && lastX > 0 && lastY > 0) {
            if (lineStyle == LINE_STYLE_COMMON) {
                val center = selectedCells[selectedCells.size - 1].getCenter()
                canvas.drawLine(center.x.toFloat(), center.y.toFloat(), lastX, lastY, linePaint)
            } else if (lineStyle == LINE_STYLE_INDICATOR) {
                val lastCell = selectedCells[selectedCells.size - 1]
                val lastCellCenter = lastCell.getCenter()
                val radius = lastCell.getRadius()

                if (!(lastX >= lastCellCenter.x - radius && lastX <= lastCellCenter.x + radius && lastY >= lastCellCenter.y - radius && lastY <= lastCellCenter.y + radius)) {
                    val diffX = lastX - lastCellCenter.x
                    val diffY = lastY - lastCellCenter.y
                    val length = Math.sqrt((diffX * diffX + diffY * diffY).toDouble())
                    canvas.drawLine(
                        (lastCellCenter.x + radius * diffX / length).toFloat(),
                        (lastCellCenter.y + radius * diffY / length).toFloat(),
                        lastX,
                        lastY,
                        linePaint
                    )
                }
            }
        }
        super.dispatchDraw(canvas)
    }

    private fun setupCells() {
        cells.clear()
        for (i in 0..<plvRowCount) {
            for (j in 0..<plvColumnCount) {
                val cell = Cell(
                    context,
                    i * plvColumnCount + j,
                    regularCellBackground,
                    regularDotColor,
                    regularDotRadiusRatio,
                    selectedCellBackground,
                    selectedDotColor,
                    selectedDotRadiusRatio,
                    errorCellBackground,
                    errorDotColor,
                    errorDotRadiusRatio,
                    lineStyle,
                    regularLineColor,
                    errorLineColor,
                    plvColumnCount,
                    indicatorSizeRatio
                )
                val cellPadding = spacing / 2
                cell.setPadding(cellPadding, cellPadding, cellPadding, cellPadding)
                addView(cell)

                cells.add(cell)
            }
        }
    }

    private fun initPathPaint() {
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeCap = Paint.Cap.ROUND

        linePaint.strokeWidth = lineWidth.toFloat()
        linePaint.color = regularLineColor
    }

    fun reset() {
        enableTouch()
        for (cell in selectedCells) {
            cell.reset()
        }
        selectedCells.clear()
        linePaint.color = regularLineColor
        linePath.reset()

        lastX = 0f
        lastY = 0f

        invalidate()
    }

    fun setSecureMode(boolean: Boolean) {
        isSecureMode = boolean
    }

    private fun getHitCell(x: Int, y: Int): Cell? {
        for (cell in cells) {
            if (isSelected(cell, x, y)) {
                return cell
            }
        }
        return null
    }

    private fun isSelected(view: View, x: Int, y: Int): Boolean {
        val innerPadding = view.width * hitAreaPaddingRatio
        return x >= view.left + innerPadding && x <= view.right - innerPadding && y >= view.top + innerPadding && y <= view.bottom - innerPadding
    }

    private fun onFinish() {
        lastX = 0f
        lastY = 0f
        inputPasswordListener?.onInputComplete(Gson().toJson(generateSelectedIds()))
    }

    private fun generateSelectedIds(): ArrayList<Int> {
        var result = ArrayList<Int>()
        for (cell in selectedCells) {
            result.add(cell.index)
        }
        return result
    }

    fun onError() {
        if (isSecureMode) {
            reset()
            return
        }
        for (cell in selectedCells) {
            cell.setState(State.ERROR)
        }
        linePaint.color = errorLineColor
        invalidate()
        reset()
    }

    fun setInputPasswordListener(listener: InputPasswordListener) {
        this.inputPasswordListener = listener
    }

    fun setThemeId(themeId: Int) {
        if (themeId == DEFAULT_THEME) {
            selectedDotColor = ContextCompat.getColor(context, R.color.selectedColor)
            regularDotColor = ContextCompat.getColor(context, R.color.regularColor)
            regularLineColor = ContextCompat.getColor(context, R.color.selectedColor)
        } else {
            selectedDotColor = ContextCompat.getColor(context, R.color.white)
            regularDotColor = ContextCompat.getColor(context, R.color.white)
            regularLineColor = ContextCompat.getColor(context, R.color.white)
        }

        val appTheme = AppTheme.values().find { it.themeId == themeId }
        var isSelector = appTheme?.patterSelector == true

        AppTheme.entries.find { it.themeId == themeId }?.let {
            if (themeId != DEFAULT_THEME) {
                regularLineColor = it.lineColor
                linePaint.color = regularLineColor
            }
            lineWidth = context.dp2px(it.lineWidth)
            linePaint.strokeWidth = lineWidth.toFloat()
        }

        cells.forEachIndexed { index, cell ->
            cell.selectedDotColor = selectedDotColor
            cell.regularDotColor = regularDotColor

            if (isSelector) {
                cell.regularCellBackground = Utils.getPatternDotDrawable(context, themeId, false)
                cell.selectedCellBackground = Utils.getPatternDotDrawable(context, themeId, true)
                cell.isCustomDraw = true
            }
        }
        linePaint.color = regularLineColor
    }

    fun setSateError() {
        if (isSecureMode) {
            reset()
            return
        }
        for (cell in selectedCells) {
            cell.setState(State.ERROR)
            cell.errorDotColor = selectedDotColor
        }
        invalidate()
    }

    fun disableTouch() {
        isCanTouch = false
    }

    fun enableTouch() {
        isCanTouch = true
    }

    fun setCustomTheme(customTheme: CustomTheme) {
        selectedDotColor = customTheme.dotColor
        regularDotColor = ContextCompat.getColor(context, R.color.regularColor)
        regularLineColor = customTheme.lineColor

        cells.forEachIndexed { index, cell ->
            cell.selectedDotColor = selectedDotColor
            cell.regularDotColor = regularDotColor
        }
        linePaint.color = regularLineColor
    }

    companion object {
        const val DEFAULT_RADIUS_RATIO = 0.2f
        const val DEFAULT_LINE_WIDTH = 2f // unit: dp
        const val DEFAULT_SPACING = 24f // unit: dp
        const val DEFAULT_ROW_COUNT = 3
        const val DEFAULT_COLUMN_COUNT = 3
        const val DEFAULT_ERROR_DURATION = 400 // unit: ms
        const val DEFAULT_HIT_AREA_PADDING_RATIO = 0.2f
        const val DEFAULT_INDICATOR_SIZE_RATIO = 0.2f

        const val LINE_STYLE_COMMON = 1
        const val LINE_STYLE_INDICATOR = 2
    }
}