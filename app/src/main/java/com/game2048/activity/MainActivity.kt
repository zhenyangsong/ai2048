package com.game2048.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.game2048.R
import com.game2048.model.Direction
import com.game2048.model.QuestionBank
import com.game2048.view.GameView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        scoreText = findViewById(R.id.scoreText)
        highScoreText = findViewById(R.id.highScoreText)

        gameView.init()
        updateScore()

        gameView.setOnTouchListener(object : OnSwipeListener() {
            override fun onSwipe(direction: Direction) {
                gameView.move(direction)
                gameView.invalidate()
                updateScore()
                checkGameState()
            }
        })

        findViewById<View>(R.id.restartBtn).setOnClickListener {
            gameView.init()
            gameView.invalidate()
            updateScore()
        }

        findViewById<View>(R.id.quizBtn).setOnClickListener {
            showQuiz()
        }
    }

    private fun updateScore() {
        scoreText.text = "分数: ${gameView.getScore()}"
        highScoreText.text = "最高: ${gameView.getHighScore()}"
    }

    private fun checkGameState() {
        // 获胜检测
        if (gameView.isWon()) {
            AlertDialog.Builder(this)
                .setTitle("🎉 恭喜获胜！")
                .setMessage("你达到了 ${gameView.getWinValue()} 分！继续挑战还是结束？")
                .setPositiveButton("继续") { _, _ ->
                    // 继续游戏，清除获胜状态但保留分数
                }
                .setNegativeButton("重来") { _, _ ->
                    gameView.init()
                    gameView.invalidate()
                    updateScore()
                }
                .setCancelable(false)
                .show()
            return
        }

        if (gameView.isGameOver()) {
            AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("得分: ${gameView.getScore()}")
                .setPositiveButton("重来") { _, _ ->
                    gameView.init()
                    gameView.invalidate()
                    updateScore()
                }
                .setCancelable(false)
                .show()
        } else if (gameView.isQuizUnlocked()) {
            showQuizUnlockDialog()
        }
    }

    private fun showQuizUnlockDialog() {
        AlertDialog.Builder(this)
            .setTitle("答题解锁")
            .setMessage("达到128分！回答一道Java题继续游戏")
            .setPositiveButton("开始答题") { _, _ -> showQuiz() }
            .setCancelable(false)
            .show()
    }

    private fun showQuiz() {
        val question = QuestionBank.getRandomQuestion()
        AlertDialog.Builder(this)
            .setTitle("Java基础题 #${question.id}")
            .setMessage(question.question + "\n\n" + question.options.mapIndexed { i, o -> "${i + 1}. $o" }.joinToString("\n"))
            .setItems(question.options.toTypedArray()) { _, which ->
                if (which == question.correctIndex) {
                    // 答对后降低分数，防止无限循环
                    gameView.applyQuizPenalty()
                    gameView.resetQuizUnlocked()
                    gameView.invalidate()
                    updateScore()
                    AlertDialog.Builder(this)
                        .setTitle("正确！✅")
                        .setMessage("分数降低64分，继续游戏吧！")
                        .setPositiveButton("好", null)
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("错误！❌")
                        .setMessage("正确答案是: ${question.options[question.correctIndex]}")
                        .setPositiveButton("重来") { _, _ ->
                            gameView.init()
                            gameView.invalidate()
                            updateScore()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                gameView.resetQuizUnlocked()
            }
            .show()
    }
}

abstract class OnSwipeListener : View.OnTouchListener {
    private val gestureDetector = GestureDetector(object : GestureDetector.SimpleOnGestureListener() {
        private val threshold = 100f
        private val velocityThreshold = 100

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) return false
            val dx = e2.x - e1.x
            val dy = e2.y - e1.y
            if (abs(dx) > abs(dy)) {
                if (abs(dx) > threshold && abs(velocityX) > velocityThreshold) {
                    onSwipe(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                    return true
                }
            } else {
                if (abs(dy) > threshold && abs(velocityY) > velocityThreshold) {
                    onSwipe(if (dy > 0) Direction.DOWN else Direction.UP)
                    return true
                }
            }
            return false
        }
    })

    abstract fun onSwipe(direction: Direction)

    override fun onTouch(v: View, event: MotionEvent) = gestureDetector.onTouchEvent(event)
}
