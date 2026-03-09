package com.game2048.model

import android.content.Context
import android.content.SharedPreferences

class Game2048(val context: Context, val size: Int = 4) {
    private val grid = Array(size) { IntArray(size) }
    var score = 0
        private set
    var highScore = 0
        private set
    var quizUnlocked = false
        private set
    var hasWon = false
        private set

    private val prefs: SharedPreferences = context.getSharedPreferences("game2048", Context.MODE_PRIVATE)

    companion object {
        const val QUIZ_THRESHOLD = 128 // 达到此分数解锁答题
        const val WIN_VALUE = 2048
        private const val QUIZ_PENALTY = 64 // 答对后强制降低的分数
    }

    fun getCell(row: Int, col: Int) = grid[row][col]

    fun init() {
        for (i in 0 until size) grid[i].fill(0)
        score = 0
        quizUnlocked = false
        hasWon = false
        highScore = prefs.getInt("highScore", 0)
        addRandomTile()
        addRandomTile()
    }

    private fun addRandomTile() {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until size) for (j in 0 until size) if (grid[i][j] == 0) empty.add(i to j)
        if (empty.isNotEmpty()) {
            val (r, c) = empty.random()
            grid[r][c] = if (Math.random() < 0.9) 2 else 4
        }
    }

    fun move(direction: Direction): Boolean {
        val oldGrid = grid.map { it.copyOf() }.toTypedArray()
        var moved = false

        when (direction) {
            Direction.UP -> moved = moveUp()
            Direction.DOWN -> moved = moveDown()
            Direction.LEFT -> moved = moveLeft()
            Direction.RIGHT -> moved = moveRight()
        }

        if (moved) {
            addRandomTile()
            if (score > highScore) {
                highScore = score
                prefs.edit().putInt("highScore", highScore).apply()
            }
            // 获胜检测
            if (score >= WIN_VALUE && !hasWon) {
                hasWon = true
            }
            if (score >= QUIZ_THRESHOLD && !quizUnlocked) quizUnlocked = true
        }

        return moved && !oldGrid.contentDeepEquals(grid)
    }

    // 答对题后强制降低分数，防止无限循环
    fun applyQuizPenalty() {
        score = maxOf(0, score - QUIZ_PENALTY)
    }

    private fun moveLeft(): Boolean {
        var moved = false
        for (i in 0 until size) {
            val row = grid[i].filter { it != 0 }.toMutableList()
            for (j in row.indices step 1) {
                if (j + 1 < row.size && row[j] == row[j + 1]) {
                    row[j] *= 2
                    score += row[j]
                    row.removeAt(j + 1)
                }
            }
            val newRow = row + List(size - row.size) { 0 }
            if (!grid[i].contentEquals(newRow)) {
                grid[i] = newRow.toIntArray()
                moved = true
            }
        }
        return moved
    }

    private fun moveRight(): Boolean {
        var moved = false
        for (i in 0 until size) {
            val row = grid[i].filter { it != 0 }.toMutableList().reversed()
            val newRow = mutableListOf<Int>()
            var j = 0
            while (j < row.size) {
                if (j + 1 < row.size && row[j] == row[j + 1]) {
                    newRow.add(row[j] * 2)
                    score += row[j] * 2
                    j += 2
                } else {
                    newRow.add(row[j])
                    j++
                }
            }
            newRow.reverse()
            while (newRow.size < size) newRow.add(0)
            if (!grid[i].contentEquals(newRow.toIntArray())) {
                grid[i] = newRow.toIntArray()
                moved = true
            }
        }
        return moved
    }

    private fun moveUp(): Boolean {
        var moved = false
        for (j in 0 until size) {
            val col = (0 until size).map { grid[it][j] }.filter { it != 0 }.toMutableList()
            for (i in col.indices step 1) {
                if (i + 1 < col.size && col[i] == col[i + 1]) {
                    col[i] *= 2
                    score += col[i]
                    col.removeAt(i + 1)
                }
            }
            val newCol = col + List(size - col.size) { 0 }
            for (i in 0 until size) {
                if (grid[i][j] != newCol[i]) {
                    grid[i][j] = newCol[i]
                    moved = true
                }
            }
        }
        return moved
    }

    private fun moveDown(): Boolean {
        var moved = false
        for (j in 0 until size) {
            val col = (0 until size).map { grid[it][j] }.filter { it != 0 }.toMutableList().reversed()
            val newCol = mutableListOf<Int>()
            var i = 0
            while (i < col.size) {
                if (i + 1 < col.size && col[i] == col[i + 1]) {
                    newCol.add(col[i] * 2)
                    score += col[i] * 2
                    i += 2
                } else {
                    newCol.add(col[i])
                    i++
                }
            }
            newCol.reverse()
            while (newCol.size < size) newCol.add(0)
            for (i in 0 until size) {
                if (grid[i][j] != newCol[i]) {
                    grid[i][j] = newCol[i]
                    moved = true
                }
            }
        }
        return moved
    }

    fun isGameOver(): Boolean {
        for (i in 0 until size) for (j in 0 until size) {
            if (grid[i][j] == 0) return false
            if (j + 1 < size && grid[i][j] == grid[i][j + 1]) return false
            if (i + 1 < size && grid[i][j] == grid[i + 1][j]) return false
        }
        return true
    }

    fun getGrid(): Array<IntArray> = grid

    fun getWinValue() = WIN_VALUE

    fun isWon() = hasWon

    fun resetQuizUnlocked() {
        quizUnlocked = false
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
