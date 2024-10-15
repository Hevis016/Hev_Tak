package com.selara.hevtak

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.selara.hevtak.databinding.ActivityMainBinding

class BotGame : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clickButton: MediaPlayer
    private var activePlayer = 1 // 1 - Player 1 (X), 2 - Player 2 (O)
    private var gameState = IntArray(9) { 0 } // 0 - Empty, 1 - X, 2 - O
    private val winningPositions = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )
    private var gameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun playerTap(view: View) {
        if (!gameActive) return

        val tappedImage = view as ImageView
        val tappedIndex = tappedImage.tag.toString().toInt()

        if (gameState[tappedIndex] == 0) {
            gameState[tappedIndex] = activePlayer

            tappedImage.scaleY = 0.9f
            tappedImage.scaleX = 0.9f
            tappedImage.setImageResource(if (activePlayer == 1) R.drawable.krest else R.drawable.circle)
            tappedImage.animate().scaleX(1f).duration = 400
            tappedImage.animate().scaleY(1f).duration = 400
            clickButton = MediaPlayer.create(this, R.raw.click_button)
            clickButton.start()

            if (checkWinner()) {
                gameActive = false
                val winner = if (activePlayer == 1) "Гравець один" else "Гравець два"
                showWinDialog("$winner переміг!")
            } else if (gameState.all { it != 0 }) {
                gameActive = false
                showWinDialog("Нічия!")
            } else {
                activePlayer = if (activePlayer == 1) 2 else 1
                updatePlayerHighlight()

                // Хід бота із затримкою 1 секунда
                if (activePlayer == 2 && gameActive) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        botMove()
                    }, 1000) // Затримка в 1 секунду
                }
            }
        }
    }

    private fun botMove() {
        // Спочатку перевіримо, чи може бот виграти в наступному ході
        for (position in winningPositions) {
            if (gameState[position[0]] == activePlayer &&
                gameState[position[1]] == activePlayer &&
                gameState[position[2]] == 0
            ) {
                makeBotMove(position[2])
                return
            } else if (gameState[position[0]] == activePlayer &&
                gameState[position[2]] == activePlayer &&
                gameState[position[1]] == 0
            ) {
                makeBotMove(position[1])
                return
            } else if (gameState[position[1]] == activePlayer &&
                gameState[position[2]] == activePlayer &&
                gameState[position[0]] == 0
            ) {
                makeBotMove(position[0])
                return
            }
        }

        // Якщо бот не може виграти, перевіримо, чи може гравець 1 виграти, і заблокуємо його
        for (position in winningPositions) {
            if (gameState[position[0]] == 1 &&
                gameState[position[1]] == 1 &&
                gameState[position[2]] == 0
            ) {
                makeBotMove(position[2])
                return
            } else if (gameState[position[0]] == 1 &&
                gameState[position[2]] == 1 &&
                gameState[position[1]] == 0
            ) {
                makeBotMove(position[1])
                return
            } else if (gameState[position[1]] == 1 &&
                gameState[position[2]] == 1 &&
                gameState[position[0]] == 0
            ) {
                makeBotMove(position[0])
                return
            }
        }

        // Якщо середня клітинка порожня, бот займає її
        if (gameState[4] == 0) {
            makeBotMove(4)
            return
        }

        // Якщо кутова клітинка порожня, бот займає її
        val corners = listOf(0, 2, 6, 8)
        for (corner in corners) {
            if (gameState[corner] == 0) {
                makeBotMove(corner)
                return
            }
        }

        // Якщо всі попередні варіанти не підходять, бот робить випадковий хід
        val emptyCells = mutableListOf<Int>()
        for (i in gameState.indices) {
            if (gameState[i] == 0) {
                emptyCells.add(i)
            }
        }

        if (emptyCells.isNotEmpty()) {
            val randomIndex = emptyCells.random()
            makeBotMove(randomIndex)
        }
    }

    private fun makeBotMove(index: Int) {
        gameState[index] = activePlayer

        val imageViews = arrayOf(
            binding.image1,
            binding.image2,
            binding.image3,
            binding.image4,
            binding.image5,
            binding.image6,
            binding.image7,
            binding.image8,
            binding.image9
        )

        imageViews[index].scaleY = 0.9f
        imageViews[index].scaleX = 0.9f
        imageViews[index].setImageResource(R.drawable.circle)
        imageViews[index].animate().scaleX(1f).duration = 400
        imageViews[index].animate().scaleY(1f).duration = 400
        clickButton = MediaPlayer.create(this, R.raw.click_button)
        clickButton.start()

        // Перевіряємо перемогу або нічію
        if (checkWinner()) {
            gameActive = false
            showWinDialog("Гравець два переміг!")
        } else if (gameState.all { it != 0 }) {
            gameActive = false
            showWinDialog("Нічия!")
        } else {
            activePlayer = 1
            updatePlayerHighlight()
        }
    }


    private fun showWinDialog(message: String) {
        val winDialog = WinDialog(this, message, this)
        winDialog.show()
    }

    private fun checkWinner(): Boolean {
        for (position in winningPositions) {
            if (gameState[position[0]] == gameState[position[1]] &&
                gameState[position[1]] == gameState[position[2]] &&
                gameState[position[0]] != 0
            ) {
                return true
            }
        }
        return false
    }

    private fun updatePlayerHighlight() {
        if (activePlayer == 1) {
            binding.PlayerOneLayout.setBackgroundResource(R.drawable.round_back_gray_border2)
            binding.PlayerTwoLayout.setBackgroundResource(R.drawable.round_back_black_gray)
        } else {
            binding.PlayerOneLayout.setBackgroundResource(R.drawable.round_back_black_gray)
            binding.PlayerTwoLayout.setBackgroundResource(R.drawable.round_back_gray_border2)
        }
    }

    fun resetGame(view: View) {
        gameState.fill(0)
        gameActive = true
        activePlayer = 1

        val imageViews = arrayOf(
            binding.image1,
            binding.image2,
            binding.image3,
            binding.image4,
            binding.image5,
            binding.image6,
            binding.image7,
            binding.image8,
            binding.image9
        )

        for (imageView in imageViews) {
            imageView.setImageResource(R.drawable.transparent)
        }

        updatePlayerHighlight()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources for MediaPlayer instances
        clickButton.release()
    }
}


