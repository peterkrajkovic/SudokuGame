package com.example.sudokugame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var boardSize = 9
    private var sqrtSize = 3
    private var cellSizePixels = 0
    private var selectedRow = -1
    private var selectedColumn = -1
    private var boardNumbers = ArrayList<Pair<Int, Int>?>()
    internal var onTouchListener: OnTouchListener? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(173,216,230)
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.LTGRAY
    }
    private var textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 35f
    }
    private var preDefinedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(235,235,235)
    }
    private var checkedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(231,255,232)
    }
    private var correctCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GREEN
    }
    private var wrongCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = widthMeasureSpec.coerceAtMost(heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / boardSize)
        fillCells(canvas)
        drawLines(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedRow == -1 || selectedColumn == -1 ) return

        for (row in 0 until boardSize) {
            for (column in 0 until boardSize) {
                var conflict = false
                if (row == selectedRow && column == selectedColumn) {
                    fillCell(canvas, row, column, selectedCellPaint)
                } else if (row == selectedRow || column == selectedColumn || (row / sqrtSize == selectedRow / sqrtSize && column / sqrtSize == selectedColumn / sqrtSize)) {
                    fillCell(canvas, row, column, conflictingCellPaint)
                    conflict = true
                }

                if (boardNumbers[row * boardSize + column] != null) {
                    val text = boardNumbers[row * boardSize + column]!!.first.toString()
                    val textX = column * cellSizePixels + (cellSizePixels - textPaint.measureText(text)) / 2
                    val textY = row * cellSizePixels + (cellSizePixels + textPaint.textSize) / 2
                    if (boardNumbers[row * boardSize + column]!!.second == 0 && !conflict) {
                        fillCell(canvas, row, column, preDefinedCellPaint)
                    } else if (boardNumbers[row * boardSize + column]!!.second == 2 && !conflict) {
                        fillCell(canvas, row, column, checkedCellPaint)
                    } else if (boardNumbers[row * boardSize + column]!!.second == 3 && !conflict) {
                        fillCell(canvas, row, column, correctCellPaint)
                    } else if (boardNumbers[row * boardSize + column]!!.second == 4 && !conflict) {
                        fillCell(canvas, row, column, wrongCellPaint)
                    }

                    canvas.drawText(text, textX, textY, textPaint)
                }

            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint) {
        val rect = Rect(column * cellSizePixels, row * cellSizePixels, (column + 1) * cellSizePixels, (row + 1) * cellSizePixels)
        canvas.drawRect(rect, paint)
    }

    private fun drawLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (i in 1 until boardSize) {
            val paint = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(
                i * cellSizePixels.toFloat(),
                0F,
                i * cellSizePixels.toFloat(),
                height.toFloat(),
                paint
            )

            canvas.drawLine(
                0F,
                i * cellSizePixels.toFloat(),
                width.toFloat(),
                i * cellSizePixels.toFloat(),
                paint
            )
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val selectedRow = (y / cellSizePixels).toInt()
        val selectedColumn = (x / cellSizePixels).toInt()
        onTouchListener?.onTouch(selectedRow, selectedColumn)
    }

    fun updateSelectedCell(row: Int, column: Int) {
        selectedRow = row
        selectedColumn = column
        invalidate()
    }

    fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        this.boardNumbers = boardNumbers as ArrayList<Pair<Int, Int>?>
        selectedRow = 10
        selectedColumn = 10
        invalidate()
    }

    interface OnTouchListener {
        fun onTouch(row: Int, column: Int)
    }
}