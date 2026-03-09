package com.game2048.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.game2048.model.Game2048

class GameView @JvmOverloads constructor(
    private val context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val game = Game2048(context)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val bgPaint = Paint()
    private var cellSize = 0f

    private val colors = mapOf(
        0 to Color.parseColor("#CDC1B4"),
        2 to Color.parseColor("#EEE4DA"),
        4 to Color.parseColor("#EDE0C8"),
        8 to Color.parseColor("#F2B179"),
        16 to Color.parseColor("#F59563"),
        32 to Color.parseColor("#F67C5F"),
        64 to Color.parseColor("#F65E3B"),
        128 to Color.parseColor("#EDCF72"),
        256 to Color.parseColor("#EDCC61"),
        512 to Color.parseColor("#EDC850"),
        1024 to Color.parseColor("#EDC53F"),
        2048 to Color.parseColor("#EDC22E")
    )

    fun init() = game.init()
    fun move(dir: com.game2048.model.Direction) = game.move(dir)
    fun getScore() = game.score
    fun getHighScore() = game.highScore
    fun isQuizUnlocked() = game.quizUnlocked
    fun isGameOver() = game.isGameOver()
    fun isWon() = game.isWon()
    fun getWinValue() = game.getWinValue()
    fun applyQuizPenalty() = game.applyQuizPenalty()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellSize = minOf(w, h) / 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val grid = game.getGrid()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val value = grid[i][j]
                val left = j * cellSize + 4
                val top = i * cellSize + 4
                val right = left + cellSize - 8
                val bottom = top + cellSize - 8

                bgPaint.color = colors[value] ?: Color.parseColor("#3C3A32")
                canvas.drawRoundRect(left, top, right, bottom, 8f, 8f, bgPaint)

                if (value > 0) {
                    paint.color = if (value <= 4) Color.parseColor("#776E65") else Color.WHITE
                    paint.textSize = if (value < 100) cellSize / 2.5f else if (value < 1000) cellSize / 3f else cellSize / 4f
                    canvas.drawText(value.toString(), left + cellSize / 2, bottom - cellSize / 4, paint)
                }
            }
        }
    }

    fun resetQuizUnlocked() {
        game.quizUnlocked = false
    }
}
