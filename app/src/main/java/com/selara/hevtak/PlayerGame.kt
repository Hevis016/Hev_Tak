package com.selara.hevtak

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.selara.hevtak.databinding.ActivityMainBinding

class PlayerGame : AppCompatActivity() {

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
            }
        }
    }

    private fun showWinDialog(message: String) {
        val winDialog = WinDialog(this, message, this) // Передача активності як третього параметра
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
